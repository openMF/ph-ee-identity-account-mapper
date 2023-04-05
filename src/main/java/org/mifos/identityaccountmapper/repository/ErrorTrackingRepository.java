package org.mifos.identityaccountmapper.repository;

import org.mifos.identityaccountmapper.domain.ErrorTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorTrackingRepository extends JpaRepository<ErrorTracking, Long>, JpaSpecificationExecutor<ErrorTracking> {
}
