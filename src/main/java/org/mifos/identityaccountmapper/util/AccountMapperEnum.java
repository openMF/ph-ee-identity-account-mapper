package org.mifos.identityaccountmapper.util;

public enum AccountMapperEnum {
    SUCCESS_RESPONSE_CODE("00"),
    FAILED_RESPONSE_CODE("01"),
    SUCCESS_RESPONSE_MESSAGE("Request successfully received by Pay-BB"),
    FAILED_RESPONSE_MESSAGE("Request not acknowledged by Pay-BB"),
    WORKER_ACCOUNT_LOOKUP_CALLBACK("mojaloop-party-lookup-callback");

    private final String value;


    AccountMapperEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
