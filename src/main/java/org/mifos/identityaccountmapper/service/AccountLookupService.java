package org.mifos.identityaccountmapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.mifos.identityaccountmapper.domain.PaymentModalityDetails;
import org.mifos.identityaccountmapper.exception.AccountValidationException;
import org.mifos.identityaccountmapper.exception.PayeeIdentityException;
import org.mifos.identityaccountmapper.repository.ErrorTrackingRepository;
import org.mifos.identityaccountmapper.repository.MasterRepository;
import org.mifos.identityaccountmapper.repository.PaymentModalityRepository;
import org.mifos.pheeidaccountvalidatorimpl.service.AccountValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.getKeyByValue;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.getValueByKey;

@Service
public class AccountLookupService {
    private final MasterRepository masterRepository;
    private final ErrorTrackingRepository errorTrackingRepository;
    private final PaymentModalityRepository paymentModalityRepository;
    private final SendCallbackService sendCallbackService;
    private final ObjectMapper objectMapper;
    private final AccountLookupReadService accountLookupReadService;
    @Value("${account_validation_enabled}")
    private Boolean accountValidationEnabled;
    @Value("${account_validator_connector}")
    private  String accountValidatorConnector;
    @Value("${callback_enabled}")
    private  Boolean callbackEnabled;
    @Autowired
    private ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(AccountLookupService.class);
    private final Set<String> paymentModalityCodes =  new HashSet<>(Set.of("00", "01","02"));

    @Autowired
    public AccountLookupService(MasterRepository masterRepository, ErrorTrackingRepository errorTrackingRepository,
                                PaymentModalityRepository paymentModalityRepository, SendCallbackService sendCallbackService,
                                ObjectMapper objectMapper, AccountLookupReadService accountLookupReadService){
        this.masterRepository = masterRepository;
        this.errorTrackingRepository = errorTrackingRepository;
        this.paymentModalityRepository = paymentModalityRepository;
        this.sendCallbackService = sendCallbackService;
        this.objectMapper = objectMapper;
        this.accountLookupReadService = accountLookupReadService;
    }
    @Async("asyncExecutor")
    public void accountLookup(String callbackURL,String payeeIdentity, String paymentModality, String requestId, String registeringInstitutionId){
        IdentityDetails identityDetails = masterRepository.findByPayeeIdentityAndRegisteringInstitutionId(payeeIdentity, registeringInstitutionId).orElseThrow(()-> PayeeIdentityException.payeeIdentityNotFound(payeeIdentity));
        if(!identityDetails.getRegisteringInstitutionId().matches(registeringInstitutionId)){
            sendCallbackService.sendCallback("Registering Institution Id is not mapped to the Payee Identity provided in the request.", callbackURL);
            return;
        }
        PaymentModalityDetails paymentModalityDetails = paymentModalityRepository.findByMasterId(identityDetails.getMasterId()).get(0);
        if(!paymentModalityCodes.contains(paymentModality)){
            paymentModality = getValueByKey(paymentModality);
        }

        if(accountValidationEnabled) {
            AccountValidationService accountValidationService = null;
            try {
                accountValidationService = (AccountValidationService) this.applicationContext.getBean(accountValidatorConnector);
            } catch (NoSuchBeanDefinitionException ex) {
                // Handle the case when the bean is not found in the application context
            }
            assert accountValidationService != null;
            Boolean accountValidate = accountValidationService.validateAccount(paymentModalityDetails.getDestinationAccount(),
                    paymentModalityDetails.getInstitutionCode(), fetchPaymentModality(paymentModality),payeeIdentity, callbackURL);
            if(callbackEnabled) {
                sendAccountLookupCallback(callbackURL, accountValidate, payeeIdentity, requestId, registeringInstitutionId);
            }
        }
        else{
            sendAccountLookupCallback(callbackURL, true, payeeIdentity, requestId, registeringInstitutionId);
        }
    }

    public void sendAccountLookupCallback(String callbackURL, Boolean accountValidate, String payeeIdentity, String requestId, String registeringInstitutionId){
        try {
            if(accountValidate) {
                sendCallbackService.sendCallback(objectMapper.writeValueAsString(accountLookupReadService.lookup(payeeIdentity, callbackURL, requestId, registeringInstitutionId)), callbackURL);
            }else {
                sendCallbackService.sendCallback("Account Validation Failed", callbackURL);
                throw AccountValidationException.accountvalidationFailed(payeeIdentity);
            }
        } catch (JsonProcessingException | RuntimeException e) {
            logger.error(e.getMessage());
        }
    }
    public String fetchPaymentModality(String paymentModality){
        return getKeyByValue(paymentModality);
    }
}
