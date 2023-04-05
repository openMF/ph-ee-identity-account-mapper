package org.mifos.identityaccountmapper.api.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.RegisterBeneficiaryService;
import org.mifos.identityaccountmapper.api.definition.RegisterBeneficiaryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class RegisterBeneficiaryApiController implements RegisterBeneficiaryApi {
    @Autowired
    RegisterBeneficiaryService registerBeneficiaryService;

    @Override
    public ResponseDTO registerBeneficiary(String callbackURL, RequestDTO requestBody) throws ExecutionException, InterruptedException, JsonProcessingException {
        try {
            registerBeneficiaryService.registerBeneficiary(callbackURL, requestBody);
        }catch (Exception e){
            return new ResponseDTO("10", "Request not acknowledged by Pay-BB", requestBody.getRequestID());

        }
        return new ResponseDTO("20", "Request successfully received by Pay-BB", requestBody.getRequestID());
    }
}
