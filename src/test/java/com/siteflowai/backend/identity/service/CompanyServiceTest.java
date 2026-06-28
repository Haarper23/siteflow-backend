package com.siteflowai.backend.identity.service;

import com.siteflowai.backend.identity.domain.Company;
import com.siteflowai.backend.identity.dto.CompanyResponse;
import com.siteflowai.backend.identity.dto.CreateCompanyRequest;
import com.siteflowai.backend.identity.exception.ResourceNotFoundException;
import com.siteflowai.backend.identity.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void createCompany_trimsNameAndPersists() {
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyResponse response = companyService.createCompany(new CreateCompanyRequest("  Acme Construction  "));

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Acme Construction");
        assertThat(response.name()).isEqualTo("Acme Construction");
    }

    @Test
    void getCompany_whenMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getCompany(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
