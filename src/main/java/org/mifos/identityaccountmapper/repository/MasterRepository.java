package org.mifos.identityaccountmapper.repository;

import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<IdentityDetails, Long> ,JpaSpecificationExecutor<IdentityDetails> {

    Optional<IdentityDetails> findByMasterId(String masterId);

    Optional<IdentityDetails> findByPayeeIdentity(String functionalId);

    Boolean existsByPayeeIdentity(String functionalId);
}
