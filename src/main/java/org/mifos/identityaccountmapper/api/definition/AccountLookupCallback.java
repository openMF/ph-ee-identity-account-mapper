package org.mifos.identityaccountmapper.api.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.ExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountLookupCallback {

    @PutMapping("/accountLookupCallback")
    ResponseEntity<Object> accountLookupCallback(@RequestBody String requestBody)
            throws ExecutionException, InterruptedException, JsonProcessingException;

    @PutMapping("/batchAccountLookupCallback")
    ResponseEntity<Object> batchAccountLookupCallback(@RequestBody String requestBody)
            throws ExecutionException, InterruptedException, JsonProcessingException;
}
