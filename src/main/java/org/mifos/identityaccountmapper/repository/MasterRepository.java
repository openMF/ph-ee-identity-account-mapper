package org.mifos.identityaccountmapper.repository;

import org.mifos.identityaccountmapper.domain.IdentityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterRepository extends JpaRepository<IdentityDetails, Long> ,JpaSpecificationExecutor<IdentityDetails> {

    IdentityDetails findByMasterId(String masterId);

    IdentityDetails findByPayeeIdentity(String functionalId);

    Boolean existsByPayeeIdentity(String functionalId);
}
