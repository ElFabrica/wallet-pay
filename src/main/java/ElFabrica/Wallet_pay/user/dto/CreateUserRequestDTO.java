package ElFabrica.Wallet_pay.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDTO(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
        String name,

        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail deve ter formato valido")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 8, message = "Senha deve ter pelo menos 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "Senha deve conter pelo menos uma letra e um numero"
        )
        String password,

        @NotBlank(message = "Documento e obrigatorio")
        @Pattern(regexp = "^\\d+$", message = "Documento deve conter apenas numeros")
        String document
) {
}
