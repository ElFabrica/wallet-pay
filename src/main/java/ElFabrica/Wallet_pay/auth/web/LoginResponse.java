package ElFabrica.Wallet_pay.auth.web;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken
) {
}
