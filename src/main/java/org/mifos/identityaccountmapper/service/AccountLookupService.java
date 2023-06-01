package org.mifos.identityaccountmapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.identityaccountmapper.data.BeneficiaryDTO;
import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.mifos.identityaccountmapper.domain.PaymentModalityDetails;
import org.mifos.identityaccountmapper.exception.PayeeIdentityException;
import org.mifos.identityaccountmapper.repository.ErrorTrackingRepository;
import org.mifos.identityaccountmapper.repository.MasterRepository;
import org.mifos.identityaccountmapper.repository.PaymentModalityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AccountLookupService {
    private final MasterRepository masterRepository;
    private final ErrorTrackingRepository errorTrackingRepository;
    private final PaymentModalityRepository paymentModalityRepository;
    private final SendCallbackService sendCallbackService;
    private final ObjectMapper objectMapper;
    private final AccountLookupReadService accountLookupReadService;
    private static final Logger logger = LoggerFactory.getLogger(AccountLookupService.class);

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
    public void accountLookup(String callbackURL,String payeeIdentity){

        try {
            sendCallbackService.sendCallback(objectMapper.writeValueAsString(accountLookupReadService.lookup(payeeIdentity, callbackURL)), callbackURL);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }

    }
}
