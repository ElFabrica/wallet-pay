package ElFabrica.Wallet_pay.auth.controllers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationResendRequest(
        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail deve ter formato valido")
        String email
) {
}
