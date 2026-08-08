package ElFabrica.Wallet_pay.auth.application;

public record AuthTokenResult(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken
) {
}
