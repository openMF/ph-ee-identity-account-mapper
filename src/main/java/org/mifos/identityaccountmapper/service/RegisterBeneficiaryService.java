package org.mifos.identityaccountmapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.identityaccountmapper.data.BeneficiaryDTO;
import org.mifos.identityaccountmapper.data.CallbackRequestDTO;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.domain.ErrorTracking;
import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.mifos.identityaccountmapper.domain.PaymentModalityDetails;
import org.mifos.identityaccountmapper.repository.ErrorTrackingRepository;
import org.mifos.identityaccountmapper.repository.MasterRepository;
import org.mifos.identityaccountmapper.repository.PaymentModalityRepository;
import org.mifos.identityaccountmapper.util.UniqueIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterBeneficiaryService {

    private final MasterRepository masterRepository;
    private final ErrorTrackingRepository errorTrackingRepository;
    private final PaymentModalityRepository paymentModalityRepository;
    private final SendCallbackService sendCallbackService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RegisterBeneficiaryService.class);

    @Autowired
    public RegisterBeneficiaryService(MasterRepository masterRepository, ErrorTrackingRepository errorTrackingRepository,
                                       PaymentModalityRepository paymentModalityRepository, SendCallbackService sendCallbackService,
                                      ObjectMapper objectMapper){
        this.masterRepository = masterRepository;
        this.errorTrackingRepository = errorTrackingRepository;
        this.paymentModalityRepository = paymentModalityRepository;
        this.sendCallbackService = sendCallbackService;
        this.objectMapper = objectMapper;
    }


    @Async("asyncExecutor")
    public void registerBeneficiary(String callbackURL, RequestDTO requestBody, String registeringInstitutionId) {
        List<BeneficiaryDTO> beneficiaryList = requestBody.getBeneficiaries();
        List<ErrorTracking> errorTrackingsList = new ArrayList<>();

        validateAndSaveBeneficiaries(beneficiaryList, requestBody, errorTrackingsList, registeringInstitutionId);
        CallbackRequestDTO callbackRequest = sendCallbackService.createRequestBody(errorTrackingsList,requestBody.getRequestID());

        try {
            sendCallbackService.sendCallback(objectMapper.writeValueAsString(callbackRequest), callbackURL);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    private void validateAndSaveBeneficiaries(List<BeneficiaryDTO> beneficiariesList, RequestDTO request, List<ErrorTracking> errorTrackingList, String registeringInstitutionId){
        beneficiariesList.stream().forEach(beneficiary ->{
            String requestID  = request.getRequestID();
            Boolean beneficiaryExists =  validateBeneficiary(beneficiary, requestID, errorTrackingList);
            try {
                if (!beneficiaryExists) {
                    String masterId = UniqueIDGenerator.generateUniqueNumber(20);
                    IdentityDetails identityDetails = new IdentityDetails(masterId, registeringInstitutionId, LocalDateTime.now(), beneficiary.getPayeeIdentity());
                    this.masterRepository.save(identityDetails);
                    PaymentModalityDetails paymentModalityDetails = new PaymentModalityDetails(masterId, beneficiary.getFinancialAddress(), beneficiary.getPaymentModality(), beneficiary.getBankingInstitutionCode());
                    this.paymentModalityRepository.save(paymentModalityDetails);
                }
            }catch (RuntimeException e) {
                try {
                    ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(), e.getMessage());
                    this.errorTrackingRepository.save(errorTracking);
                } catch (RuntimeException ex) {
                    logger.error(e.getMessage());
                } catch (Exception e2) {
                    logger.error(e2.getMessage());
                }
            }catch(Exception e){
                logger.error(e.getMessage());
            }
        });
    }

    @Transactional
    private Boolean validateBeneficiary(BeneficiaryDTO beneficiary, String requestID, List<ErrorTracking> errorTrackingList){
        Boolean beneficiaryExists = masterRepository.existsByPayeeIdentity(beneficiary.getPayeeIdentity());
        try{
            if(beneficiaryExists){
                ErrorTracking  errorTracking= new ErrorTracking(requestID,beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(),"Beneficiary already registered");
                errorTrackingList.add(errorTracking);
                this.errorTrackingRepository.save(errorTracking);
            }
        }
        catch(Exception e){
            logger.error(e.getMessage());
        }
        return beneficiaryExists;
    }
}
