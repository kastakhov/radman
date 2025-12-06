package software.netcore.radman.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import software.netcore.radman.data.internal.repo.SystemUserRepo;
import software.netcore.radman.security.fallback.FallbackAuthenticationProvider;
import software.netcore.radman.security.fallback.SingleUserDetailsManager;
import software.netcore.radman.security.fallback.SingleUserDetailsManagerImpl;
import software.netcore.radman.security.ldap.LdapProperties;
import software.netcore.radman.security.ldap.LocalLdapAuthoritiesPopulator;
import software.netcore.radman.security.local.LocalAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @since v. 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";

    private final SystemUserRepo systemUserRepo;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
        // Vaadin handles CSRF internally
		http.csrf(csrf -> csrf.disable())
				// Register our CustomRequestCache, that saves unauthorized access attempts, so
				// the user is redirected after login.
				.requestCache(cache -> cache.requestCache(new VaadinRequestCache()))
				// Restrict access to our application.
				.authorizeHttpRequests(auth -> auth
				    // Allow static resources
					.requestMatchers(
							"/VAADIN/**",
							"/favicon.ico",
							"/robots.txt",
							"/manifest.webmanifest",
							"/sw.js",
							"/offline-page.html",
							"/frontend/**",
							"/webjars/**",
							"/frontend-es5/**",
							"/frontend-es6/**")
						.permitAll()
				    // Allow all flow internal requests.
					.requestMatchers(VaadinRequestMatcher::matches)
						.permitAll()
				    // Allow all requests by logged in users.
					.anyRequest()
						.authenticated()
				)
				// Configure the login page.
				.formLogin(form -> form
					.loginPage(LOGIN_URL)
					.permitAll()
                    .successHandler(loginSuccessHandler())
					.failureUrl(LOGIN_FAILURE_URL)
				)
				// Configure logout
				.logout(logout -> logout
					.logoutSuccessUrl(LOGOUT_SUCCESS_URL)
				)
				// Set the authentication manager
				.authenticationManager(authenticationManager());
        //@formatter:on
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        List<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(fallbackAuthenticationProvider());
        providers.add(localAuthenticationProvider());
        
        if (ldapProperties().isEnabled()) {
            providers.add(ldapAuthenticationProvider());
        }
        
        return new ProviderManager(providers);
    }
    
    private AuthenticationProvider ldapAuthenticationProvider() throws Exception {
        LdapProperties props = ldapProperties();
        
        org.springframework.security.ldap.DefaultSpringSecurityContextSource contextSource = 
            new org.springframework.security.ldap.DefaultSpringSecurityContextSource(props.getUrls());
        contextSource.setUserDn(props.getManagerDn());
        contextSource.setPassword(props.getManagerPassword());
        contextSource.afterPropertiesSet();
        
        FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
            props.getSearchBaseDn(), 
            props.getUserSearchFilter(), 
            contextSource
        );
        
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(userSearch);
        
        LdapAuthenticationProvider provider = new LdapAuthenticationProvider(
            authenticator, 
            ldapAuthoritiesPopulator()
        );
        
        return provider;
    }

    @Bean
    LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(systemUserRepo);
    }

    @Bean
    AuthenticationProvider localAuthenticationProvider() {
        return new LocalAuthenticationProvider(systemUserRepo, passwordEncoder());
    }

    @Bean
    AuthenticationProvider fallbackAuthenticationProvider() {
        return new FallbackAuthenticationProvider(fallbackUserDetailsManager());
    }

    @Bean
    SingleUserDetailsManager fallbackUserDetailsManager() {
        return new SingleUserDetailsManagerImpl();
    }

    @Bean
    LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
        return new LocalLdapAuthoritiesPopulator(systemUserRepo);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConfigurationProperties(prefix = "ldap")
    LdapProperties ldapProperties() {
        return new LdapProperties();
    }

}
