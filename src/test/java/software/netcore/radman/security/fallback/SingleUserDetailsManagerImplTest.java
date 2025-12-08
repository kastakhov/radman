package software.netcore.radman.security.fallback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import software.netcore.radman.data.internal.entity.Role;
import software.netcore.radman.security.RoleAuthority;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for SingleUserDetailsManagerImpl
 */
class SingleUserDetailsManagerImplTest {

    private SingleUserDetailsManagerImpl userDetailsManager;

    @BeforeEach
    void setUp() {
        userDetailsManager = new SingleUserDetailsManagerImpl();
    }

    @Test
    void createUser_ShouldStoreUser() {
        // Arrange
        UserDetails user = new User("admin", "password",
                RoleAuthority.asCollection(new RoleAuthority(Role.ADMIN)));

        // Act
        userDetailsManager.createUser(user);

        // Assert
        UserDetails loaded = userDetailsManager.loadUserByUsername("admin");
        assertThat(loaded).isNotNull();
        assertThat(loaded.getUsername()).isEqualTo("admin");
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenExists() {
        // Arrange
        UserDetails user = new User("testuser", "password",
                RoleAuthority.asCollection(new RoleAuthority(Role.READ_ONLY)));
        userDetailsManager.createUser(user);

        // Act
        UserDetails loaded = userDetailsManager.loadUserByUsername("testuser");

        // Assert
        assertThat(loaded).isNotNull();
        assertThat(loaded.getUsername()).isEqualTo("testuser");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotExists() {
        // Act & Assert
        assertThatThrownBy(() -> userDetailsManager.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void deleteUser_ShouldRemoveUser() {
        // Arrange
        UserDetails user = new User("deleteuser", "password",
                RoleAuthority.asCollection(new RoleAuthority(Role.ADMIN)));
        userDetailsManager.createUser(user);

        // Act
        userDetailsManager.deleteUser();

        // Assert
        assertThatThrownBy(() -> userDetailsManager.loadUserByUsername("deleteuser"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_AfterDeletion() {
        // Arrange
        UserDetails user = new User("tempuser", "password",
                RoleAuthority.asCollection(new RoleAuthority(Role.ADMIN)));
        userDetailsManager.createUser(user);
        userDetailsManager.deleteUser();

        // Act & Assert
        assertThatThrownBy(() -> userDetailsManager.loadUserByUsername("tempuser"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void createUser_ShouldReplaceExistingUser() {
        // Arrange
        UserDetails user1 = new User("user", "password1",
                RoleAuthority.asCollection(new RoleAuthority(Role.READ_ONLY)));
        UserDetails user2 = new User("user", "password2",
                RoleAuthority.asCollection(new RoleAuthority(Role.ADMIN)));

        // Act
        userDetailsManager.createUser(user1);
        userDetailsManager.createUser(user2);

        // Assert
        UserDetails loaded = userDetailsManager.loadUserByUsername("user");
        assertThat(loaded.getPassword()).isEqualTo("password2");
    }
}
