package org.mifos.identityaccountmapper.api.implementation;

import org.mifos.identityaccountmapper.api.definition.BatchAccountLookupApi;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.AccountLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.*;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_MESSAGE;

@RestController
public class BatchAccountLookupApiController implements BatchAccountLookupApi {
    @Autowired
    AccountLookupService accountLookupService;
    @Override
    public ResponseEntity<ResponseDTO> batchAccountLookup(String callbackURL, RequestDTO requestDTO, String registeringInstitutionId) throws ExecutionException, InterruptedException {
        try{
            accountLookupService.batchAccountLookup(callbackURL, requestDTO.getRequestID(), requestDTO.getBeneficiaries(), registeringInstitutionId);

        } catch (Exception e) {
        ResponseDTO responseDTO = new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(), requestDTO.getRequestID());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }
    ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(), requestDTO.getRequestID());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
    }
}
