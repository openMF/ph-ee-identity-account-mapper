package org.mifos.identityaccountmapper.api.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.api.definition.AddPaymentModalityApi;
import org.mifos.identityaccountmapper.service.AddUpdatePaymentModalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.*;

@RestController
public class AddPaymentModalityApiController implements AddPaymentModalityApi {
    @Autowired
    AddUpdatePaymentModalityService addUpdatePaymentModalityService;

    @Override
    public ResponseDTO addPaymentModality(String callbackURL, RequestDTO requestBody) throws ExecutionException, InterruptedException, JsonProcessingException {
        try {
            addUpdatePaymentModalityService.addPaymentModality(callbackURL, requestBody);
        } catch (Exception e) {
            return new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(), requestBody.getRequestID());

        }
        return new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(), requestBody.getRequestID());
    }
}
