package ElFabrica.Wallet_pay.auth.dto;

public record RefreshResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
