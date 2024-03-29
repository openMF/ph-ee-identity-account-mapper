package org.mifos.identityaccountmapper.data;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountLookupResponseDTO implements Serializable {

    private String requestId;
    private String payeeIdentity;
    private List<PaymentModalityDTO> paymentModalityList;
    private Boolean isValidated;
}
