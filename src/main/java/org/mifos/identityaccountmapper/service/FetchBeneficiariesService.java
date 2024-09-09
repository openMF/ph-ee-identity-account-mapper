package org.mifos.identityaccountmapper.service;

import java.util.List;
import java.util.Optional;

import org.mifos.identityaccountmapper.data.FetchBeneficiariesResponseDTO;
import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.mifos.identityaccountmapper.domain.PaymentModalityDetails;
import org.mifos.identityaccountmapper.exception.PayeeIdentityException;
import org.mifos.identityaccountmapper.repository.MasterRepository;
import org.mifos.identityaccountmapper.repository.PaymentModalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FetchBeneficiariesService {

    private final MasterRepository masterRepository;
    private final PaymentModalityRepository paymentModalityRepository;

    @Autowired
    public FetchBeneficiariesService(MasterRepository masterRepository, PaymentModalityRepository paymentModalityRepository) {
        this.masterRepository = masterRepository;
        this.paymentModalityRepository = paymentModalityRepository;
    }

    public FetchBeneficiariesResponseDTO fetchBeneficiary(String payeeIdentity, String registeringInstitutionId) {
        IdentityDetails identityDetails = new IdentityDetails();
        PaymentModalityDetails paymentModalityDetails = new PaymentModalityDetails();
        if (masterRepository.existsByPayeeIdentityAndRegisteringInstitutionId(payeeIdentity, registeringInstitutionId)) {
            identityDetails = masterRepository.findByPayeeIdentityAndRegisteringInstitutionId(payeeIdentity, registeringInstitutionId)
                    .orElseThrow(() -> PayeeIdentityException.payeeIdentityNotFound(payeeIdentity));
            paymentModalityDetails = paymentModalityRepository.findByMasterId(identityDetails.getMasterId()).get(0);
        }
        return new FetchBeneficiariesResponseDTO(registeringInstitutionId, payeeIdentity, paymentModalityDetails.getModality(),
                paymentModalityDetails.getDestinationAccount(), paymentModalityDetails.getInstitutionCode());
    }

    public Page<FetchBeneficiariesResponseDTO> fetchAllBeneficiaries(int page, int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        Page<IdentityDetails> identityPage = masterRepository.findAll(pageRequest);
        return identityPage.map(identityDetails -> {
            PaymentModalityDetails paymentModalityDetails = paymentModalityRepository.findByMasterId(identityDetails.getMasterId()).get(0);
            return new FetchBeneficiariesResponseDTO(identityDetails.getRegisteringInstitutionId(), identityDetails.getPayeeIdentity(),
                    paymentModalityDetails.getModality(), paymentModalityDetails.getDestinationAccount(), paymentModalityDetails.getInstitutionCode());
        });
    }

    public Page<FetchBeneficiariesResponseDTO> fetchAllBeneficiariesByRegisteringInstitution(int page, int pageSize, String registeringInstitutionId) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        Page<IdentityDetails> identityPage = masterRepository.findByRegisteringInstitutionId(registeringInstitutionId, pageRequest);
        return identityPage.map(identityDetails -> {
            PaymentModalityDetails paymentModalityDetails = paymentModalityRepository.findByMasterId(identityDetails.getMasterId()).get(0);
            return new FetchBeneficiariesResponseDTO(registeringInstitutionId, identityDetails.getPayeeIdentity(),
                    paymentModalityDetails.getModality(), paymentModalityDetails.getDestinationAccount(), paymentModalityDetails.getInstitutionCode());
        });
    }

    public Page<FetchBeneficiariesResponseDTO> fetchAllBeneficiariesByBankingInstitution(int page, int pageSize, String bankingInstitutionCode) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        Page<PaymentModalityDetails> paymentModalityPage = paymentModalityRepository.findByInstitutionCode(bankingInstitutionCode, pageRequest);
        System.out.println(paymentModalityPage);
        return paymentModalityPage.map(paymentModalityDetails -> {
            IdentityDetails identityDetails = masterRepository.findByMasterId(paymentModalityDetails.getMasterId()).get(0);
            System.out.println(identityDetails.getPayeeIdentity());
            System.out.println(paymentModalityDetails.getDestinationAccount());
            return new FetchBeneficiariesResponseDTO(identityDetails.getRegisteringInstitutionId(), identityDetails.getPayeeIdentity(),
                    paymentModalityDetails.getModality(), paymentModalityDetails.getDestinationAccount(), bankingInstitutionCode);

    });


}




}
