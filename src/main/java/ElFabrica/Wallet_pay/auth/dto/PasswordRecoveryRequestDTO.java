package ElFabrica.Wallet_pay.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordRecoveryRequestDTO(
        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail deve ter formato valido")
        String email
) {
}
