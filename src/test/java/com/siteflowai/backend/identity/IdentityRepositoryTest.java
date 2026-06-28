package com.siteflowai.backend.identity;

import com.siteflowai.backend.identity.domain.AccountStatus;
import com.siteflowai.backend.identity.domain.AppUser;
import com.siteflowai.backend.identity.domain.Company;
import com.siteflowai.backend.identity.domain.CompanyMembership;
import com.siteflowai.backend.identity.domain.MembershipStatus;
import com.siteflowai.backend.identity.domain.RoleCode;
import com.siteflowai.backend.identity.repository.AppUserRepository;
import com.siteflowai.backend.identity.repository.CompanyMembershipRepository;
import com.siteflowai.backend.identity.repository.CompanyRepository;
import com.siteflowai.backend.identity.repository.RoleRepository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Repository-slice tests against the real PostgreSQL schema produced by Flyway.
 * Each test runs in its own transaction and is rolled back, so runs are repeatable
 * and order-independent. The seeded {@code role} rows come from the migration
 * (committed outside the test transaction) and are never mutated here.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IdentityRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private CompanyMembershipRepository membershipRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void migrationSeedsTheFourRoles() {
        assertThat(roleRepository.count()).isEqualTo(4);
        assertThat(roleRepository.findById("OWNER")).isPresent();
        assertThat(roleRepository.findById("ADMIN")).isPresent();
        assertThat(roleRepository.findById("MANAGER")).isPresent();
        assertThat(roleRepository.findById("WORKER")).isPresent();
    }

    @Test
    void persistsAndFindsUserByEmailCaseInsensitively() {
        userRepository.saveAndFlush(newUser("person@example.com"));

        assertThat(userRepository.findByEmailIgnoreCase("PERSON@Example.com")).isPresent();
        assertThat(userRepository.existsByEmailIgnoreCase("Person@example.COM")).isTrue();
    }

    @Test
    void emailUniquenessIsEnforcedCaseInsensitivelyAtDatabaseLevel() {
        userRepository.saveAndFlush(newUser("dup@example.com"));

        AppUser caseVariant = newUser("DUP@Example.com");
        assertThatThrownBy(() -> userRepository.saveAndFlush(caseVariant))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void membershipIsUniquePerCompanyAndUser() {
        Company company = companyRepository.saveAndFlush(newCompany());
        AppUser user = userRepository.saveAndFlush(newUser("member@example.com"));
        membershipRepository.saveAndFlush(newMembership(company.getId(), user.getId(), RoleCode.ADMIN));

        CompanyMembership duplicate = newMembership(company.getId(), user.getId(), RoleCode.WORKER);
        assertThatThrownBy(() -> membershipRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void membershipQueriesByCompanyAndUser() {
        Company company = companyRepository.saveAndFlush(newCompany());
        AppUser user = userRepository.saveAndFlush(newUser("lookup@example.com"));
        membershipRepository.saveAndFlush(newMembership(company.getId(), user.getId(), RoleCode.MANAGER));

        assertThat(membershipRepository.existsByCompanyIdAndUserId(company.getId(), user.getId())).isTrue();
        assertThat(membershipRepository.findByCompanyId(company.getId())).hasSize(1);
        assertThat(membershipRepository.findByUserId(user.getId())).hasSize(1);
    }

    @Test
    void invalidRoleCodeViolatesForeignKey() {
        Company company = companyRepository.saveAndFlush(newCompany());
        AppUser user = userRepository.saveAndFlush(newUser("fk@example.com"));

        assertThatThrownBy(() -> entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO company_membership "
                        + "(id, company_id, user_id, role_code, status, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, now(), now())")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, company.getId())
                .setParameter(3, user.getId())
                .setParameter(4, "NOT_A_ROLE")
                .setParameter(5, MembershipStatus.ACTIVE.name())
                .executeUpdate())
                .isInstanceOf(PersistenceException.class);
    }

    private static Company newCompany() {
        return Company.builder().name("Acme Construction").build();
    }

    private static AppUser newUser(String email) {
        return AppUser.builder()
                .email(email)
                .passwordHash("$2a$10$placeholderplaceholderplaceholderplaceholderplaceholder")
                .fullName("Test User")
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private static CompanyMembership newMembership(UUID companyId, UUID userId, RoleCode role) {
        return CompanyMembership.builder()
                .companyId(companyId)
                .userId(userId)
                .role(role)
                .status(MembershipStatus.ACTIVE)
                .build();
    }
}
