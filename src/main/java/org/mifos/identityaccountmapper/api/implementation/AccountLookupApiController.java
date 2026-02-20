package org.mifos.identityaccountmapper.api.implementation;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.FAILED_RESPONSE_CODE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.FAILED_RESPONSE_MESSAGE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_CODE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_MESSAGE;

import lombok.extern.slf4j.Slf4j;
import org.mifos.identityaccountmapper.api.definition.AccountLookupApi;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.AccountLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AccountLookupApiController implements AccountLookupApi {

    @Autowired
    AccountLookupService accountLookupService;

    @Value("${isExternalLookup}")
    Boolean isExternalLookup;

    @Override
    public <T> ResponseEntity<T> accountLookup(String callbackURL, String payeeIdentity, String paymentModality, String requestId,
            String registeringInstitutionId) {
        log.info("TDDEBUG> Inside account lookup API controller");
        try {
            log.info("TDDEBUG> Calling account lookup service from API controller callbackURL: " + callbackURL + ", payeeIdentity: "
                    + payeeIdentity + ", paymentModality: " + paymentModality + ", requestId: " + requestId + ", registeringInstitutionId: "
                    + registeringInstitutionId);
            accountLookupService.accountLookup(callbackURL, payeeIdentity, paymentModality, requestId, registeringInstitutionId);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(), requestId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((T) responseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(), requestId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body((T) responseDTO);
    }
}
