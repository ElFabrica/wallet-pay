package ElFabrica.Wallet_pay.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import ElFabrica.Wallet_pay.user.domain.UserEntity;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class AccessTokenServiceTest {

    @Test
    void shouldIssueSignedJwtWithUserIdIssuedAtAndTwentyMinuteExpiration() {
        String secret = "wallet-pay-test-secret-with-32-bytes";
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        AccessTokenService accessTokenService = new AccessTokenService(new NimbusJwtEncoder(new ImmutableSecret<>(secretKey)));
        UserEntity user = new UserEntity("Joao Silva", "joao@email.com", "hash", "52998224725");
        UUID userId = UUID.randomUUID();
        setField(user, "id", userId);

        AccessTokenResult result = accessTokenService.issue(user);

        Jwt jwt = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build()
                .decode(result.accessToken());
        assertThat(result.expiresIn()).isEqualTo(1200);
        assertThat(jwt.getSubject()).isEqualTo(userId.toString());
        assertThat(jwt.getIssuedAt()).isNotNull();
        assertThat(jwt.getExpiresAt()).isNotNull();
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()).toSeconds()).isEqualTo(1200);
        assertThat(jwt.getClaims()).doesNotContainKeys("password", "passwordHash", "document");
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
