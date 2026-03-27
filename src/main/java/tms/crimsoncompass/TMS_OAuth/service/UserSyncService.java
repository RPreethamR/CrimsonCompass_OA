package tms.crimsoncompass.TMS_OAuth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import tms.crimsoncompass.TMS_OAuth.client.TmsUserClient;
import tms.crimsoncompass.TMS_OAuth.dto.UserSyncRequest;

/**
 * Synchronizes authenticated OAuth user data with the TMS main backend
 * via a Feign client call to {@code POST /api/users/sync}.
 */
@Service
public class UserSyncService {

    private static final Logger log = LoggerFactory.getLogger(UserSyncService.class);

    private final TmsUserClient tmsUserClient;

    public UserSyncService(TmsUserClient tmsUserClient) {
        this.tmsUserClient = tmsUserClient;
    }

    /**
     * Extracts user attributes from the OAuth2 principal and syncs them
     * to the TMS service. Failures are logged but do not block the auth flow.
     */
    public void syncUser(OAuth2User oauthUser) {
        UserSyncRequest request = new UserSyncRequest(
                (String) oauthUser.getAttribute("authId"),
                (String) oauthUser.getAttribute("email"),
                (String) oauthUser.getAttribute("name")
        );

        try {
            tmsUserClient.syncUser(request);
            log.info("User synced successfully: authId={}", request.authId());
        } catch (Exception e) {
            log.error("Failed to sync user authId={}: {}", request.authId(), e.getMessage(), e);
        }
    }
}
