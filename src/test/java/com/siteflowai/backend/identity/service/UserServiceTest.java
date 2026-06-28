package com.siteflowai.backend.identity.service;

import com.siteflowai.backend.identity.domain.AccountStatus;
import com.siteflowai.backend.identity.domain.AppUser;
import com.siteflowai.backend.identity.dto.CreateUserRequest;
import com.siteflowai.backend.identity.dto.UserResponse;
import com.siteflowai.backend.identity.exception.EmailAlreadyUsedException;
import com.siteflowai.backend.identity.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_normalizesEmail_hashesPassword_andOmitsHashFromResponse() {
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateUserRequest request = new CreateUserRequest("  User@Example.COM ", "secret123", "  Jane Doe  ");
        UserResponse response = userService.createUser(request);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());
        AppUser saved = captor.getValue();

        assertThat(saved.getEmail()).isEqualTo("user@example.com");
        assertThat(saved.getPasswordHash()).isEqualTo("HASHED");
        assertThat(saved.getFullName()).isEqualTo("Jane Doe");
        assertThat(saved.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        assertThat(response.email()).isEqualTo("user@example.com");
        // UserResponse intentionally has no password field to expose.
    }

    @Test
    void createUser_blankFullName_storedAsNull() {
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(new CreateUserRequest("user@example.com", "secret123", "   "));

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getFullName()).isNull();
    }

    @Test
    void createUser_duplicateEmail_throwsAndDoesNotPersist() {
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        assertThatThrownBy(() ->
                userService.createUser(new CreateUserRequest("User@example.com", "secret123", null)))
                .isInstanceOf(EmailAlreadyUsedException.class);

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
