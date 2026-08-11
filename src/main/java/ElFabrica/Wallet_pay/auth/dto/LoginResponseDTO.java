package ElFabrica.Wallet_pay.auth.dto;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken
) {
}
