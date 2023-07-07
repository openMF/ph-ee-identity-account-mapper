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
    public void sendCallback(String body, String callbackURL){
         RestAssured.given()
                .baseUri(callbackURL)
                .body(body)
                .when()
                .put();
    }

    public CallbackRequestDTO createRequestBody(List<ErrorTracking> errorTrackingList, String requestId){
        List<FailedCaseDTO> failedCaseList = new ArrayList<>();
        int numberFailedCases = 0;
        for(ErrorTracking error: errorTrackingList){
            failedCaseList.add(new FailedCaseDTO(error.getPayeeIdentity(),error.getModality(),error.getErrorDescription()));
            numberFailedCases++;
        }

        return new CallbackRequestDTO(UniqueIDGenerator.generateUniqueNumber(12),requestId,numberFailedCases,failedCaseList);
    }
}
