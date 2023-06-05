package org.mifos.identityaccountmapper.api.implementation;

import org.mifos.identityaccountmapper.api.definition.AccountLookupApi;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.AccountLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.*;

@RestController
public class AccountLookupApiController implements AccountLookupApi {
    @Autowired
    AccountLookupService accountLookupService;

    @Override
    public ResponseDTO accountLookup(String callbackURL, String payeeIdentity, String paymentModality, String requestId){
        try {
            accountLookupService.accountLookup(callbackURL,payeeIdentity, paymentModality, requestId);
        } catch (Exception e) {
            return new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(), requestId);

        }
        return new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(), requestId);
    }
}
