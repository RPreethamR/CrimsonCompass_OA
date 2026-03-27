package tms.crimsoncompass.TMS_OAuth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import tms.crimsoncompass.TMS_OAuth.client.TmsUserClient;
import tms.crimsoncompass.TMS_OAuth.dto.UserSyncRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSyncServiceTest {

    @Mock
    private TmsUserClient tmsUserClient;

    @Mock
    private OAuth2User oauthUser;

    @InjectMocks
    private UserSyncService userSyncService;

    @Test
    @DisplayName("syncUser sends correct payload to TMS backend")
    void syncUser_sendsCorrectPayload() {
        when(oauthUser.getAttribute("authId")).thenReturn("google-123");
        when(oauthUser.getAttribute("email")).thenReturn("test@example.com");
        when(oauthUser.getAttribute("name")).thenReturn("Test User");

        userSyncService.syncUser(oauthUser);

        verify(tmsUserClient).syncUser(new UserSyncRequest("google-123", "test@example.com", "Test User"));
    }

    @Test
    @DisplayName("syncUser handles Feign error gracefully without throwing")
    void syncUser_handlesFeignError() {
        when(oauthUser.getAttribute("authId")).thenReturn("google-123");
        when(oauthUser.getAttribute("email")).thenReturn("test@example.com");
        when(oauthUser.getAttribute("name")).thenReturn("Test User");
        doThrow(new RuntimeException("Connection refused")).when(tmsUserClient).syncUser(any());

        // Should not throw — error is caught and logged
        userSyncService.syncUser(oauthUser);

        verify(tmsUserClient).syncUser(any(UserSyncRequest.class));
    }
}
