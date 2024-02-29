package org.mifos.identityaccountmapper.api.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.ExecutionException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface UpdateBeneficiaryApi {

    @PutMapping("/beneficiary")
    <T> ResponseEntity<T> registerBeneficiary(@RequestHeader(value = "X-CallbackURL") String callbackURL,
            @RequestHeader(value = "X-Registering-Institution-ID") String registeringInstitutionId, @RequestBody RequestDTO requestBody)
            throws ExecutionException, InterruptedException, JsonProcessingException;
}
