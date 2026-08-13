package ElFabrica.Wallet_pay.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record TransferRequestDTO(
        @NotNull(message = "Usuario de destino e obrigatorio")
        UUID receiverUserId,

        @NotBlank(message = "Valor e obrigatorio")
        String amount,

        @Size(max = 255, message = "Descricao deve ter no maximo 255 caracteres")
        String description
) {
}
