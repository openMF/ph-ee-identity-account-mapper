package org.mifos.identityaccountmapper.service;

import java.util.List;
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

    public Page<FetchBeneficiariesResponseDTO> fetchAllBeneficiaries(int page, int pageSize, String registeringInstitutionId) {
        Pageable pageRequest = PageRequest.of(page, pageSize);

        Page<IdentityDetails> identityPage = masterRepository.findByRegisteringInstitutionId(registeringInstitutionId, pageRequest);

        return identityPage.map(identityDetails -> {
            PaymentModalityDetails paymentModalityDetails = null;
            if (identityDetails != null) {
                List<PaymentModalityDetails> paymentModalities = paymentModalityRepository.findByMasterId(identityDetails.getMasterId());
                if (!paymentModalities.isEmpty()) {
                    paymentModalityDetails = paymentModalities.get(0);
                }
            }

            return new FetchBeneficiariesResponseDTO(registeringInstitutionId,
                    identityDetails != null ? identityDetails.getPayeeIdentity() : null,
                    paymentModalityDetails != null ? paymentModalityDetails.getModality() : null,
                    paymentModalityDetails != null ? paymentModalityDetails.getDestinationAccount() : null,
                    paymentModalityDetails != null ? paymentModalityDetails.getInstitutionCode() : null);
        });
    }

}
