package ElFabrica.Wallet_pay.transaction.service;

import java.time.Instant;
import java.util.UUID;

public record TransferSenderWalletResult(
        UUID id,
        String balance,
        String currency,
        Instant updatedAt
) {
}
