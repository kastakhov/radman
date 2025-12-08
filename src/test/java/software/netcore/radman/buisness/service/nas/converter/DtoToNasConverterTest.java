package software.netcore.radman.buisness.service.nas.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.data.radius.entity.Nas;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DtoToNasConverter
 */
class DtoToNasConverterTest {

    private DtoToNasConverter converter;

    @BeforeEach
    void setUp() {
        converter = new DtoToNasConverter();
    }

    @Test
    void convert_ShouldConvertDtoToNas() {
        // Arrange
        NasDto nasDto = new NasDto();
        nasDto.setId(1);
        nasDto.setNasName("test-nas");
        nasDto.setShortName("test");
        nasDto.setServer("192.168.1.1");
        nasDto.setType("other");
        nasDto.setSecret("secret123");
        nasDto.setPorts(1812);
        nasDto.setCommunity("public");
        nasDto.setDescription("Test NAS");

        // Act
        Nas result = converter.convert(nasDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getNasName()).isEqualTo("test-nas");
        assertThat(result.getShortName()).isEqualTo("test");
        assertThat(result.getServer()).isEqualTo("192.168.1.1");
        assertThat(result.getType()).isEqualTo("other");
        assertThat(result.getSecret()).isEqualTo("secret123");
        assertThat(result.getPorts()).isEqualTo(1812);
        assertThat(result.getCommunity()).isEqualTo("public");
        assertThat(result.getDescription()).isEqualTo("Test NAS");
    }

    @Test
    void convert_ShouldHandleNullValues() {
        // Arrange
        NasDto nasDto = new NasDto();
        nasDto.setNasName("minimal-nas");

        // Act
        Nas result = converter.convert(nasDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNasName()).isEqualTo("minimal-nas");
        assertThat(result.getShortName()).isNull();
        assertThat(result.getSecret()).isNull();
    }

    @Test
    void convert_RoundTrip_ShouldPreserveData() {
        // Arrange
        NasDto originalDto = new NasDto();
        originalDto.setNasName("roundtrip-test");
        originalDto.setSecret("roundtrip-secret");

        DtoToNasConverter dtoToNas = new DtoToNasConverter();
        NasToDtoConverter nasToDto = new NasToDtoConverter();

        // Act
        Nas nas = dtoToNas.convert(originalDto);
        NasDto resultDto = nasToDto.convert(nas);

        // Assert
        assertThat(resultDto.getNasName()).isEqualTo(originalDto.getNasName());
        assertThat(resultDto.getSecret()).isEqualTo(originalDto.getSecret());
    }
}
