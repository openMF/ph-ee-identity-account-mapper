package org.mifos.identityaccountmapper.api.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.concurrent.ExecutionException;

public interface UpdatePaymentModalityApi {
    @PostMapping("/identityAccountMapper/updatePaymentModality")
    ResponseDTO updatePaymentModality(@RequestHeader(value="X-CallbackURL") String callbackURL,
                                      @RequestBody RequestDTO requestBody) throws ExecutionException, InterruptedException, JsonProcessingException;

}
