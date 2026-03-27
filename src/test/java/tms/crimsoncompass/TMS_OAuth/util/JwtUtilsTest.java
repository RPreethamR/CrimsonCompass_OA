package tms.crimsoncompass.TMS_OAuth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    // 64-byte Base64-encoded key for HmacSHA512
    private static final String TEST_SECRET =
            "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLWhtYWMtc2hhLTUxMi1hbGdvcml0aG0tNjQtYnl0ZXM=";
    private static final long TEST_EXPIRATION = 3600000L; // 1 hour

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = new JwtUtils();
        setField(jwtUtils, "secret", TEST_SECRET);
        setField(jwtUtils, "expiration", TEST_EXPIRATION);
        // Trigger @PostConstruct manually
        jwtUtils.getClass().getDeclaredMethod("init").invoke(jwtUtils);
    }

    @Test
    @DisplayName("generateToken returns a non-null JWT string")
    void generateToken_returnsNonNull() {
        String token = jwtUtils.generateToken("user-123");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("generated token contains the correct authId as subject")
    void generateToken_containsCorrectSubject() {
        String authId = "google-oauth2-abc";
        String token = jwtUtils.generateToken(authId);

        String extractedAuthId = jwtUtils.getAuthIdFromToken(token);
        assertEquals(authId, extractedAuthId);
    }

    @Test
    @DisplayName("generated token has expiration within expected bounds")
    void generateToken_hasValidExpiration() {
        String token = jwtUtils.generateToken("user-123");

        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(TEST_SECRET));
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();

        long expiresIn = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertEquals(TEST_EXPIRATION, expiresIn);
    }

    @Test
    @DisplayName("validateToken returns true for a valid token")
    void validateToken_validToken() {
        String token = jwtUtils.generateToken("user-123");
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("validateToken returns false for a tampered token")
    void validateToken_tamperedToken() {
        String token = jwtUtils.generateToken("user-123");
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtUtils.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("validateToken returns false for garbage input")
    void validateToken_garbageInput() {
        assertFalse(jwtUtils.validateToken("not.a.jwt"));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
