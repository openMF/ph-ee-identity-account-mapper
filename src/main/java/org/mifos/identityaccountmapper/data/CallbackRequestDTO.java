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
public class CallbackRequestDTO {

    private String requestID;
    private String registerRequestID;
    private int numberFailedCases;
    private List<FailedCaseDTO> failedCases;

}
