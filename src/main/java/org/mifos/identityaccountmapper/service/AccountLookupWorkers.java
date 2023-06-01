package org.mifos.identityaccountmapper.service;

import io.camunda.zeebe.client.ZeebeClient;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

import static org.mifos.identityaccountmapper.util.AccountMapperEnum.WORKER_ACCOUNT_LOOKUP_CALLBACK;

@Component
public class AccountLookupWorkers {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;
    @Autowired
    private CamelContext camelContext;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Value("${zeebe.client.evenly-allocated-max-jobs}")
    private int workerMaxJobs;

    @PostConstruct
    public void setupWorkers() {
        logger.info("## generating " + WORKER_ACCOUNT_LOOKUP_CALLBACK + "zeebe worker");
        zeebeClient.newWorker()
                .jobType(WORKER_ACCOUNT_LOOKUP_CALLBACK.getValue())
                .handler((client, job) -> {
                    logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
                    Map<String, Object> existingVariables = job.getVariablesAsMap();

                    Exchange exchange = new DefaultExchange(camelContext);

                    logger.debug("Zeebe variables: {}", existingVariables);
                    producerTemplate.send("direct:send-account-lookup-callback", exchange);


                    client.newCompleteCommand(job.getKey())
                            .send()
                    ;
                })
                .name(WORKER_ACCOUNT_LOOKUP_CALLBACK.getValue())
                .maxJobsActive(workerMaxJobs)
                .open();

    }
}
