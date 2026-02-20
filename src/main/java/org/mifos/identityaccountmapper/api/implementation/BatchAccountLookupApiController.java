package org.mifos.identityaccountmapper.api.implementation;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.FAILED_RESPONSE_CODE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.FAILED_RESPONSE_MESSAGE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_CODE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_MESSAGE;

import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.mifos.identityaccountmapper.api.definition.BatchAccountLookupApi;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.AccountLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BatchAccountLookupApiController implements BatchAccountLookupApi {

    @Autowired
    AccountLookupService accountLookupService;

    @Override
    public ResponseEntity<ResponseDTO> batchAccountLookup(String callbackURL, RequestDTO requestDTO, String registeringInstitutionId)
            throws ExecutionException, InterruptedException {
        log.info("=== IDENTITY MAPPER BATCH LOOKUP DEBUG ===");
        log.info("Callback URL: {}", callbackURL);
        log.info("Request ID: {}", requestDTO.getRequestID());
        log.info("Registering Institution ID: {}", registeringInstitutionId);
        log.info("SourceBBID: {}", requestDTO.getSourceBBID());
        log.info("Number of beneficiaries in request: {}",
                requestDTO.getBeneficiaries() != null ? requestDTO.getBeneficiaries().size() : 0);
        if (requestDTO.getBeneficiaries() != null && !requestDTO.getBeneficiaries().isEmpty()) {
            log.info("First beneficiary: {}", requestDTO.getBeneficiaries().get(0));
            log.info(
                    "First beneficiary details - payeeIdentity: '{}', paymentModality: '{}', financialAddress: '{}', bankingInstitutionCode: '{}'",
                    requestDTO.getBeneficiaries().get(0).getPayeeIdentity(), requestDTO.getBeneficiaries().get(0).getPaymentModality(),
                    requestDTO.getBeneficiaries().get(0).getFinancialAddress(),
                    requestDTO.getBeneficiaries().get(0).getBankingInstitutionCode());
        }
        try {
            accountLookupService.batchAccountLookup(callbackURL, requestDTO.getRequestID(), requestDTO.getBeneficiaries(),
                    registeringInstitutionId);

        } catch (Exception e) {
            log.error("ERROR in batch account lookup", e);
            ResponseDTO responseDTO = new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(),
                    requestDTO.getRequestID());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(),
                requestDTO.getRequestID());
        log.info("Batch account lookup accepted, returning 202 ACCEPTED");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
    }
}
