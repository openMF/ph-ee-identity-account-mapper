package org.mifos.identityaccountmapper.api.implementation;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.FAILED_RESPONSE_CODE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.FAILED_RESPONSE_MESSAGE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_CODE;
import static org.mifos.identityaccountmapper.util.AccountMapperEnum.SUCCESS_RESPONSE_MESSAGE;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.identityaccountmapper.api.definition.RegisterBeneficiaryApi;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.service.RegisterBeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RegisterBeneficiaryApiController implements RegisterBeneficiaryApi {

    @Autowired
    RegisterBeneficiaryService registerBeneficiaryService;

    @Override
    public <T> ResponseEntity<T> registerBeneficiary(String callbackURL, String registeringInstitutionId, RequestDTO requestBody)
            throws ExecutionException, InterruptedException, JsonProcessingException {
        log.info("TDDEBUG> Inside beneficiary lookup controller ");
        try {
            PhErrorDTO phErrorDTO = registerBeneficiaryService.registerBeneficiary(callbackURL, requestBody, registeringInstitutionId);
            if (phErrorDTO != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((T) phErrorDTO);
            }
        } catch (Exception e) {
            log.error("Internal Server Error in registerBeneficiary for requestID: {}", requestBody.getRequestID(), e); // <--
                                                                                                                        // ADD
                                                                                                                        // THIS
                                                                                                                        // LINE
            ResponseDTO responseDTO = new ResponseDTO(FAILED_RESPONSE_CODE.getValue(), FAILED_RESPONSE_MESSAGE.getValue(),
                    requestBody.getRequestID());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((T) responseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO(SUCCESS_RESPONSE_CODE.getValue(), SUCCESS_RESPONSE_MESSAGE.getValue(),
                requestBody.getRequestID());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body((T) responseDTO);
    }
}
