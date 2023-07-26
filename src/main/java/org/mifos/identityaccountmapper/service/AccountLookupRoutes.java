package org.mifos.identityaccountmapper.service;

import org.apache.camel.LoggingLevel;
import org.mifos.connector.common.camel.ErrorHandlerRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AccountLookupRoutes extends ErrorHandlerRouteBuilder {
    public AccountLookupRoutes() {
        super.configure();
    }

    @Autowired
    private SendCallbackService sendCallbackService;
    @Autowired
    private AccountLookupService accountLookupService;

    @Override
    public void configure() {
        from("direct:send-account-lookup-callback")
                .id("send-account-lookup-callback")
                .log(LoggingLevel.DEBUG, "######## Sending Account Lookup Callback")
                .process(exchange -> {
                    Object isPayeePartyLookupFailed = exchange.getProperty("PARTY_LOOKUP_FAILED");
                    String callbackURL = exchange.getProperty("callbackURL",String.class);
                    String payeeIdentity = exchange.getProperty("payeeIdentity",String.class);
                    String transactionId = exchange.getProperty("transactionId", String.class);
                    String registeringInstitutionId = exchange.getProperty("registeringInstitutionId", String.class);
                    if (isPayeePartyLookupFailed != null && !(boolean) isPayeePartyLookupFailed) {
                        accountLookupService.sendAccountLookupCallback(callbackURL, true, payeeIdentity, transactionId, registeringInstitutionId);
                    }else {
                        accountLookupService.sendAccountLookupCallback(callbackURL, false, payeeIdentity, transactionId, registeringInstitutionId);
                    }
                });

    }
}
