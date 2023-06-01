package org.mifos.identityaccountmapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.identityaccountmapper.data.BeneficiaryDTO;
import org.mifos.identityaccountmapper.data.CallbackRequestDTO;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.domain.ErrorTracking;
import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.mifos.identityaccountmapper.domain.PaymentModalityDetails;
import org.mifos.identityaccountmapper.exception.PayeeIdentityException;
import org.mifos.identityaccountmapper.repository.ErrorTrackingRepository;
import org.mifos.identityaccountmapper.repository.MasterRepository;
import org.mifos.identityaccountmapper.repository.PaymentModalityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class AddUpdatePaymentModalityService {
    private final MasterRepository masterRepository;
    private final ErrorTrackingRepository errorTrackingRepository;
    private final PaymentModalityRepository paymentModalityRepository;
    private final SendCallbackService sendCallbackService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AddUpdatePaymentModalityService.class);

    @Autowired
    public AddUpdatePaymentModalityService(MasterRepository masterRepository, ErrorTrackingRepository errorTrackingRepository,
                                           PaymentModalityRepository paymentModalityRepository, SendCallbackService sendCallbackService,
                                           ObjectMapper objectMapper){
        this.masterRepository = masterRepository;
        this.errorTrackingRepository = errorTrackingRepository;
        this.paymentModalityRepository = paymentModalityRepository;
        this.sendCallbackService = sendCallbackService;
        this.objectMapper = objectMapper;
    }

    @Async("asyncExecutor")
    public void addPaymentModality(String callbackURL, RequestDTO requestBody){
        List<BeneficiaryDTO> beneficiaryList = requestBody.getBeneficiaries();
        List<ErrorTracking> errorTrackingsList = new ArrayList<>();

        validateAndAddPaymentModality(beneficiaryList, requestBody, errorTrackingsList);
        sendCallback(errorTrackingsList,requestBody.getRequestID(), callbackURL);
    }

    @Async("asyncExecutor")
    public void  updatePaymentModality(String callbackURL,RequestDTO requestBody){
        List<BeneficiaryDTO> beneficiaryList = requestBody.getBeneficiaries();
        List<ErrorTracking> errorTrackingsList = new ArrayList<>();

        validateAndUpdatePaymentModality(beneficiaryList, requestBody, errorTrackingsList);
        sendCallback(errorTrackingsList,requestBody.getRequestID(), callbackURL);
    }

    private void sendCallback(List<ErrorTracking> errorTrackingList, String requestId, String callbackURL){
        CallbackRequestDTO callbackRequest = sendCallbackService.createRequestBody(errorTrackingList,requestId);

        try {
            sendCallbackService.sendCallback(objectMapper.writeValueAsString(callbackRequest), callbackURL);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateAndUpdatePaymentModality(List<BeneficiaryDTO> beneficiaryList, RequestDTO request, List<ErrorTracking> errorTrackingList) {
        beneficiaryList.stream().forEach(beneficiary -> {
            String requestID = request.getRequestID();
            Boolean beneficiaryExists = validateBeneficiary(beneficiary, requestID, errorTrackingList);
            updateModalityDetails(beneficiary, beneficiaryExists, errorTrackingList, requestID, beneficiary.getPayeeIdentity());
        });
    }

    @Transactional
    @CacheEvict(value = "accountLookupCache",key = "#payeeIdentity")
    public void updateModalityDetails(BeneficiaryDTO beneficiary, Boolean beneficiaryExists, List<ErrorTracking> errorTrackingList, String requestID, String payeeIdentity){
        try {
            if(beneficiaryExists){
                PaymentModalityDetails paymentModality = fetchPaymentModalityDetails(beneficiary);

                paymentModality.setModality(beneficiary.getPaymentModality());
                if (beneficiary.getFinancialAddress() != null) {
                    paymentModality.setDestinationAccount(beneficiary.getFinancialAddress());
                }
                if (beneficiary.getBankingInstitutionCode() != null) {
                    paymentModality.setInstitutionCode(beneficiary.getBankingInstitutionCode());
                }
                paymentModalityRepository.save(paymentModality);
            }
        }catch (Exception e){
            saveError(requestID, beneficiary, e.getMessage(), errorTrackingList);
            logger.error(e.getMessage());
        }
    }


    private void validateAndAddPaymentModality(List<BeneficiaryDTO> beneficiaryList, RequestDTO request, List<ErrorTracking> errorTrackingList){
        beneficiaryList.stream().forEach(beneficiary ->{
            String requestID  = request.getRequestID();
            Boolean beneficiaryExists =  validateBeneficiary(beneficiary, requestID, errorTrackingList);
            addModalityDetails(beneficiary, beneficiaryExists, errorTrackingList, requestID, beneficiary.getPayeeIdentity());
        });
    }

    @Transactional
    @CacheEvict(value = "accountLookupCache",key = "#payeeIdentity")
    public void addModalityDetails(BeneficiaryDTO beneficiary, Boolean beneficiaryExists, List<ErrorTracking> errorTrackingList, String requestID, String payeeIdentity){
        try {
            if(beneficiaryExists){
                PaymentModalityDetails paymentModality = fetchPaymentModalityDetails(beneficiary);

                if(!paymentModalityExist(paymentModality, requestID, beneficiary, errorTrackingList)){
                    paymentModality.setModality(beneficiary.getPaymentModality());
                    if (beneficiary.getFinancialAddress() != null) {
                        paymentModality.setDestinationAccount(beneficiary.getFinancialAddress());
                    }
                    if (beneficiary.getBankingInstitutionCode() != null) {
                        paymentModality.setInstitutionCode(beneficiary.getBankingInstitutionCode());
                    }
                    paymentModalityRepository.save(paymentModality);
                }
            }
        }catch (Exception e){
            saveError(requestID, beneficiary, e.getMessage(), errorTrackingList);
            logger.error(e.getMessage());
        }
    }

    private PaymentModalityDetails fetchPaymentModalityDetails(BeneficiaryDTO beneficiary){
        IdentityDetails identityDetails = null;
        try {
            identityDetails = masterRepository.findByPayeeIdentity(beneficiary.getPayeeIdentity()).orElseThrow(()-> PayeeIdentityException.payeeIdentityNotFound(beneficiary.getPayeeIdentity()));
        } catch (PayeeIdentityException e) {
            logger.error(e.getMessage());
        }
        return paymentModalityRepository.findByMasterId(identityDetails.getMasterId()).get(0);
    }
    @Transactional
    private Boolean validateBeneficiary(BeneficiaryDTO beneficiary,String requestID, List<ErrorTracking> errorTrackingList){
        Boolean beneficiaryExists = masterRepository.existsByPayeeIdentity(beneficiary.getPayeeIdentity());
        if(!beneficiaryExists){
            saveError(requestID, beneficiary, "Beneficiary is not registered", errorTrackingList);
        }
        return beneficiaryExists;
    }
    @Transactional
    private Boolean paymentModalityExist(PaymentModalityDetails paymentModality,String requestID, BeneficiaryDTO beneficiary,List<ErrorTracking> errorTrackingList){
        if(paymentModality.getModality() != null){
            saveError(requestID, beneficiary, "Beneficiary already registered with other Modality", errorTrackingList);
            return true;
        }
        return false;
    }
    @Transactional
    private void saveError(String requestID, BeneficiaryDTO beneficiary, String errorDescription, List<ErrorTracking> errorTrackingList){
        try {
            ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(), errorDescription);
            errorTrackingList.add(errorTracking);
            this.errorTrackingRepository.save(errorTracking);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
