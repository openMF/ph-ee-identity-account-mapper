package org.mifos.identityaccountmapper.api.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.api.definition.UpdatePaymentModalityApi;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class UpdatePaymentModalityApiController implements UpdatePaymentModalityApi {
    @Override
    public ResponseDTO updatePaymentModality(String callbackURL, RequestDTO requestBody) throws ExecutionException, InterruptedException, JsonProcessingException {
        return null;
    }
}
