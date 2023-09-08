package org.mifos.identityaccountmapper.repository;

import java.util.List;
import org.mifos.identityaccountmapper.domain.PaymentModalityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentModalityRepository
        extends JpaRepository<PaymentModalityDetails, Long>, JpaSpecificationExecutor<PaymentModalityDetails> {
    // IdentityDetails findByMasterId(Long masterId);

    List<PaymentModalityDetails> findByMasterId(String masterID);
}
