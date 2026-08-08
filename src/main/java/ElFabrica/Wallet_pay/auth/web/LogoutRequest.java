package ElFabrica.Wallet_pay.auth.web;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Refresh token e obrigatorio")
        String refreshToken
) {
}
