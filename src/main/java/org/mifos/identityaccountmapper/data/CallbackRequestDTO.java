package org.mifos.identityaccountmapper.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
