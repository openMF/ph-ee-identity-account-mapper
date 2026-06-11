package org.mifos.identityaccountmapper.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "identity_details")
public class IdentityDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "master_id")
    private String masterId;
    @Column(name = "registering_institution_id", nullable = false)
    private String registeringInstitutionId;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "payee_identity", nullable = false)
    private String payeeIdentity;

    public IdentityDetails() {}

    public IdentityDetails(String masterId, String registeringInstitutionId, LocalDateTime createdOn, String payeeIdentity) {
        this.masterId = masterId;
        this.registeringInstitutionId = registeringInstitutionId;
        this.createdOn = createdOn;
        this.payeeIdentity = payeeIdentity;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getRegisteringInstitutionId() {
        return registeringInstitutionId;
    }

    public void setRegisteringInstitutionId(String registeringInstitutionId) {
        this.registeringInstitutionId = registeringInstitutionId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getPayeeIdentity() {
        return payeeIdentity;
    }

    public void setPayeeIdentity(String payeeIdentity) {
        this.payeeIdentity = payeeIdentity;
    }
}
