package org.mifos.identityaccountmapper.service;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.mifos.identityaccountmapper.data.CallbackRequestDTO;
import org.mifos.identityaccountmapper.data.FailedCaseDTO;
import org.mifos.identityaccountmapper.domain.ErrorTracking;
import org.mifos.identityaccountmapper.util.UniqueIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SendCallbackService {
    private static final Logger logger = LoggerFactory.getLogger(SendCallbackService.class);

    public void sendCallback(String body, String callbackURL){
        logger.debug(body);
        logger.debug(callbackURL);
        Response response = RestAssured.given()
                .baseUri(callbackURL)
                .body(body)
                .when()
                .put();

        String responseBody = response.getBody().asString();
        logger.debug(responseBody);
        int responseCode = response.getStatusCode();
        logger.debug(String.valueOf(responseCode));
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
