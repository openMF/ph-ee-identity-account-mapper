package org.mifos.identityaccountmapper.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchAccountLookupResponseDTO {

    private String requestID;
    private String registeringInstitutionId;
    private List<BeneficiaryDTO> beneficiaryDTOList;
}
