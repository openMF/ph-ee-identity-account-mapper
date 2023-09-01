package org.mifos.identityaccountmapper.api.definition;

import org.mifos.identityaccountmapper.data.BatchAccountLookupRequestDTO;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

public interface BatchAccountLookupApi {
    @PutMapping("/beneficiary")
    ResponseEntity<ResponseDTO> batchAccountLookup(@RequestHeader(value="X-CallbackURL") String callbackURL,
                                                   @RequestBody BatchAccountLookupRequestDTO requestBody,
                                                   @RequestHeader(value = "X-Registering-Institution-ID") String registeringInstitutionId) throws ExecutionException, InterruptedException;
}
