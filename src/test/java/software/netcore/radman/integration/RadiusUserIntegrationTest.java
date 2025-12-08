package software.netcore.radman.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import software.netcore.radman.buisness.service.dto.LoadingResult;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.data.internal.entity.RadiusUser;
import software.netcore.radman.data.internal.repo.RadiusUserRepo;
import software.netcore.radman.data.radius.entity.RadCheck;
import software.netcore.radman.data.radius.entity.RadReply;
import software.netcore.radman.data.radius.repo.RadCheckRepo;
import software.netcore.radman.data.radius.repo.RadReplyRepo;
import software.netcore.radman.data.radius.repo.RadUserGroupRepo;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Radius User management flow
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class RadiusUserIntegrationTest {

    @Autowired
    private RadiusUserService radiusUserService;

    @Autowired
    private RadiusUserRepo radiusUserRepo;

    @Autowired
    private RadCheckRepo radCheckRepo;

    @Autowired
    private RadReplyRepo radReplyRepo;

    @Autowired
    private RadUserGroupRepo radUserGroupRepo;

    @BeforeEach
    void setUp() {
        radUserGroupRepo.deleteAll();
        radCheckRepo.deleteAll();
        radReplyRepo.deleteAll();
        radiusUserRepo.deleteAll();
    }

    @Test
    void createRadiusUser_ShouldPersistToDatabase() {
        // Arrange
        RadiusUserDto userDto = new RadiusUserDto();
        userDto.setUsername("integrationuser");
        userDto.setDescription("Integration Test User");

        // Act
        RadiusUserDto created = radiusUserService.createRadiusUser(userDto);

        // Assert
        assertThat(created.getId()).isNotNull();
        assertThat(radiusUserRepo.findById(created.getId())).isPresent();

        RadiusUser persisted = radiusUserRepo.findById(created.getId()).get();
        assertThat(persisted.getUsername()).isEqualTo("integrationuser");
        assertThat(persisted.getDescription()).isEqualTo("Integration Test User");
    }

    @Test
    void updateRadiusUser_ShouldModifyExistingRecord() {
        // Arrange
        RadiusUser user = new RadiusUser();
        user.setUsername("originaluser");
        user.setDescription("Original Description");
        user = radiusUserRepo.save(user);

        RadiusUserDto updateDto = new RadiusUserDto();
        updateDto.setId(user.getId());
        updateDto.setUsername("updateduser");
        updateDto.setDescription("Updated Description");

        // Act
        RadiusUserDto updated = radiusUserService.updateRadiusUser(updateDto);

        // Assert
        RadiusUser persisted = radiusUserRepo.findById(user.getId()).get();
        assertThat(persisted.getUsername()).isEqualTo("updateduser");
        assertThat(persisted.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void deleteRadiusUser_WithRemoveFromRadius_ShouldRemoveAllRelatedData() {
        // Arrange
        RadiusUser user = new RadiusUser();
        user.setUsername("deleteuser");
        user = radiusUserRepo.save(user);

        // Create related radius data
        RadCheck radCheck = new RadCheck();
        radCheck.setUsername("deleteuser");
        radCheck.setAttribute("Cleartext-Password");
        radCheck.setOp(":=");
        radCheck.setValue("password");
        radCheckRepo.save(radCheck);

        RadReply radReply = new RadReply();
        radReply.setUsername("deleteuser");
        radReply.setAttribute("Framed-IP-Address");
        radReply.setOp(":=");
        radReply.setValue("192.168.1.100");
        radReplyRepo.save(radReply);

        RadiusUserDto userDto = new RadiusUserDto();
        userDto.setId(user.getId());
        userDto.setUsername("deleteuser");

        // Act
        radiusUserService.deleteRadiusUser(userDto, true);

        // Assert
        assertThat(radiusUserRepo.findById(user.getId())).isEmpty();
        assertThat(radCheckRepo.findAll()).isEmpty();
        assertThat(radReplyRepo.findAll()).isEmpty();
    }

    @Test
    void deleteRadiusUser_WithoutRemoveFromRadius_ShouldOnlyRemoveFromRadman() {
        // Arrange
        RadiusUser user = new RadiusUser();
        user.setUsername("keepuser");
        user = radiusUserRepo.save(user);

        // Create related radius data
        RadCheck radCheck = new RadCheck();
        radCheck.setUsername("keepuser");
        radCheck.setAttribute("Cleartext-Password");
        radCheck.setOp(":=");
        radCheck.setValue("password");
        radCheckRepo.save(radCheck);

        RadiusUserDto userDto = new RadiusUserDto();
        userDto.setId(user.getId());
        userDto.setUsername("keepuser");

        // Act
        radiusUserService.deleteRadiusUser(userDto, false);

        // Assert
        assertThat(radiusUserRepo.findById(user.getId())).isEmpty();
        assertThat(radCheckRepo.findAll()).isNotEmpty(); // Should still exist
    }

    @Test
    void loadRadiusUsersFromRadiusDB_ShouldImportNewUsers() {
        // Arrange
        RadCheck radCheck1 = new RadCheck();
        radCheck1.setUsername("radiususer1");
        radCheck1.setAttribute("Cleartext-Password");
        radCheck1.setOp(":=");
        radCheck1.setValue("pass1");
        radCheckRepo.save(radCheck1);

        RadReply radReply1 = new RadReply();
        radReply1.setUsername("radiususer2");
        radReply1.setAttribute("Framed-IP-Address");
        radReply1.setOp(":=");
        radReply1.setValue("192.168.1.1");
        radReplyRepo.save(radReply1);

        // Act
        LoadingResult result = radiusUserService.loadRadiusUsersFromRadiusDB();

        // Assert
        assertThat(result.getLoaded()).isEqualTo(2);
        assertThat(result.getDuplicate()).isEqualTo(0);
        assertThat(result.getErrored()).isEqualTo(0);
        assertThat(radiusUserRepo.count()).isEqualTo(2);
    }

    @Test
    void loadRadiusUsersFromRadiusDB_ShouldHandleDuplicates() {
        // Arrange
        // Pre-create user in radman
        RadiusUser existingUser = new RadiusUser();
        existingUser.setUsername("existinguser");
        radiusUserRepo.save(existingUser);

        // Create user in radius
        RadCheck radCheck = new RadCheck();
        radCheck.setUsername("existinguser");
        radCheck.setAttribute("Cleartext-Password");
        radCheck.setOp(":=");
        radCheck.setValue("password");
        radCheckRepo.save(radCheck);

        // Act
        LoadingResult result = radiusUserService.loadRadiusUsersFromRadiusDB();

        // Assert
        assertThat(result.getLoaded()).isEqualTo(0);
        assertThat(result.getDuplicate()).isEqualTo(1);
        assertThat(result.getErrored()).isEqualTo(0);
        assertThat(radiusUserRepo.count()).isEqualTo(1); // Still only one
    }
}
