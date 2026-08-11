package ElFabrica.Wallet_pay.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDTO(
        @NotBlank(message = "Refresh token e obrigatorio")
        String refreshToken
) {
}
