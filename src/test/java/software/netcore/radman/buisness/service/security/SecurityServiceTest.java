package software.netcore.radman.buisness.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.data.internal.entity.AuthProvider;
import software.netcore.radman.data.internal.entity.Role;
import software.netcore.radman.data.internal.repo.SystemUserRepo;
import software.netcore.radman.security.RoleAuthority;
import software.netcore.radman.security.fallback.SingleUserDetailsManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SecurityService
 */
@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private SingleUserDetailsManager userDetailsManager;

    @Mock
    private SystemUserRepo systemUserRepo;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        ReflectionTestUtils.setField(securityService, "autoLoginEnabled", false);
        ReflectionTestUtils.setField(securityService, "autoLoginUsername", "auto-admin");
    }

    @Test
    void getLoggedUserRole_ShouldReturnAdminRole() {
        // Arrange
        RoleAuthority adminAuthority = new RoleAuthority(Role.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenAnswer(invocation -> RoleAuthority.asCollection(adminAuthority));

        // Act
        RoleDto result = securityService.getLoggedUserRole();

        // Assert
        assertThat(result).isEqualTo(RoleDto.ADMIN);
    }

    @Test
    void getLoggedUserRole_ShouldReturnReadOnlyRole() {
        // Arrange
        RoleAuthority readOnlyAuthority = new RoleAuthority(Role.READ_ONLY);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenAnswer(invocation -> RoleAuthority.asCollection(readOnlyAuthority));

        // Act
        RoleDto result = securityService.getLoggedUserRole();

        // Assert
        assertThat(result).isEqualTo(RoleDto.READ_ONLY);
    }

    @Test
    void initiateFallbackUser_ShouldCreateUser_WhenNoAdminExists() {
        // Arrange
        when(systemUserRepo.countByRoleAndAuthProvider(Role.ADMIN, AuthProvider.LOCAL)).thenReturn(0L);

        // Act
        securityService.initiateFallbackUser();

        // Assert
        verify(userDetailsManager).createUser(any(User.class));
        verify(systemUserRepo).countByRoleAndAuthProvider(Role.ADMIN, AuthProvider.LOCAL);
    }

    @Test
    void initiateFallbackUser_ShouldNotCreateUser_WhenAdminExists() {
        // Arrange
        when(systemUserRepo.countByRoleAndAuthProvider(Role.ADMIN, AuthProvider.LOCAL)).thenReturn(1L);

        // Act
        securityService.initiateFallbackUser();

        // Assert
        verify(userDetailsManager, never()).createUser(any(User.class));
    }

    @Test
    void isAutoLoginEnabled_ShouldReturnFalse_WhenDisabled() {
        // Act
        boolean result = securityService.isAutoLoginEnabled();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isAutoLoginEnabled_ShouldReturnTrue_WhenEnabled() {
        // Arrange
        ReflectionTestUtils.setField(securityService, "autoLoginEnabled", true);

        // Act
        boolean result = securityService.isAutoLoginEnabled();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void getAutoLoginUsername_ShouldReturnConfiguredUsername() {
        // Act
        String result = securityService.getAutoLoginUsername();

        // Assert
        assertThat(result).isEqualTo("auto-admin");
    }

    @Test
    void isCurrentUserAutoLogin_ShouldReturnFalse_WhenAutoLoginDisabled() {
        // Act
        boolean result = securityService.isCurrentUserAutoLogin();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isCurrentUserAutoLogin_ShouldReturnTrue_WhenUserIsAutoLogin() {
        // Arrange
        ReflectionTestUtils.setField(securityService, "autoLoginEnabled", true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("auto-admin");

        // Act
        boolean result = securityService.isCurrentUserAutoLogin();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void isCurrentUserAutoLogin_ShouldReturnFalse_WhenUserIsNotAutoLogin() {
        // Arrange
        ReflectionTestUtils.setField(securityService, "autoLoginEnabled", true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("regular-user");

        // Act
        boolean result = securityService.isCurrentUserAutoLogin();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void getLoggedUsername_ShouldReturnUsername() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");

        // Act
        String result = securityService.getLoggedUsername();

        // Assert
        assertThat(result).isEqualTo("testuser");
    }

    @Test
    void getLoggedUsername_ShouldReturnNull_WhenNoAuthentication() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        String result = securityService.getLoggedUsername();

        // Assert
        assertThat(result).isNull();
    }
}
