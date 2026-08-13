package ElFabrica.Wallet_pay.transaction.dto;

import java.time.Instant;
import java.util.UUID;

public record TransferSenderWalletResponseDTO(
        UUID id,
        String balance,
        String currency,
        Instant updatedAt
) {
}
