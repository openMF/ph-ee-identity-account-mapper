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
public class RequestDTO {

    private String requestID;
    private String sourceBBID;
    private List<BeneficiaryDTO> beneficiaries;

}
