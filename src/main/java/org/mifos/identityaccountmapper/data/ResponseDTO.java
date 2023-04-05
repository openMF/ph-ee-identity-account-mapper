package org.mifos.identityaccountmapper.data;

public class ResponseDTO {
    private String ResponseCode;
    private String ResponseDescription;
    private String RequestID;

    public ResponseDTO(String responseCode, String responseDescription, String requestID) {
        ResponseCode = responseCode;
        ResponseDescription = responseDescription;
        RequestID = requestID;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        ResponseDescription = responseDescription;
    }

    public String getRequestID() {
        return RequestID;
    }

    public void setRequestID(String requestID) {
        RequestID = requestID;
    }
}
