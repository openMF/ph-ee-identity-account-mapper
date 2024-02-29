package org.mifos.identityaccountmapper.service;

import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.ACCOUNT_ID;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.MSISDN;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.VIRTUAL_ADDRESS;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.VOUCHER;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.WALLET_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
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

@Service
public class AddUpdatePaymentModalityService {

    private final MasterRepository masterRepository;
    private final ErrorTrackingRepository errorTrackingRepository;
    private final PaymentModalityRepository paymentModalityRepository;
    private final SendCallbackService sendCallbackService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AddUpdatePaymentModalityService.class);
    private BeneficiaryValidator beneficiaryValidator;

    @Autowired
    public AddUpdatePaymentModalityService(MasterRepository masterRepository, ErrorTrackingRepository errorTrackingRepository,
            PaymentModalityRepository paymentModalityRepository, SendCallbackService sendCallbackService, ObjectMapper objectMapper) {
        this.masterRepository = masterRepository;
        this.errorTrackingRepository = errorTrackingRepository;
        this.paymentModalityRepository = paymentModalityRepository;
        this.sendCallbackService = sendCallbackService;
        this.objectMapper = objectMapper;
    }

    @Async("asyncExecutor")
    public void addPaymentModality(String callbackURL, RequestDTO requestBody, String registeringInstitutionId) {
        List<BeneficiaryDTO> beneficiaryList = requestBody.getBeneficiaries();
        List<ErrorTracking> errorTrackingsList = new ArrayList<>();

        validateAndAddPaymentModality(beneficiaryList, requestBody, errorTrackingsList, registeringInstitutionId);
        sendCallback(errorTrackingsList, requestBody.getRequestID(), callbackURL);
    }

    public PhErrorDTO updatePaymentModality(String callbackURL, RequestDTO requestBody, String registeringInstitutionId) {
        PhErrorDTO phErrorDTO = beneficiaryValidator.validateCreateBeneficiary(requestBody);
        if (phErrorDTO == null) {
            List<BeneficiaryDTO> beneficiaryList = requestBody.getBeneficiaries();
            List<ErrorTracking> errorTrackingsList = new ArrayList<>();

            validateAndUpdatePaymentModality(beneficiaryList, requestBody, errorTrackingsList, registeringInstitutionId);
            sendCallback(errorTrackingsList, requestBody.getRequestID(), callbackURL);

        }
        return phErrorDTO;
    }

    private void sendCallback(List<ErrorTracking> errorTrackingList, String requestId, String callbackURL) {
        CallbackRequestDTO callbackRequest = sendCallbackService.createRequestBody(errorTrackingList, requestId);

        try {
            sendCallbackService.sendCallback(objectMapper.writeValueAsString(callbackRequest), callbackURL);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateAndUpdatePaymentModality(List<BeneficiaryDTO> beneficiaryList, RequestDTO request,
            List<ErrorTracking> errorTrackingList, String registeringInstitutionId) {
        beneficiaryList.stream().forEach(beneficiary -> {
            String requestID = request.getRequestID();
            Boolean beneficiaryExists = validateBeneficiary(beneficiary, requestID, errorTrackingList, registeringInstitutionId);
            updateModalityDetails(beneficiary, beneficiaryExists, errorTrackingList, requestID, beneficiary.getPayeeIdentity(),
                    registeringInstitutionId);
        });
    }

    @Transactional
    @CacheEvict(value = "accountLookupCache", key = "#payeeIdentity")
    public void updateModalityDetails(BeneficiaryDTO beneficiary, Boolean beneficiaryExists, List<ErrorTracking> errorTrackingList,
            String requestID, String payeeIdentity, String registeringInstitutionId) {
        try {
            if (beneficiaryExists) {
                PaymentModalityDetails paymentModality = fetchPaymentModalityDetails(beneficiary, registeringInstitutionId);

                paymentModality.setModality(beneficiary.getPaymentModality());
                if (beneficiary.getFinancialAddress() != null) {
                    paymentModality.setDestinationAccount(beneficiary.getFinancialAddress());
                }
                if (beneficiary.getBankingInstitutionCode() != null) {
                    paymentModality.setInstitutionCode(beneficiary.getBankingInstitutionCode());
                }
                paymentModalityRepository.save(paymentModality);
            }
        } catch (Exception e) {
            saveError(requestID, beneficiary, e.getMessage(), errorTrackingList);
            logger.error(e.getMessage());
        }
    }

    private void validateAndAddPaymentModality(List<BeneficiaryDTO> beneficiaryList, RequestDTO request,
            List<ErrorTracking> errorTrackingList, String registeringInstitutionId) {
        beneficiaryList.stream().forEach(beneficiary -> {
            String requestID = request.getRequestID();
            Boolean beneficiaryExists = validateBeneficiary(beneficiary, requestID, errorTrackingList, registeringInstitutionId);
            addModalityDetails(beneficiary, beneficiaryExists, errorTrackingList, requestID, beneficiary.getPayeeIdentity(),
                    registeringInstitutionId);
        });
    }

    @Transactional
    @CacheEvict(value = "accountLookupCache", key = "#payeeIdentity")
    public void addModalityDetails(BeneficiaryDTO beneficiary, Boolean beneficiaryExists, List<ErrorTracking> errorTrackingList,
            String requestID, String payeeIdentity, String registeringInstitutionId) {
        try {
            if (beneficiaryExists) {
                PaymentModalityDetails paymentModality = fetchPaymentModalityDetails(beneficiary, registeringInstitutionId);

                if (!paymentModalityExist(paymentModality, requestID, beneficiary, errorTrackingList)) {
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
        } catch (Exception e) {
            saveError(requestID, beneficiary, e.getMessage(), errorTrackingList);
            logger.error(e.getMessage());
        }
    }

    private PaymentModalityDetails fetchPaymentModalityDetails(BeneficiaryDTO beneficiary, String registeringInstitutionId) {
        IdentityDetails identityDetails = null;
        try {
            identityDetails = masterRepository
                    .findByPayeeIdentityAndRegisteringInstitutionId(beneficiary.getPayeeIdentity(), registeringInstitutionId)
                    .orElseThrow(() -> PayeeIdentityException.payeeIdentityNotFound(beneficiary.getPayeeIdentity()));
        } catch (PayeeIdentityException e) {
            logger.error(e.getMessage());
        }
        return paymentModalityRepository.findByMasterId(identityDetails.getMasterId()).get(0);
    }

    @Transactional
    private Boolean validateBeneficiary(BeneficiaryDTO beneficiary, String requestID, List<ErrorTracking> errorTrackingList,
            String registeringInstitutionId) {
        Boolean beneficiaryExists = masterRepository.existsByPayeeIdentityAndRegisteringInstitutionId(beneficiary.getPayeeIdentity(),
                registeringInstitutionId);
        if (!beneficiaryExists) {
            saveError(requestID, beneficiary, "Beneficiary is not registered", errorTrackingList);
        } else if (beneficiary.getPayeeIdentity() != null && (!beneficiary.getPayeeIdentity().isEmpty()
                || beneficiary.getPayeeIdentity().length() > 0 && beneficiary.getPayeeIdentity().length() <= 12)) {
            ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(),
                    "Payee Identity Invalid");
            errorTrackingList.add(errorTracking);
            beneficiaryExists = false;
        } else if (!(beneficiary.getPaymentModality().equals(ACCOUNT_ID.getValue())
                || beneficiary.getPaymentModality().equals(MSISDN.getValue())
                || beneficiary.getPaymentModality().equals(VIRTUAL_ADDRESS.getValue())
                || beneficiary.getPaymentModality().equals(WALLET_ID.getValue())
                || beneficiary.getPaymentModality().equals(VOUCHER.getValue()))) {
            ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(),
                    "Payee Modality Invalid");
            errorTrackingList.add(errorTracking);
            beneficiaryExists = false;
        } else if (beneficiary.getFinancialAddress() != null && beneficiary.getFinancialAddress().isEmpty()
                || beneficiary.getFinancialAddress().length() > 30) {
            ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(),
                    "Financial Address Invalid");
            errorTrackingList.add(errorTracking);
            beneficiaryExists = false;
        } else {
            IdentityDetails identityDetails = masterRepository
                    .findByPayeeIdentityAndRegisteringInstitutionId(beneficiary.getPayeeIdentity(), registeringInstitutionId)
                    .orElseThrow(() -> PayeeIdentityException.payeeIdentityNotFound(beneficiary.getPayeeIdentity()));
            if (!identityDetails.getRegisteringInstitutionId().equals(registeringInstitutionId)) {
                saveError(requestID, beneficiary, "Registering Institution Id Mismatch", errorTrackingList);
                return false;
            }
        }
        return beneficiaryExists;
    }

    @Transactional
    private Boolean paymentModalityExist(PaymentModalityDetails paymentModality, String requestID, BeneficiaryDTO beneficiary,
            List<ErrorTracking> errorTrackingList) {
        if (paymentModality.getModality() != null) {
            saveError(requestID, beneficiary, "Beneficiary already registered with other Modality", errorTrackingList);
            return true;
        }
        return false;
    }

    @Transactional
    private void saveError(String requestID, BeneficiaryDTO beneficiary, String errorDescription, List<ErrorTracking> errorTrackingList) {
        try {
            ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(),
                    errorDescription);
            errorTrackingList.add(errorTracking);
            this.errorTrackingRepository.save(errorTracking);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
