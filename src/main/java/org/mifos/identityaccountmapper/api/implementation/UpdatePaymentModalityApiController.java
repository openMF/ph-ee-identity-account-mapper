package org.mifos.identityaccountmapper.api.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.api.definition.UpdatePaymentModalityApi;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.AddUpdatePaymentModalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.*;

@RestController
public class UpdatePaymentModalityApiController implements UpdatePaymentModalityApi {
    @Autowired
    AddUpdatePaymentModalityService addUpdatePaymentModalityService;
    @Override
    public ResponseEntity<ResponseDTO> updatePaymentModality(String callbackURL, String registeringInstitutionId, RequestDTO requestBody) throws ExecutionException, InterruptedException, JsonProcessingException {
        try {
            addUpdatePaymentModalityService.updatePaymentModality(callbackURL, requestBody, registeringInstitutionId);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(), requestBody.getRequestID());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(), requestBody.getRequestID());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
    }
}
