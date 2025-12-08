package software.netcore.radman;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.data.radius.repo.NasRepo;
import software.netcore.radman.data.internal.repo.RadiusUserRepo;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Application context and smoke tests to ensure the application starts correctly
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RadmanApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void allRequiredBeansArePresent() {
        // Verify critical beans are loaded
        assertThat(applicationContext.getBean(NasService.class)).isNotNull();
        assertThat(applicationContext.getBean(RadiusUserService.class)).isNotNull();
        assertThat(applicationContext.getBean(SecurityService.class)).isNotNull();
        assertThat(applicationContext.getBean(NasRepo.class)).isNotNull();
        assertThat(applicationContext.getBean(RadiusUserRepo.class)).isNotNull();
    }

    @Test
    void databaseConnectionsAreConfigured() {
        // Verify both datasources are configured
        assertThat(applicationContext.containsBean("radmanDataSource")).isTrue();
        assertThat(applicationContext.containsBean("radiusDataSource")).isTrue();
    }

    @Test
    void transactionManagersAreConfigured() {
        // Verify both transaction managers are configured
        assertThat(applicationContext.containsBean("txRadman")).isTrue();
        assertThat(applicationContext.containsBean("txRadius")).isTrue();
    }

    @Test
    void entityManagersAreConfigured() {
        // Verify both entity managers are configured
        assertThat(applicationContext.containsBean("radmanEntityManager")).isTrue();
        assertThat(applicationContext.containsBean("radiusEntityManager")).isTrue();
    }

    @Test
    void conversionServiceIsConfigured() {
        // Verify conversion service is available
        assertThat(applicationContext.containsBean("defaultConversionService")).isTrue();
    }
}
