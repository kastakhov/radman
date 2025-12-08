package software.netcore.radman.buisness.service.nas.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.data.radius.entity.Nas;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for NasToDtoConverter
 */
class NasToDtoConverterTest {

    private NasToDtoConverter converter;

    @BeforeEach
    void setUp() {
        converter = new NasToDtoConverter();
    }

    @Test
    void convert_ShouldConvertNasToDto() {
        // Arrange
        Nas nas = new Nas();
        nas.setId(1);
        nas.setNasName("test-nas");
        nas.setShortName("test");
        nas.setServer("192.168.1.1");
        nas.setType("other");
        nas.setSecret("secret123");
        nas.setPorts(1812);
        nas.setCommunity("public");
        nas.setDescription("Test NAS");

        // Act
        NasDto result = converter.convert(nas);

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
        Nas nas = new Nas();
        nas.setNasName("minimal-nas");

        // Act
        NasDto result = converter.convert(nas);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNasName()).isEqualTo("minimal-nas");
        assertThat(result.getShortName()).isNull();
        assertThat(result.getSecret()).isNull();
    }
}
