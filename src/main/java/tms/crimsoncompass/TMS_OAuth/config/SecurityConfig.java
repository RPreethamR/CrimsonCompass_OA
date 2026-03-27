package tms.crimsoncompass.TMS_OAuth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import tms.crimsoncompass.TMS_OAuth.service.CustomOAuth2UserService;
import tms.crimsoncompass.TMS_OAuth.service.OAuth2AuthenticationSuccessHandler;

/**
 * Spring Security configuration for the OAuth microservice.
 *
 * <ul>
 *   <li>CSRF is disabled — this service issues stateless JWTs and does not use sessions.</li>
 *   <li>CORS is delegated to {@link CorsConfig}.</li>
 *   <li>{@code /health} is publicly accessible for container readiness probes.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          OAuth2AuthenticationSuccessHandler successHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS — uses the CorsConfig WebMvcConfigurer bean
                .cors(Customizer.withDefaults())
                // CSRF disabled: stateless JWT auth, no server-side sessions
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/oauth2/**", "/login/**", "/health").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(successHandler)
                );
        return http.build();
    }
}
