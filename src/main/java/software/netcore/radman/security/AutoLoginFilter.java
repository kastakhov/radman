package software.netcore.radman.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import software.netcore.radman.data.internal.entity.Role;

import java.io.IOException;

/**
 * Filter that automatically authenticates users as admin when auto-login is enabled.
 * 
 * @since v. 1.0.4
 */
public class AutoLoginFilter extends OncePerRequestFilter {

    private final boolean autoLoginEnabled;
    private final String autoLoginUsername;

    public AutoLoginFilter(boolean autoLoginEnabled, String autoLoginUsername) {
        this.autoLoginEnabled = autoLoginEnabled;
        this.autoLoginUsername = autoLoginUsername;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        if (autoLoginEnabled && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Create an authenticated token with admin role
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    autoLoginUsername, 
                    null, 
                    RoleAuthority.asCollection(new RoleAuthority(Role.ADMIN))
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
}
