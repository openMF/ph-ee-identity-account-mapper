package org.mifos.identityaccountmapper.api.definition;

import java.util.concurrent.ExecutionException;
import org.mifos.identityaccountmapper.data.FetchBeneficiariesResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface FetchBeneficiariesApi {

    @GetMapping("/beneficiaries/{payeeIdentity}")
    ResponseEntity<FetchBeneficiariesResponseDTO> fetchBeneficiary(@PathVariable String payeeIdentity,
            @RequestHeader(value = "X-Registering-Institution-ID") String registeringInstitutionId)
            throws ExecutionException, InterruptedException;

    @GetMapping("/beneficiaries")
    ResponseEntity<Page<FetchBeneficiariesResponseDTO>> fetchAllBeneficiary(
            @RequestHeader(value = "X-Registering-Institution-ID") String registeringInstitutionId,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize)
            throws ExecutionException, InterruptedException;
}
