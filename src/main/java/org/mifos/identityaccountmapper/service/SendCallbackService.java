package org.mifos.identityaccountmapper.service;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.List;
import org.mifos.identityaccountmapper.data.CallbackRequestDTO;
import org.mifos.identityaccountmapper.data.FailedCaseDTO;
import org.mifos.identityaccountmapper.domain.ErrorTracking;
import org.mifos.identityaccountmapper.util.UniqueIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(SendCallbackService.class);

    public void sendCallback(String body, String callbackURL) {
        logger.debug(body);
        logger.debug(callbackURL);
        RequestSpecification requestSpec = new RequestSpecBuilder().build();
        requestSpec.relaxedHTTPSValidation();
        Response response = RestAssured.given(requestSpec).baseUri(callbackURL).header("Content-Type", ContentType.JSON).body(body).when()
                .put();

        String responseBody = response.getBody().asString();
        logger.info(responseBody);
        int responseCode = response.getStatusCode();
        logger.debug(String.valueOf(responseCode));
    }

    public CallbackRequestDTO createRequestBody(List<ErrorTracking> errorTrackingList, String requestId) {
        List<FailedCaseDTO> failedCaseList = new ArrayList<>();
        int numberFailedCases = 0;
        for (ErrorTracking error : errorTrackingList) {
            failedCaseList.add(new FailedCaseDTO(error.getPayeeIdentity(), error.getModality(), error.getErrorDescription()));
            numberFailedCases++;
        }

        return new CallbackRequestDTO(UniqueIDGenerator.generateUniqueNumber(12), requestId, numberFailedCases, failedCaseList);
    }
}
