package org.mifos.identityaccountmapper.service;

import org.mifos.identityaccountmapper.data.BeneficiaryDTO;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.domain.ErrorTracking;
import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.mifos.identityaccountmapper.domain.PaymentModalityDetails;
import org.mifos.identityaccountmapper.repository.ErrorTrackingRepository;
import org.mifos.identityaccountmapper.repository.MasterRepository;
import org.mifos.identityaccountmapper.repository.PaymentModalityRepository;
import org.mifos.identityaccountmapper.util.UniqueIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RegisterBeneficiaryService {

    private final MasterRepository masterRepository;
    private final ErrorTrackingRepository errorTrackingRepository;
    private final PaymentModalityRepository paymentModalityRepository;
    private final SendCallbackService sendCallbackService;
    private static final Logger logger = LoggerFactory.getLogger(RegisterBeneficiaryService.class);

    @Autowired
    public RegisterBeneficiaryService(MasterRepository masterRepository, ErrorTrackingRepository errorTrackingRepository,
                                       PaymentModalityRepository paymentModalityRepository, SendCallbackService sendCallbackService){
        this.masterRepository = masterRepository;
        this.errorTrackingRepository = errorTrackingRepository;
        this.paymentModalityRepository = paymentModalityRepository;
        this.sendCallbackService = sendCallbackService;
    }


    @Async("asyncExecutor")
    public void registerBeneficiary(String callbackURL, RequestDTO requestBody) throws InterruptedException {
        List<BeneficiaryDTO> beneficiaryList = requestBody.getBeneficiaries();
        List<ErrorTracking> errorTrackingsList = new ArrayList<>();

        validateAndSaveBeneficiaries(beneficiaryList, requestBody, errorTrackingsList);
        sendCallbackService.sendCallback(errorTrackingsList, callbackURL,requestBody.getRequestID());
    }

    @Transactional
    private void validateAndSaveBeneficiaries(List<BeneficiaryDTO> beneficiariesList, RequestDTO request, List<ErrorTracking> errorTrackingList){
        for(BeneficiaryDTO beneficiary: beneficiariesList){
            String requestID  = request.getRequestID();
            Boolean beneficiaryexists =  validateBeneficiary(beneficiary, requestID, errorTrackingList);
            try {
                if (!beneficiaryexists) {
                    String masterId = UniqueIDGenerator.generateUniqueNumber(20);
                    IdentityDetails identityDetails = new IdentityDetails(masterId, request.getSourceBBID(), LocalDateTime.now(), beneficiary.getPayeeIdentity());
                    this.masterRepository.save(identityDetails);
                    String destinationAccount = beneficiary.getAccountNumber() != null ? "" : beneficiary.getAccountNumber();
                    PaymentModalityDetails paymentModalityDetails = new PaymentModalityDetails(masterId, destinationAccount, beneficiary.getPaymentModality(), "");
                    this.paymentModalityRepository.save(paymentModalityDetails);
                }
            }catch (RuntimeException e) {
                try {
                    ErrorTracking errorTracking = new ErrorTracking(requestID, beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(), e.getMessage());
                    this.errorTrackingRepository.save(errorTracking);
                } catch (RuntimeException ex) {
                    logger.error(e.getMessage());
                } catch (Exception e2) {
                    logger.error(e2.getMessage());
                }
            }catch(Exception e){
                logger.error(e.getMessage());
            }
        }
    }

    @Transactional
    private Boolean validateBeneficiary(BeneficiaryDTO beneficiary, String requestID, List<ErrorTracking> errorTrackingList){
        Boolean beneficiaryExists = masterRepository.existsByPayeeIdentity(beneficiary.getPayeeIdentity());
        try{
            if(beneficiaryExists){
                ErrorTracking  errorTracking= new ErrorTracking(requestID,beneficiary.getPayeeIdentity(), beneficiary.getPaymentModality(),"Beneficiary already registered");
                errorTrackingList.add(errorTracking);
                this.errorTrackingRepository.save(errorTracking);
            }
        }
        catch(Exception e){
            logger.error(e.getMessage());
        }
        return beneficiaryExists;
    }
}
