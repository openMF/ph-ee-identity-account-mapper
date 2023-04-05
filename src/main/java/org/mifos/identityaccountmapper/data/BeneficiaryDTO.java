package org.mifos.identityaccountmapper.data;

public class  BeneficiaryDTO {
    private String payeeIdentity;
    private String paymentModality;
    private String accountNumber;
    private String bankingInstitutionCode;

    public BeneficiaryDTO(String payeeIdentity, String paymentModality, String accountNumber, String bankingInstitutionCode) {
        this.payeeIdentity = payeeIdentity;
        this.paymentModality = paymentModality;
        this.accountNumber = accountNumber;
        this.bankingInstitutionCode = bankingInstitutionCode;
    }

    public String getBankingInstitutionCode() {
        return bankingInstitutionCode;
    }

    public void setBankingInstitutionCode(String bankingInstitutionCode) {
        this.bankingInstitutionCode = bankingInstitutionCode;
    }

    public String getPayeeIdentity() {
        return payeeIdentity;
    }

    public void setPayeeIdentity(String payeeIdentity) {
        this.payeeIdentity = payeeIdentity;
    }

    public String getPaymentModality() {
        return paymentModality;
    }

    public void setPaymentModality(String paymentModality) {
        this.paymentModality = paymentModality;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
