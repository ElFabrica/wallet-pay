package ElFabrica.Wallet_pay.auth.web;

public record RefreshResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
