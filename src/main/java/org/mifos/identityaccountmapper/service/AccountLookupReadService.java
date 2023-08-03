package org.mifos.identityaccountmapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.identityaccountmapper.data.AccountLookupResponseDTO;
import org.mifos.identityaccountmapper.data.PaymentModalityDTO;
import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.mifos.identityaccountmapper.domain.PaymentModalityDetails;
import org.mifos.identityaccountmapper.repository.ErrorTrackingRepository;
import org.mifos.identityaccountmapper.repository.MasterRepository;
import org.mifos.identityaccountmapper.repository.PaymentModalityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountLookupReadService {
    private final MasterRepository masterRepository;
    private final ErrorTrackingRepository errorTrackingRepository;
    private final PaymentModalityRepository paymentModalityRepository;
    private final SendCallbackService sendCallbackService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AccountLookupReadService.class);


    @Autowired
    public AccountLookupReadService(MasterRepository masterRepository, ErrorTrackingRepository errorTrackingRepository,
                                     PaymentModalityRepository paymentModalityRepository, SendCallbackService sendCallbackService,
                                    ObjectMapper objectMapper){
        this.masterRepository = masterRepository;
        this.errorTrackingRepository = errorTrackingRepository;
        this.paymentModalityRepository = paymentModalityRepository;
        this.sendCallbackService = sendCallbackService;
        this.objectMapper = objectMapper;
    }
    @Cacheable(value = "accountLookupCache",key = "#payeeIdentity")
    public AccountLookupResponseDTO lookup(String payeeIdentity, String callbackURL, String requestId, String registeringInstitutionId){
        IdentityDetails identityDetails = null;
        List<PaymentModalityDetails> paymentModalityDetails = new ArrayList<>();
        try{
            identityDetails = masterRepository
                    .findByPayeeIdentityAndRegisteringInstitutionId(payeeIdentity, registeringInstitutionId)
                    .orElseThrow(()-> new RuntimeException("Payee Identity does not exist."));
            paymentModalityDetails = paymentModalityRepository.findByMasterId(identityDetails.getMasterId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            try {
                sendCallbackService.sendCallback(objectMapper.writeValueAsString(e.getMessage()), callbackURL);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        return createResponseDTO(paymentModalityDetails, identityDetails, requestId);
    }

    private AccountLookupResponseDTO createResponseDTO(List<PaymentModalityDetails> paymentModalityDetailsList, IdentityDetails identityDetails, String requestId){
        List<PaymentModalityDTO> paymentModalityList = new ArrayList<>();
        for(PaymentModalityDetails paymentModalityDetails: paymentModalityDetailsList){
            paymentModalityList.add(new PaymentModalityDTO(paymentModalityDetails.getModality(), paymentModalityDetails.getDestinationAccount(), paymentModalityDetails.getInstitutionCode()));
        }
        return new AccountLookupResponseDTO(requestId, identityDetails.getPayeeIdentity(),paymentModalityList);
    }
}
