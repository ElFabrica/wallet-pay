package ElFabrica.Wallet_pay.auth.controllers;

public record RefreshResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
