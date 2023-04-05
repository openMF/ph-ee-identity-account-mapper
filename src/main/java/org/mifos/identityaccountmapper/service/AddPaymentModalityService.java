package org.mifos.identityaccountmapper.service;

import org.mifos.identityaccountmapper.data.BeneficiaryDTO;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.data.ResponseDTO;
import org.mifos.identityaccountmapper.domain.ErrorTracking;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddPaymentModalityService {
    public ResponseDTO addPaymentModality(String callbackURL, RequestDTO requestBody){
        List<BeneficiaryDTO> beneficiaryList = requestBody.getBeneficiaries();
        List<ErrorTracking> errorTrackingsList = new ArrayList<>();

        validateAndAddPaymentModality(beneficiaryList, requestBody, errorTrackingsList);

        return new ResponseDTO("20", "Request successfully received by Pay-BB", requestBody.getRequestID());
    }

    private void validateAndAddPaymentModality(List<BeneficiaryDTO> beneficiaryList, RequestDTO request, List<ErrorTracking> errorTrackingList){
        for(BeneficiaryDTO beneficiary: beneficiaryList){
            String requestID  = request.getRequestID();
            Boolean beneficiaryexists =  validateBeneficiary(beneficiary, requestID, errorTrackingList);
        }
    }

    private Boolean validateBeneficiary(BeneficiaryDTO beneficiary,String requestId, List<ErrorTracking> errorTrackingList){
        return null;
    }
}
