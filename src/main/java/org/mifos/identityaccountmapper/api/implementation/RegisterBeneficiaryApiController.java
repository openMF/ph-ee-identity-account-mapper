package org.mifos.identityaccountmapper.api.implementation;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.FAILED_RESPONSE_CODE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.FAILED_RESPONSE_MESSAGE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_CODE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_MESSAGE;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.ExecutionException;
import org.mifos.identityaccountmapper.api.definition.RegisterBeneficiaryApi;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.RegisterBeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterBeneficiaryApiController implements RegisterBeneficiaryApi {

    @Autowired
    RegisterBeneficiaryService registerBeneficiaryService;

    @Override
    public ResponseEntity<ResponseDTO> registerBeneficiary(String callbackURL, String registeringInstitutionId, RequestDTO requestBody)
            throws ExecutionException, InterruptedException, JsonProcessingException {
        try {
            registerBeneficiaryService.registerBeneficiary(callbackURL, requestBody, registeringInstitutionId);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(),
                    requestBody.getRequestID());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(),
                requestBody.getRequestID());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
    }
}
