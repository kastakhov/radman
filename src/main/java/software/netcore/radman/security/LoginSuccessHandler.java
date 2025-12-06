package software.netcore.radman.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import software.netcore.radman.data.internal.entity.SystemUser;
import software.netcore.radman.data.internal.repo.SystemUserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

/**
 * @since v. 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final SystemUserRepo systemUserRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);

        try {
            SystemUser systemUser = systemUserRepo.findByUsername(String.valueOf(authentication.getPrincipal()));
            systemUser.setLastLoginTime(Instant.now().getEpochSecond());
            systemUserRepo.save(systemUser);
        } catch (Exception ex) {
            log.warn("Failed to update system user '{}' last login time", authentication.getPrincipal());
        }
    }

}
