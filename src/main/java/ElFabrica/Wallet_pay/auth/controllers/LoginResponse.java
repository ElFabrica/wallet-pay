package ElFabrica.Wallet_pay.auth.controllers;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken
) {
}
