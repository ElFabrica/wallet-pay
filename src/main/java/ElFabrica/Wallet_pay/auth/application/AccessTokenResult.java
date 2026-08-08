package ElFabrica.Wallet_pay.auth.application;

public record AccessTokenResult(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
