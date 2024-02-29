package org.mifos.identityaccountmapper.api.implementation;

import static org.mifos.identityaccountmapper.zeebe.ZeebeVariables.ACCOUNT_LOOKUP;
import static org.mifos.identityaccountmapper.zeebe.ZeebeVariables.ACCOUNT_LOOKUP_FAILED;
import static org.mifos.identityaccountmapper.zeebe.ZeebeVariables.BATCH_ACCOUNT_LOOKUP_RESPONSE;
import static org.mifos.identityaccountmapper.zeebe.ZeebeVariables.PARTY_LOOKUP_FAILED;
import static org.mifos.identityaccountmapper.zeebe.ZeebeVariables.PARTY_LOOKUP_FSP_ID;
import static org.mifos.identityaccountmapper.zeebe.ZeebeVariables.PAYEE_PARTY_ID;
import static org.mifos.identityaccountmapper.zeebe.ZeebeVariables.PAYEE_PARTY_ID_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.mifos.identityaccountmapper.api.definition.AccountLookupCallback;
import org.mifos.identityaccountmapper.data.AccountLookupResponseDTO;
import org.mifos.identityaccountmapper.data.BatchAccountLookupResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountLookupCallbackController implements AccountLookupCallback {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private ZeebeClient zeebeClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResponseEntity<Object> accountLookupCallback(String requestBody)
            throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();

        String error = null;
        String transactionId = null;
        AccountLookupResponseDTO accountLookupResponseDTO = null;
        try {
            logger.info(requestBody);
            accountLookupResponseDTO = objectMapper.readValue(requestBody, AccountLookupResponseDTO.class);
            variables.put(ACCOUNT_LOOKUP_FAILED, false);
            variables.put(PAYEE_PARTY_ID, accountLookupResponseDTO.getPaymentModalityList().get(0).getFinancialAddress());
            variables.put(PAYEE_PARTY_ID_TYPE, accountLookupResponseDTO.getPaymentModalityList().get(0).getPaymentModality());
            variables.put(PARTY_LOOKUP_FSP_ID, accountLookupResponseDTO.getPaymentModalityList().get(0).getBankingInstitutionCode());
            transactionId = accountLookupResponseDTO.getRequestId();
            Boolean isValidated = accountLookupResponseDTO.getIsValidated();
            if (!isValidated) {
                variables.put(ACCOUNT_LOOKUP_FAILED, true);
            }
            logger.info("END of TRY block Error: {}", isValidated);
        } catch (IOException e) {
            variables.put(ACCOUNT_LOOKUP_FAILED, true);
            error = objectMapper.readValue(requestBody, String.class);
            logger.info("Error: {}", error);
        }

        // if (zeebeClient != null) {

        zeebeClient.newPublishMessageCommand().messageName(ACCOUNT_LOOKUP).correlationKey(transactionId).timeToLive(Duration.ofMillis(60000))
                .variables(variables).send().join();
        logger.info("-------------> variable published");
        // }
        return ResponseEntity.status(HttpStatus.OK).body("Accepted");
    }

    @Override
    public ResponseEntity<Object> batchAccountLookupCallback(String requestBody)
            throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        String error = null;
        String transactionId = null;
        // String response = exchange.getIn().getBody(String.class);
        BatchAccountLookupResponseDTO batchAccountLookupResponseDTO = null;
        try {
            batchAccountLookupResponseDTO = objectMapper.readValue(requestBody, BatchAccountLookupResponseDTO.class);
            variables.put("batchAccountLookupCallback", requestBody);
            transactionId = batchAccountLookupResponseDTO.getRequestID();
            variables.put("cachedTransactionId", transactionId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            variables.put(PARTY_LOOKUP_FAILED, true);
            error = objectMapper.readValue(requestBody, String.class);
        }

        if (zeebeClient != null) {

            zeebeClient.newPublishMessageCommand().messageName(BATCH_ACCOUNT_LOOKUP_RESPONSE).correlationKey(transactionId)
                    .timeToLive(Duration.ofMillis(50000)).variables(variables).send();
        }
        return ResponseEntity.status(HttpStatus.OK).body("Accepted");
    }
}
