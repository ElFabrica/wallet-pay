package ElFabrica.Wallet_pay.auth.service;

public record AuthTokenResult(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken
) {
}
