package com.siteflowai.backend.identity.repository;

import com.siteflowai.backend.identity.domain.CompanyMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyMembershipRepository extends JpaRepository<CompanyMembership, UUID> {

    boolean existsByCompanyIdAndUserId(UUID companyId, UUID userId);

    List<CompanyMembership> findByUserId(UUID userId);

    List<CompanyMembership> findByCompanyId(UUID companyId);
}
