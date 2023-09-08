package org.mifos.identityaccountmapper.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailedCaseDTO {

    private String payeeIdentity;
    private String paymentModality;
    private String failureReason;

}
