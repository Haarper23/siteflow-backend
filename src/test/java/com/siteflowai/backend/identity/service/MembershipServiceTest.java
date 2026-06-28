package com.siteflowai.backend.identity.service;

import com.siteflowai.backend.identity.domain.CompanyMembership;
import com.siteflowai.backend.identity.domain.MembershipStatus;
import com.siteflowai.backend.identity.domain.RoleCode;
import com.siteflowai.backend.identity.dto.AddMembershipRequest;
import com.siteflowai.backend.identity.dto.MembershipResponse;
import com.siteflowai.backend.identity.exception.DuplicateMembershipException;
import com.siteflowai.backend.identity.exception.ResourceNotFoundException;
import com.siteflowai.backend.identity.repository.AppUserRepository;
import com.siteflowai.backend.identity.repository.CompanyMembershipRepository;
import com.siteflowai.backend.identity.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock
    private CompanyMembershipRepository membershipRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private MembershipService membershipService;

    private final UUID companyId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void addMembership_persistsWithRoleAndActiveStatus() {
        when(companyRepository.existsById(companyId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(membershipRepository.existsByCompanyIdAndUserId(companyId, userId)).thenReturn(false);
        when(membershipRepository.save(any(CompanyMembership.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MembershipResponse response =
                membershipService.addMembership(new AddMembershipRequest(companyId, userId, RoleCode.ADMIN));

        ArgumentCaptor<CompanyMembership> captor = ArgumentCaptor.forClass(CompanyMembership.class);
        verify(membershipRepository).save(captor.capture());
        CompanyMembership saved = captor.getValue();

        assertThat(saved.getCompanyId()).isEqualTo(companyId);
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getRole()).isEqualTo(RoleCode.ADMIN);
        assertThat(saved.getStatus()).isEqualTo(MembershipStatus.ACTIVE);
        assertThat(response.role()).isEqualTo(RoleCode.ADMIN);
    }

    @Test
    void addMembership_duplicate_throwsAndDoesNotPersist() {
        when(companyRepository.existsById(companyId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(membershipRepository.existsByCompanyIdAndUserId(companyId, userId)).thenReturn(true);

        assertThatThrownBy(() ->
                membershipService.addMembership(new AddMembershipRequest(companyId, userId, RoleCode.WORKER)))
                .isInstanceOf(DuplicateMembershipException.class);

        verify(membershipRepository, never()).save(any());
    }

    @Test
    void addMembership_unknownCompany_throwsNotFound() {
        when(companyRepository.existsById(companyId)).thenReturn(false);

        assertThatThrownBy(() ->
                membershipService.addMembership(new AddMembershipRequest(companyId, userId, RoleCode.MANAGER)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(membershipRepository, never()).save(any());
    }
}
