package tms.crimsoncompass.TMS_OAuth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tms.crimsoncompass.TMS_OAuth.util.JwtUtils;

import java.io.IOException;

/**
 * Handles successful OAuth2 authentication by:
 * <ol>
 *   <li>Syncing the user to the TMS main service via Feign.</li>
 *   <li>Generating a JWT and setting it as a secure, httpOnly cookie.</li>
 *   <li>Redirecting to the frontend application.</li>
 * </ol>
 */
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final UserSyncService userSyncService;
    private final JwtUtils jwtUtils;

    @Value("${app.redirect.url}")
    private String redirectUrl;

    public OAuth2AuthenticationSuccessHandler(UserSyncService userSyncService, JwtUtils jwtUtils) {
        this.userSyncService = userSyncService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        // Sync user data to the TMS main backend
        userSyncService.syncUser(oauthUser);

        // Generate JWT for the authenticated user
        String authId = (String) oauthUser.getAttribute("authId");
        String token = jwtUtils.generateToken(authId);
        log.info("OAuth2 login successful for authId={}", authId);

        // Set JWT as a secure httpOnly cookie
        Cookie jwtCookie = new Cookie("token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600); // 1 hour, matches JWT expiration
        jwtCookie.setAttribute("SameSite", "Lax");
        response.addCookie(jwtCookie);

        // Also pass token as query param for frontend compatibility
        response.sendRedirect(redirectUrl + "?token=" + token);
    }
}
