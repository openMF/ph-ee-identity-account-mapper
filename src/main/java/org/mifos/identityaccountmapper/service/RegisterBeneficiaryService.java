package org.mifos.identityaccountmapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.mifos.identityaccountmapper.repository.ErrorTrackingRepository;
import org.mifos.identityaccountmapper.repository.MasterRepository;
import org.mifos.identityaccountmapper.repository.PaymentModalityRepository;
import org.mifos.identityaccountmapper.util.UniqueIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterBeneficiaryService {

    private final MasterRepository masterRepository;
    private final ErrorTrackingRepository errorTrackingRepository;
    private final PaymentModalityRepository paymentModalityRepository;
    private final SendCallbackService sendCallbackService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RegisterBeneficiaryService.class);
    @Autowired
    private BeneficiaryValidator beneficiaryValidator;

    @Autowired
    public RegisterBeneficiaryService(MasterRepository masterRepository, ErrorTrackingRepository errorTrackingRepository,
            PaymentModalityRepository paymentModalityRepository, SendCallbackService sendCallbackService, ObjectMapper objectMapper) {
        this.masterRepository = masterRepository;
        this.errorTrackingRepository = errorTrackingRepository;
        this.paymentModalityRepository = paymentModalityRepository;
        this.sendCallbackService = sendCallbackService;
        this.objectMapper = objectMapper;
    }

    public PhErrorDTO registerBeneficiary(String callbackURL, RequestDTO requestBody, String registeringInstitutionId) {
        PhErrorDTO phErrorDTO = beneficiaryValidator.validateCreateBeneficiary(requestBody);
        if (phErrorDTO == null) {
            List<BeneficiaryDTO> beneficiaryList = requestBody.getBeneficiaries();
            List<ErrorTracking> errorTrackingsList = new ArrayList<>();

            validateAndSaveBeneficiaries(beneficiaryList, requestBody, errorTrackingsList, registeringInstitutionId);
            CallbackRequestDTO callbackRequest = sendCallbackService.createRequestBody(errorTrackingsList, requestBody.getRequestID());

            try {
                sendCallbackService.sendCallback(objectMapper.writeValueAsString(callbackRequest), callbackURL);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }
        }
        return phErrorDTO;
    }

    @Transactional
    private void validateAndSaveBeneficiaries(List<BeneficiaryDTO> beneficiariesList, RequestDTO request,
            List<ErrorTracking> errorTrackingList, String registeringInstitutionId) {
        beneficiariesList.stream().forEach(beneficiary -> {
            String requestID = request.getRequestID();
            Boolean beneficiaryExists = validateBeneficiary(beneficiary, requestID, errorTrackingList, registeringInstitutionId);
            try {
                if (!beneficiaryExists) {
                    String masterId = UniqueIDGenerator.generateUniqueNumber(20);
                    IdentityDetails identityDetails = new IdentityDetails(masterId, registeringInstitutionId,
                            LocalDateTime.now(ZoneId.systemDefault()), beneficiary.getPayeeIdentity());
                    this.masterRepository.save(identityDetails);
                    PaymentModalityDetails paymentModalityDetails = new PaymentModalityDetails(masterId, beneficiary.getFinancialAddress(),
                            beneficiary.getPaymentModality(), beneficiary.getBankingInstitutionCode());
                    this.paymentModalityRepository.save(paymentModalityDetails);
                }
            } catch (RuntimeException e) {
                try {
                    ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(),
                            beneficiary.getPaymentModality(), e.getMessage());
                    this.errorTrackingRepository.save(errorTracking);
                } catch (RuntimeException ex) {
                    logger.error(e.getMessage());
                } catch (Exception e2) {
                    logger.error(e2.getMessage());
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
    }

    @Transactional
    private Boolean validateBeneficiary(BeneficiaryDTO beneficiary, String requestID, List<ErrorTracking> errorTrackingList,
            String registeringInstitutionId) {
        Boolean beneficiaryExists = masterRepository.existsByPayeeIdentityAndRegisteringInstitutionId(beneficiary.getPayeeIdentity(),
                registeringInstitutionId);
        try {
            if (beneficiaryExists) {
                ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(),
                        "Beneficiary already registered");
                errorTrackingList.add(errorTracking);
                this.errorTrackingRepository.save(errorTracking);
            } else {
                logger.info("payee Identity {} {}", beneficiary.getPayeeIdentity(), beneficiary.getPayeeIdentity().length());
                if (!beneficiaryValidator.validatePayeeIdentity(beneficiary.getPayeeIdentity())) {
                    ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(),
                            beneficiary.getPaymentModality(), "Payee Identity Invalid");
                    errorTrackingList.add(errorTracking);
                    beneficiaryExists = true;
                } else if (!beneficiaryValidator.validatePayeeIdentity(beneficiary.getPayeeIdentity())) {
                    ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(),
                            beneficiary.getPaymentModality(), "Payee Modality Invalid");
                    errorTrackingList.add(errorTracking);
                    beneficiaryExists = true;
                } else if (!beneficiaryValidator.validateBankingInstitutionCode(beneficiary.getPaymentModality(),
                        beneficiary.getBankingInstitutionCode())) {
                    ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(),
                            beneficiary.getPaymentModality(), "Banking Institution Code Invalid");
                    errorTrackingList.add(errorTracking);
                    beneficiaryExists = true;

                } else if (!beneficiaryValidator.validateFinancialAddress(beneficiary.getFinancialAddress())) {
                    ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(),
                            beneficiary.getPaymentModality(), "Financial Address Invalid");
                    errorTrackingList.add(errorTracking);
                    beneficiaryExists = true;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return beneficiaryExists;
    }
}
