package org.mifos.identityaccountmapper.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payment_modality_details")
public class PaymentModalityDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "master_id", nullable = false)
    private String masterId;
    @Column(name = "destination_account", nullable = true)
    private String destinationAccount;
    @Column(name = "modality")
    private String modality;
    @Column(name = "institution_code", nullable = true)
    private String institutionCode;

    public PaymentModalityDetails() {}

    public PaymentModalityDetails(String masterId, String destinationAccount, String modality, String institutionCode) {
        this.masterId = masterId;
        this.destinationAccount = destinationAccount;
        this.modality = modality;
        this.institutionCode = institutionCode;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }
}
