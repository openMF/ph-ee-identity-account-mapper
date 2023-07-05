package org.mifos.identityaccountmapper.api.implementation;

import org.mifos.identityaccountmapper.api.definition.AccountLookupApi;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.AccountLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.*;

@RestController
public class AccountLookupApiController implements AccountLookupApi {
    @Autowired
    AccountLookupService accountLookupService;

    @Override
    public ResponseEntity<ResponseDTO> accountLookup(String callbackURL, String payeeIdentity, String paymentModality, String requestId, String registeringInstitutionId){
        try {
            accountLookupService.accountLookup(callbackURL,payeeIdentity, paymentModality, requestId, registeringInstitutionId);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(), requestId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(), requestId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
    }
}
