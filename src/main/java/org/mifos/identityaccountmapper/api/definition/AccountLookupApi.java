package org.mifos.identityaccountmapper.api.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

public interface AccountLookupApi {
    @GetMapping("/beneficiary")
    ResponseEntity<ResponseDTO> accountLookup(@RequestHeader(value="X-CallbackURL") String callbackURL, @RequestParam(value = "payeeIdentity") String payeeIdentity,
                                              @RequestParam(value = "paymentModality", required = false) String paymentModality, @RequestParam(value = "requestId") String requestId, @RequestHeader(value = "X-Registering-Institution-ID") String registeringInstitutionId) throws ExecutionException, InterruptedException;
}
