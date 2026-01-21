package org.mifos.identityaccountmapper.repository;

import java.util.List;
import java.util.Optional;
import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterRepository extends JpaRepository<IdentityDetails, Long>, JpaSpecificationExecutor<IdentityDetails> {

    List<IdentityDetails> findByMasterId(String masterId);

    Optional<IdentityDetails> findByMasterIdAndRegisteringInstitutionId(String masterId, String registeringInstitutionId);

    Optional<IdentityDetails> findByPayeeIdentityAndRegisteringInstitutionId(String functionalId, String registeringInstitutionId);

    Boolean existsByPayeeIdentityAndRegisteringInstitutionId(String functionalId, String registeringInstitutionId);

    Page<IdentityDetails> findByRegisteringInstitutionId(String registeringInstitutionId, Pageable pageable);
}
