package software.netcore.radman.data.internal.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import software.netcore.radman.data.internal.entity.RadiusUser;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for RadiusUserRepo
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RadiusUserRepoTest {

    @Autowired
    private RadiusUserRepo radiusUserRepo;

    private RadiusUser testUser;

    @BeforeEach
    void setUp() {
        radiusUserRepo.deleteAll();

        testUser = new RadiusUser();
        testUser.setUsername("testuser");
        testUser.setDescription("Test User");
    }

    @Test
    void save_ShouldPersistRadiusUser() {
        // Act
        RadiusUser saved = radiusUserRepo.save(testUser);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("testuser");
        assertThat(saved.getDescription()).isEqualTo("Test User");
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        // Arrange
        RadiusUser saved = radiusUserRepo.save(testUser);

        // Act
        Optional<RadiusUser> found = radiusUserRepo.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        radiusUserRepo.save(testUser);

        RadiusUser user2 = new RadiusUser();
        user2.setUsername("testuser2");
        radiusUserRepo.save(user2);

        // Act
        List<RadiusUser> all = (List<RadiusUser>) radiusUserRepo.findAll();

        // Assert
        assertThat(all).hasSize(2);
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        // Arrange
        RadiusUser saved = radiusUserRepo.save(testUser);

        // Act
        radiusUserRepo.deleteById(saved.getId());

        // Assert
        Optional<RadiusUser> found = radiusUserRepo.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_ShouldModifyUser() {
        // Arrange
        RadiusUser saved = radiusUserRepo.save(testUser);
        saved.setDescription("Updated Description");

        // Act
        RadiusUser updated = radiusUserRepo.save(saved);

        // Assert
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Arrange
        radiusUserRepo.save(testUser);

        RadiusUser user2 = new RadiusUser();
        user2.setUsername("testuser2");
        radiusUserRepo.save(user2);

        // Act
        long count = radiusUserRepo.count();

        // Assert
        assertThat(count).isEqualTo(2);
    }
}
