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
import lombok.extern.slf4j.Slf4j;
import org.mifos.identityaccountmapper.api.definition.AccountLookupCallback;
import org.mifos.identityaccountmapper.data.AccountLookupResponseDTO;
import org.mifos.identityaccountmapper.data.BatchAccountLookupResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
        log.info("TDDEBUG> Inside account lookup CALLBACK controller");
        log.info("TDDEBUG> requestBody: " + requestBody);
        try {
            logger.info(requestBody);
            accountLookupResponseDTO = objectMapper.readValue(requestBody, AccountLookupResponseDTO.class);
            variables.put(ACCOUNT_LOOKUP_FAILED, false);
            variables.put(PAYEE_PARTY_ID, accountLookupResponseDTO.getPaymentModalityList().get(0).getFinancialAddress());
            variables.put(PAYEE_PARTY_ID_TYPE, accountLookupResponseDTO.getPaymentModalityList().get(0).getPaymentModality());
            variables.put(PARTY_LOOKUP_FSP_ID, accountLookupResponseDTO.getPaymentModalityList().get(0).getBankingInstitutionCode());
            transactionId = accountLookupResponseDTO.getRequestId();
            log.info("TDDEBUG> transactionId: " + transactionId);
            log.info("variables set: " + variables.toString());
            Boolean isValidated = accountLookupResponseDTO.getIsValidated();
            if (!isValidated) {
                variables.put(ACCOUNT_LOOKUP_FAILED, true);
            }
        } catch (IOException e) {
            variables.put(ACCOUNT_LOOKUP_FAILED, true);
            error = objectMapper.readValue(requestBody, String.class);
        }

        if (zeebeClient != null) {

            zeebeClient.newPublishMessageCommand().messageName(ACCOUNT_LOOKUP).correlationKey(transactionId)
                    .timeToLive(Duration.ofMillis(50000)).variables(variables).send();
        }
        return ResponseEntity.status(HttpStatus.OK).body("Accepted");
    }

    @Override
    public ResponseEntity<Object> batchAccountLookupCallback(String requestBody)
            throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        String error = null;
        String transactionId = null;
        log.info("=== BATCH ACCOUNT LOOKUP CALLBACK DEBUG ===");
        log.info("Request body length: {} chars", requestBody != null ? requestBody.length() : 0);
        log.info("Request body: {}", requestBody);
        BatchAccountLookupResponseDTO batchAccountLookupResponseDTO = null;
        try {
            batchAccountLookupResponseDTO = objectMapper.readValue(requestBody, BatchAccountLookupResponseDTO.class);
            log.info("Parsed response - Request ID: {}", batchAccountLookupResponseDTO.getRequestID());
            log.info("Number of beneficiaries in response: {}",
                    batchAccountLookupResponseDTO.getBeneficiaryDTOList() != null
                            ? batchAccountLookupResponseDTO.getBeneficiaryDTOList().size()
                            : 0);
            if (batchAccountLookupResponseDTO.getBeneficiaryDTOList() != null
                    && !batchAccountLookupResponseDTO.getBeneficiaryDTOList().isEmpty()) {
                log.info("First beneficiary in response: {}", batchAccountLookupResponseDTO.getBeneficiaryDTOList().get(0));
            }
            variables.put("batchAccountLookupCallback", requestBody);
            transactionId = batchAccountLookupResponseDTO.getRequestID();
            variables.put("cachedTransactionId", transactionId);
            log.info("Sending to Zeebe with correlation key: {}", transactionId);
        } catch (Exception e) {
            log.error("ERROR parsing batch account lookup response", e);
            logger.error(e.getMessage());
            variables.put(PARTY_LOOKUP_FAILED, true);
            error = objectMapper.readValue(requestBody, String.class);
        }

        if (zeebeClient != null) {

            zeebeClient.newPublishMessageCommand().messageName(BATCH_ACCOUNT_LOOKUP_RESPONSE).correlationKey(transactionId)
                    .timeToLive(Duration.ofMillis(50000)).variables(variables).send();
            log.info("Message sent to Zeebe successfully");
        } else {
            log.warn("ZeebeClient is null, cannot send message!");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Accepted");
    }
}
