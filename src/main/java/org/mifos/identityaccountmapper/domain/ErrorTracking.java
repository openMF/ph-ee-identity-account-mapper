package org.mifos.identityaccountmapper.domain;

import javax.persistence.*;


@Entity
@Table(name = "error_tracking")
public class ErrorTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "request_id", nullable = false)
    private String requestId ;
    @Column(name = "payee_identity", nullable = false)
    private String payeeIdentity ;
    @Column(name = "modality")
    private String modality ;
    @Column(name = "error_description", nullable = false)
    private String errorDescription ;

    public ErrorTracking() {
    }

    public ErrorTracking(String requestId, String payeeIdentity, String modality, String errorDescription) {
        this.requestId = requestId;
        this.payeeIdentity = payeeIdentity;
        this.modality = modality;
        this.errorDescription = errorDescription;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPayeeIdentity() {
        return payeeIdentity;
    }

    public void setPayeeIdentity(String payeeIdentity) {
        this.payeeIdentity = payeeIdentity;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
