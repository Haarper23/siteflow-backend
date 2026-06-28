package com.siteflowai.backend.identity.service;

import com.siteflowai.backend.identity.domain.Company;
import com.siteflowai.backend.identity.dto.CompanyResponse;
import com.siteflowai.backend.identity.dto.CreateCompanyRequest;
import com.siteflowai.backend.identity.exception.ResourceNotFoundException;
import com.siteflowai.backend.identity.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        Company company = Company.builder()
                .name(request.name().trim())
                .build();
        return CompanyResponse.from(companyRepository.save(company));
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompany(UUID id) {
        return CompanyResponse.from(loadCompany(id));
    }

    private Company loadCompany(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
    }
}
