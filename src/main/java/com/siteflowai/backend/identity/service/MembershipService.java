package com.siteflowai.backend.identity.service;

import com.siteflowai.backend.identity.domain.CompanyMembership;
import com.siteflowai.backend.identity.domain.MembershipStatus;
import com.siteflowai.backend.identity.dto.AddMembershipRequest;
import com.siteflowai.backend.identity.dto.MembershipResponse;
import com.siteflowai.backend.identity.exception.DuplicateMembershipException;
import com.siteflowai.backend.identity.exception.ResourceNotFoundException;
import com.siteflowai.backend.identity.repository.AppUserRepository;
import com.siteflowai.backend.identity.repository.CompanyMembershipRepository;
import com.siteflowai.backend.identity.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final CompanyMembershipRepository membershipRepository;
    private final CompanyRepository companyRepository;
    private final AppUserRepository userRepository;

    @Transactional
    public MembershipResponse addMembership(AddMembershipRequest request) {
        if (!companyRepository.existsById(request.companyId())) {
            throw new ResourceNotFoundException("Company not found: " + request.companyId());
        }
        if (!userRepository.existsById(request.userId())) {
            throw new ResourceNotFoundException("User not found: " + request.userId());
        }
        if (membershipRepository.existsByCompanyIdAndUserId(request.companyId(), request.userId())) {
            throw new DuplicateMembershipException(
                    "User " + request.userId() + " is already a member of company " + request.companyId());
        }

        CompanyMembership membership = CompanyMembership.builder()
                .companyId(request.companyId())
                .userId(request.userId())
                .role(request.role())
                .status(MembershipStatus.ACTIVE)
                .build();

        return MembershipResponse.from(membershipRepository.save(membership));
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> listByCompany(UUID companyId) {
        return membershipRepository.findByCompanyId(companyId).stream()
                .map(MembershipResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> listByUser(UUID userId) {
        return membershipRepository.findByUserId(userId).stream()
                .map(MembershipResponse::from)
                .toList();
    }
}
