package com.siteflowai.backend.identity.repository;

import com.siteflowai.backend.identity.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
