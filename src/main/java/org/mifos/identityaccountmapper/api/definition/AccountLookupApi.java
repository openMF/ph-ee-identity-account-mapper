package org.mifos.identityaccountmapper.api.definition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.ExecutionException;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GOV")
public interface AccountLookupApi {

    @Operation(summary = "Account Lookup API")
    @GetMapping("/beneficiary")

    ResponseEntity<ResponseDTO> accountLookup(@RequestHeader(value = "X-CallbackURL") String callbackURL,
            @RequestParam(value = "payeeIdentity") String payeeIdentity, @RequestParam(value = "paymentModality") String paymentModality,
            @RequestParam(value = "requestId") String requestId,
            @RequestHeader(value = "X-Registering-Institution-ID") String registeringInstitutionId)
            throws ExecutionException, InterruptedException;

}
