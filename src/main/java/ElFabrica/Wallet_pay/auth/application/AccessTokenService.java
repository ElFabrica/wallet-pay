package ElFabrica.Wallet_pay.auth.application;

import ElFabrica.Wallet_pay.user.domain.UserEntity;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenService {

    public static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(20);
    public static final long ACCESS_TOKEN_EXPIRES_IN_SECONDS = ACCESS_TOKEN_TTL.toSeconds();
    private static final String TOKEN_TYPE = "Bearer";

    private final JwtEncoder jwtEncoder;

    public AccessTokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public AccessTokenResult issue(UserEntity user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(ACCESS_TOKEN_TTL);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new AccessTokenResult(token, TOKEN_TYPE, ACCESS_TOKEN_EXPIRES_IN_SECONDS);
    }
}
