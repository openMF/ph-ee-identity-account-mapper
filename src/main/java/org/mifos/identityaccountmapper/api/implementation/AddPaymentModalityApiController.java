package org.mifos.identityaccountmapper.api.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.api.definition.AddPaymentModalityApi;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class AddPaymentModalityApiController implements AddPaymentModalityApi {

    @Override
    public ResponseDTO addPaymentModality(String callbackURL, RequestDTO requestBody) throws ExecutionException, InterruptedException, JsonProcessingException {
        return null;
    }
}
