package org.mifos.identityaccountmapper.api.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.concurrent.ExecutionException;

public interface RegisterBeneficiaryApi {
    @PostMapping("/beneficiary")
    ResponseEntity<ResponseDTO> registerBeneficiary(@RequestHeader(value="X-CallbackURL") String callbackURL, @RequestHeader(value = "registeringInstitutionId") String registeringInstitutionId,
                                                    @RequestBody RequestDTO requestBody) throws ExecutionException, InterruptedException, JsonProcessingException;

}
