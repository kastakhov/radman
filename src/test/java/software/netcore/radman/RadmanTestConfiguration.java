package software.netcore.radman;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Test configuration for unit and integration tests
 */
@TestConfiguration
public class RadmanTestConfiguration {

    @Bean
    @Primary
    public DefaultConversionService testConversionService() {
        return new DefaultConversionService();
    }
}
