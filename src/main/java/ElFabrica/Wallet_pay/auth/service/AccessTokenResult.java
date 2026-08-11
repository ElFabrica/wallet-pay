package ElFabrica.Wallet_pay.auth.service;

public record AccessTokenResult(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
