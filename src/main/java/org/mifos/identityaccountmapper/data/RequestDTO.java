package org.mifos.identityaccountmapper.data;

import java.util.List;

public class RequestDTO {
    private String requestID;
    private String sourceBBID;
    private List< BeneficiaryDTO > beneficiaries ;

    public RequestDTO(String requestID, String sourceBBID, List<BeneficiaryDTO> beneficiaries) {
        this.requestID = requestID;
        this.sourceBBID = sourceBBID;
        this.beneficiaries = beneficiaries;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getSourceBBID() {
        return sourceBBID;
    }

    public void setSourceBBID(String sourceBBID) {
        this.sourceBBID = sourceBBID;
    }

    public List<BeneficiaryDTO> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<BeneficiaryDTO> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }
}
