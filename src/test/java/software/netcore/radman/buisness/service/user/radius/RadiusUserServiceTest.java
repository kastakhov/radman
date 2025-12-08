package software.netcore.radman.buisness.service.user.radius;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import software.netcore.radman.buisness.service.dto.LoadingResult;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.data.internal.entity.RadiusUser;
import software.netcore.radman.data.internal.repo.RadiusGroupRepo;
import software.netcore.radman.data.internal.repo.RadiusUserRepo;
import software.netcore.radman.data.radius.repo.*;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RadiusUserService
 */
@ExtendWith(MockitoExtension.class)
class RadiusUserServiceTest {

    @Mock
    private RadiusUserRepo radiusUserRepo;

    @Mock
    private RadiusGroupRepo radiusGroupRepo;

    @Mock
    private RadUserGroupRepo radUserGroupRepo;

    @Mock
    private RadCheckRepo radCheckRepo;

    @Mock
    private RadReplyRepo radReplyRepo;

    @Mock
    private RadGroupCheckRepo radGroupCheckRepo;

    @Mock
    private RadGroupReplyRepo radGroupReplyRepo;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private RadiusUserService radiusUserService;

    private RadiusUserDto testUserDto;
    private RadiusUser testUser;

    @BeforeEach
    void setUp() {
        testUserDto = new RadiusUserDto();
        testUserDto.setUsername("testuser");

        testUser = new RadiusUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void createRadiusUser_ShouldCreateUser() {
        // Arrange
        when(conversionService.convert(testUserDto, RadiusUser.class)).thenReturn(testUser);
        when(radiusUserRepo.save(any(RadiusUser.class))).thenReturn(testUser);
        when(conversionService.convert(testUser, RadiusUserDto.class)).thenReturn(testUserDto);

        // Act
        RadiusUserDto result = radiusUserService.createRadiusUser(testUserDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(radiusUserRepo).save(any(RadiusUser.class));
    }

    @Test
    void updateRadiusUser_ShouldUpdateUser() {
        // Arrange
        testUserDto.setId(1L);
        when(conversionService.convert(testUserDto, RadiusUser.class)).thenReturn(testUser);
        when(radiusUserRepo.save(any(RadiusUser.class))).thenReturn(testUser);
        when(conversionService.convert(testUser, RadiusUserDto.class)).thenReturn(testUserDto);

        // Act
        RadiusUserDto result = radiusUserService.updateRadiusUser(testUserDto);

        // Assert
        assertThat(result).isNotNull();
        verify(radiusUserRepo).save(any(RadiusUser.class));
    }

    @Test
    void deleteRadiusUser_WithRemoveFromRadius_ShouldDeleteFromAllTables() {
        // Arrange
        testUserDto.setId(1L);

        // Act
        radiusUserService.deleteRadiusUser(testUserDto, true);

        // Assert
        verify(radiusUserRepo).deleteById(1L);
        verify(radCheckRepo).deleteAllByUsername("testuser");
        verify(radReplyRepo).deleteAllByUsername("testuser");
        verify(radUserGroupRepo).deleteAllByUsername("testuser");
    }

    @Test
    void deleteRadiusUser_WithoutRemoveFromRadius_ShouldOnlyDeleteFromRadman() {
        // Arrange
        testUserDto.setId(1L);

        // Act
        radiusUserService.deleteRadiusUser(testUserDto, false);

        // Assert
        verify(radiusUserRepo).deleteById(1L);
        verify(radCheckRepo, never()).deleteAllByUsername(anyString());
        verify(radReplyRepo, never()).deleteAllByUsername(anyString());
        verify(radUserGroupRepo, never()).deleteAllByUsername(anyString());
    }

    @Test
    void loadRadiusUsersFromRadiusDB_ShouldLoadNewUsers() {
        // Arrange
        Set<String> radCheckUsernames = new HashSet<>();
        radCheckUsernames.add("user1");
        radCheckUsernames.add("user2");

        Set<String> radReplyUsernames = new HashSet<>();
        radReplyUsernames.add("user2");
        radReplyUsernames.add("user3");

        when(radCheckRepo.getUsernames()).thenReturn(radCheckUsernames);
        when(radReplyRepo.getUsernames()).thenReturn(radReplyUsernames);
        when(radiusUserRepo.exists(any())).thenReturn(false);
        when(radiusUserRepo.save(any(RadiusUser.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        LoadingResult result = radiusUserService.loadRadiusUsersFromRadiusDB();

        // Assert
        assertThat(result.getLoaded()).isEqualTo(3); // user1, user2, user3
        assertThat(result.getDuplicate()).isEqualTo(0);
        assertThat(result.getErrored()).isEqualTo(0);
        verify(radiusUserRepo, times(3)).save(any(RadiusUser.class));
    }

    @Test
    void loadRadiusUsersFromRadiusDB_ShouldHandleDuplicates() {
        // Arrange
        Set<String> radCheckUsernames = new HashSet<>();
        radCheckUsernames.add("user1");

        when(radCheckRepo.getUsernames()).thenReturn(radCheckUsernames);
        when(radReplyRepo.getUsernames()).thenReturn(new HashSet<>());
        when(radiusUserRepo.exists(any())).thenReturn(true);

        // Act
        LoadingResult result = radiusUserService.loadRadiusUsersFromRadiusDB();

        // Assert
        assertThat(result.getLoaded()).isEqualTo(0);
        assertThat(result.getDuplicate()).isEqualTo(1);
        assertThat(result.getErrored()).isEqualTo(0);
        verify(radiusUserRepo, never()).save(any(RadiusUser.class));
    }
}
