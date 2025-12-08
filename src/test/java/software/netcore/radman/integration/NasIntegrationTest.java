package software.netcore.radman.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.data.radius.entity.Nas;
import software.netcore.radman.data.radius.repo.NasRepo;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for NAS management flow
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class NasIntegrationTest {

    @Autowired
    private NasService nasService;

    @Autowired
    private NasRepo nasRepo;

    @BeforeEach
    void setUp() {
        nasRepo.deleteAll();
    }

    @Test
    void createNas_ShouldPersistToDatabase() {
        // Arrange
        NasDto nasDto = new NasDto();
        nasDto.setNasName("integration-test-nas");
        nasDto.setShortName("itest");
        nasDto.setType("other");
        nasDto.setSecret("integration-secret");
        nasDto.setPorts(1812);

        // Act
        NasDto created = nasService.createNas(nasDto);

        // Assert
        assertThat(created.getId()).isNotNull();
        assertThat(nasRepo.findById(created.getId())).isPresent();

        Nas persisted = nasRepo.findById(created.getId()).get();
        assertThat(persisted.getNasName()).isEqualTo("integration-test-nas");
        assertThat(persisted.getSecret()).isEqualTo("integration-secret");
    }

    @Test
    void updateNas_ShouldModifyExistingRecord() {
        // Arrange
        Nas nas = new Nas();
        nas.setNasName("original-name");
        nas.setShortName("orig");
        nas.setType("other");
        nas.setSecret("original-secret");
        nas = nasRepo.save(nas);

        NasDto updateDto = new NasDto();
        updateDto.setId(nas.getId());
        updateDto.setNasName("updated-name");
        updateDto.setShortName("upd");
        updateDto.setType("other");
        updateDto.setSecret("updated-secret");

        // Act
        NasDto updated = nasService.updateNas(updateDto);

        // Assert
        Nas persisted = nasRepo.findById(nas.getId()).get();
        assertThat(persisted.getNasName()).isEqualTo("updated-name");
        assertThat(persisted.getSecret()).isEqualTo("updated-secret");
    }

    @Test
    void deleteNas_ShouldRemoveFromDatabase() {
        // Arrange
        Nas nas = new Nas();
        nas.setNasName("to-delete");
        nas.setShortName("del");
        nas.setType("other");
        nas.setSecret("delete-secret");
        nas = nasRepo.save(nas);

        NasDto nasDto = new NasDto();
        nasDto.setId(nas.getId());

        // Act
        nasService.deleteNas(nasDto);

        // Assert
        assertThat(nasRepo.findById(nas.getId())).isEmpty();
    }

    @Test
    void existsNasWithName_ShouldReturnCorrectStatus() {
        // Arrange
        Nas nas = new Nas();
        nas.setNasName("existing-nas");
        nas.setShortName("exist");
        nas.setType("other");
        nas.setSecret("secret");
        nasRepo.save(nas);

        // Act & Assert
        assertThat(nasService.existsNasWithName("existing-nas")).isTrue();
        assertThat(nasService.existsNasWithName("nonexistent")).isFalse();
    }

    @Test
    void countNasRecords_ShouldReturnCorrectCount() {
        // Arrange
        for (int i = 0; i < 5; i++) {
            Nas nas = new Nas();
            nas.setNasName("nas-" + i);
            nas.setShortName("n" + i);
            nas.setType("other");
            nas.setSecret("secret" + i);
            nasRepo.save(nas);
        }

        // Act
        long count = nasService.countNasRecords(null);

        // Assert
        assertThat(count).isEqualTo(5);
    }
}
