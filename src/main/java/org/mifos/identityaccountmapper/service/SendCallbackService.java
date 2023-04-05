package org.mifos.identityaccountmapper.service;

import io.restassured.RestAssured;
import org.mifos.identityaccountmapper.data.CallbackRequestDTO;
import org.mifos.identityaccountmapper.data.FailedCaseDTO;
import org.mifos.identityaccountmapper.domain.ErrorTracking;
import org.mifos.identityaccountmapper.util.UniqueIDGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SendCallbackService {
    public void sendCallback(List<ErrorTracking> errorTrackingList, String callbackURL, String requestId){
        CallbackRequestDTO callbackRequestDTO = createRequestBody(errorTrackingList,requestId);
        String response = RestAssured.given()
                .baseUri(callbackURL)
                .body(callbackRequestDTO)
                .when()
                .post()
                .andReturn().asString();
    }

    private CallbackRequestDTO createRequestBody(List<ErrorTracking> errorTrackingList, String requestId){
        List<FailedCaseDTO> failedCaseList = new ArrayList<>();
        int numberFailedCases = 0;
        for(ErrorTracking error: errorTrackingList){
            failedCaseList.add(new FailedCaseDTO(error.getPayeeIdentity(),error.getModality(),error.getErrorDescription()));
            numberFailedCases++;
        }

        return new CallbackRequestDTO(UniqueIDGenerator.generateUniqueNumber(12),requestId,numberFailedCases,failedCaseList);
    }
}
