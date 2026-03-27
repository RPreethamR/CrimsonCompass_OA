package tms.crimsoncompass.TMS_OAuth.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Custom OAuth2 user service that normalizes provider-specific attributes
 * (currently Google) into a consistent internal format.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User user = super.loadUser(userRequest);
        Map<String, Object> attributes = user.getAttributes();

        String authId = (String) attributes.get("sub");
        String email  = (String) attributes.get("email");
        String name   = (String) attributes.get("name");

        if (authId == null || email == null) {
            log.warn("OAuth2 user missing required attributes: sub={}, email={}", authId, email);
        }

        // Map provider attributes to internal names
        Map<String, Object> mappedAttributes = new HashMap<>();
        mappedAttributes.put("authId", authId);
        mappedAttributes.put("email", email);
        mappedAttributes.put("name", name);

        log.debug("Loaded OAuth2 user: email={}", email);
        return new DefaultOAuth2User(user.getAuthorities(), mappedAttributes, "email");
    }
}
