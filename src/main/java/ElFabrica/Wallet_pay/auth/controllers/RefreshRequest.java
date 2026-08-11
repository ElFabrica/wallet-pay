package ElFabrica.Wallet_pay.auth.controllers;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "Refresh token e obrigatorio")
        String refreshToken
) {
}
