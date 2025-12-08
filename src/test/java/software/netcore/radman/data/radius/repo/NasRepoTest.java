package software.netcore.radman.data.radius.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import software.netcore.radman.data.radius.entity.Nas;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for NasRepo
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class NasRepoTest {

    @Autowired
    private NasRepo nasRepo;

    private Nas testNas;

    @BeforeEach
    void setUp() {
        nasRepo.deleteAll();

        testNas = new Nas();
        testNas.setNasName("test-nas");
        testNas.setShortName("test");
        testNas.setType("other");
        testNas.setSecret("secret123");
        testNas.setPorts(1812);
    }

    @Test
    void save_ShouldPersistNas() {
        // Act
        Nas saved = nasRepo.save(testNas);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNasName()).isEqualTo("test-nas");
        assertThat(saved.getSecret()).isEqualTo("secret123");
    }

    @Test
    void findById_ShouldReturnNas_WhenExists() {
        // Arrange
        Nas saved = nasRepo.save(testNas);

        // Act
        Optional<Nas> found = nasRepo.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getNasName()).isEqualTo("test-nas");
    }

    @Test
    void findAll_ShouldReturnAllNas() {
        // Arrange
        nasRepo.save(testNas);

        Nas nas2 = new Nas();
        nas2.setNasName("test-nas-2");
        nas2.setShortName("test2");
        nas2.setType("other");
        nas2.setSecret("secret456");
        nasRepo.save(nas2);

        // Act
        List<Nas> all = new java.util.ArrayList<>();
        nasRepo.findAll().forEach(all::add);

        // Assert
        assertThat(all).hasSize(2);
    }

    @Test
    void existsByNasName_ShouldReturnTrue_WhenExists() {
        // Arrange
        nasRepo.save(testNas);

        // Act
        boolean exists = nasRepo.existsByNasName("test-nas");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNasName_ShouldReturnFalse_WhenNotExists() {
        // Act
        boolean exists = nasRepo.existsByNasName("nonexistent");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void deleteById_ShouldRemoveNas() {
        // Arrange
        Nas saved = nasRepo.save(testNas);

        // Act
        nasRepo.deleteById(saved.getId());

        // Assert
        Optional<Nas> found = nasRepo.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_ShouldModifyNas() {
        // Arrange
        Nas saved = nasRepo.save(testNas);
        saved.setSecret("newsecret");

        // Act
        Nas updated = nasRepo.save(saved);

        // Assert
        assertThat(updated.getSecret()).isEqualTo("newsecret");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }
}
