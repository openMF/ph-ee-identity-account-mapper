package org.mifos.identityaccountmapper.api.implementation;

import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.mifos.identityaccountmapper.api.definition.FetchBeneficiariesApi;
import org.mifos.identityaccountmapper.data.FetchBeneficiariesResponseDTO;
import org.mifos.identityaccountmapper.service.FetchBeneficiariesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FetchBeneficiariesApiController implements FetchBeneficiariesApi {

    @Autowired
    FetchBeneficiariesService fetchBeneficiariesService;

    @Override
    public ResponseEntity<FetchBeneficiariesResponseDTO> fetchBeneficiary(String payeeIdentity, String registeringInstitutionId)
            throws ExecutionException, InterruptedException {
        if (StringUtils.isBlank(registeringInstitutionId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        FetchBeneficiariesResponseDTO fetchBeneficiariesResponseDTO = fetchBeneficiariesService.fetchBeneficiary(payeeIdentity,
                registeringInstitutionId);
        return ResponseEntity.status(HttpStatus.OK).body(fetchBeneficiariesResponseDTO);
    }

    @Override
    public ResponseEntity<Page<FetchBeneficiariesResponseDTO>> fetchAllBeneficiary(String registeringInstitutionId, Integer page,
            Integer pageSize) throws ExecutionException, InterruptedException {
        if (StringUtils.isBlank(registeringInstitutionId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Page<FetchBeneficiariesResponseDTO> fetchBeneficiariesResponseDTO = fetchBeneficiariesService.fetchAllBeneficiaries(page, pageSize,
                registeringInstitutionId);
        return ResponseEntity.status(HttpStatus.OK).body(fetchBeneficiariesResponseDTO);
    }
}
