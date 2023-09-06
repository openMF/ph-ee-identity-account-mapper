package org.mifos.identityaccountmapper.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FetchBeneficiariesResponseDTO {
    private String registeringInstitutionId;
    private String payeeIdentity;
    private String paymentModality;
    private String financialAddress;
    private String bankingInstitutionCode;
}
