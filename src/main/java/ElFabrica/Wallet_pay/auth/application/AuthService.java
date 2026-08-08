package ElFabrica.Wallet_pay.auth.application;

import ElFabrica.Wallet_pay.auth.domain.RefreshTokenEntity;
import ElFabrica.Wallet_pay.auth.infra.RefreshTokenRepository;
import ElFabrica.Wallet_pay.user.domain.UserEntity;
import ElFabrica.Wallet_pay.user.infra.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AccessTokenService accessTokenService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenService = accessTokenService;
    }

    @Transactional
    public AuthTokenResult login(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        UserEntity user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais invalidas"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Credenciais invalidas");
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("E-mail ainda nao verificado");
        }

        AccessTokenResult accessToken = accessTokenService.issue(user);
        RefreshTokenEntity refreshToken = new RefreshTokenEntity(
                UUID.randomUUID().toString(),
                user,
                Instant.now().plus(REFRESH_TOKEN_TTL)
        );
        refreshTokenRepository.save(refreshToken);

        return new AuthTokenResult(
                accessToken.accessToken(),
                accessToken.tokenType(),
                accessToken.expiresIn(),
                refreshToken.getToken()
        );
    }

    @Transactional(readOnly = true)
    public AccessTokenResult refresh(String token) {
        RefreshTokenEntity refreshToken = findValidRefreshToken(token);
        return accessTokenService.issue(refreshToken.getUser());
    }

    @Transactional
    public void logout(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token invalido"));
        refreshToken.invalidate(Instant.now());
    }

    private RefreshTokenEntity findValidRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token invalido"));

        Instant now = Instant.now();
        if (refreshToken.isInvalidated() || refreshToken.isExpired(now)) {
            throw new InvalidRefreshTokenException("Refresh token invalido");
        }

        return refreshToken;
    }
}
