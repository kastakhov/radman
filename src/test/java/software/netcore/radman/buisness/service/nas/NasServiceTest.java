package software.netcore.radman.buisness.service.nas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.data.radius.entity.Nas;
import software.netcore.radman.data.radius.entity.RadHuntGroup;
import software.netcore.radman.data.radius.repo.NasRepo;
import software.netcore.radman.data.radius.repo.RadHuntGroupRepo;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NasService
 */
@ExtendWith(MockitoExtension.class)
class NasServiceTest {

    @Mock
    private NasRepo nasRepo;

    @Mock
    private RadHuntGroupRepo radHuntGroupRepo;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private NasService nasService;

    private NasDto testNasDto;
    private Nas testNas;
    private NasGroupDto testNasGroupDto;
    private RadHuntGroup testRadHuntGroup;

    @BeforeEach
    void setUp() {
        testNasDto = new NasDto();
        testNasDto.setNasName("test-nas");
        testNasDto.setShortName("test");
        testNasDto.setSecret("secret123");

        testNas = new Nas();
        testNas.setId(1);
        testNas.setNasName("test-nas");
        testNas.setShortName("test");
        testNas.setSecret("secret123");

        testNasGroupDto = new NasGroupDto();
        testNasGroupDto.setGroupName("test-group");
        testNasGroupDto.setNasIpAddress("192.168.1.1");

        testRadHuntGroup = new RadHuntGroup();
        testRadHuntGroup.setId(1);
        testRadHuntGroup.setGroupName("test-group");
        testRadHuntGroup.setNasIpAddress("192.168.1.1");
    }

    @Test
    void createNas_ShouldCreateNasSuccessfully() {
        // Arrange
        when(conversionService.convert(testNasDto, Nas.class)).thenReturn(testNas);
        when(nasRepo.save(any(Nas.class))).thenReturn(testNas);
        when(conversionService.convert(testNas, NasDto.class)).thenReturn(testNasDto);

        // Act
        NasDto result = nasService.createNas(testNasDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNasName()).isEqualTo("test-nas");
        verify(nasRepo).save(any(Nas.class));
    }

    @Test
    void createNasGroup_ShouldCreateNasGroupSuccessfully() {
        // Arrange
        when(conversionService.convert(testNasGroupDto, RadHuntGroup.class)).thenReturn(testRadHuntGroup);
        when(radHuntGroupRepo.save(any(RadHuntGroup.class))).thenReturn(testRadHuntGroup);
        when(conversionService.convert(testRadHuntGroup, NasGroupDto.class)).thenReturn(testNasGroupDto);

        // Act
        NasGroupDto result = nasService.createNasGroup(testNasGroupDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getGroupName()).isEqualTo("test-group");
        verify(radHuntGroupRepo).save(any(RadHuntGroup.class));
    }

    @Test
    void updateNas_ShouldUpdateNasSuccessfully() {
        // Arrange
        testNasDto.setId(1);
        when(conversionService.convert(testNasDto, Nas.class)).thenReturn(testNas);
        when(nasRepo.save(any(Nas.class))).thenReturn(testNas);
        when(conversionService.convert(testNas, NasDto.class)).thenReturn(testNasDto);

        // Act
        NasDto result = nasService.updateNas(testNasDto);

        // Assert
        assertThat(result).isNotNull();
        verify(nasRepo).save(any(Nas.class));
    }

    @Test
    void existsNasWithName_ShouldReturnTrue_WhenNasExists() {
        // Arrange
        when(nasRepo.existsByNasName("test-nas")).thenReturn(true);

        // Act
        boolean exists = nasService.existsNasWithName("test-nas");

        // Assert
        assertThat(exists).isTrue();
        verify(nasRepo).existsByNasName("test-nas");
    }

    @Test
    void existsNasWithName_ShouldReturnFalse_WhenNasDoesNotExist() {
        // Arrange
        when(nasRepo.existsByNasName("nonexistent")).thenReturn(false);

        // Act
        boolean exists = nasService.existsNasWithName("nonexistent");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void deleteNas_ShouldDeleteNasSuccessfully() {
        // Arrange
        testNasDto.setId(1);

        // Act
        nasService.deleteNas(testNasDto);

        // Assert
        verify(nasRepo).deleteById(1);
    }

    @Test
    void deleteNasGroup_ShouldDeleteNasGroupSuccessfully() {
        // Arrange
        testNasGroupDto.setId(1);

        // Act
        nasService.deleteNasGroup(testNasGroupDto);

        // Assert
        verify(radHuntGroupRepo).deleteById(1);
    }

    @Test
    void pageNasRecords_ShouldReturnPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Nas> nasList = Arrays.asList(testNas);
        Page<Nas> nasPage = new PageImpl<>(nasList, pageable, 1);

        when(nasRepo.findAll(any(), eq(pageable))).thenReturn(nasPage);
        when(conversionService.convert(testNas, NasDto.class)).thenReturn(testNasDto);

        // Act
        Page<NasDto> result = nasService.pageNasRecords("test", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(nasRepo).findAll(any(), eq(pageable));
    }

    @Test
    void countNasRecords_ShouldReturnCount() {
        // Arrange
        when(nasRepo.count(any())).thenReturn(5L);

        // Act
        long count = nasService.countNasRecords("test");

        // Assert
        assertThat(count).isEqualTo(5L);
        verify(nasRepo).count(any());
    }

    @Test
    void existsNasGroupWithIpAddress_ShouldReturnTrue_WhenExists() {
        // Arrange
        when(radHuntGroupRepo.existsByNasIpAddress("192.168.1.1")).thenReturn(true);

        // Act
        boolean exists = nasService.existsNasGroupWithIpAddress("192.168.1.1");

        // Assert
        assertThat(exists).isTrue();
    }
}
