package com.siteflowai.backend.identity.repository;

import com.siteflowai.backend.identity.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
