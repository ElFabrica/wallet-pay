package ElFabrica.Wallet_pay.wallet.dto;

import java.time.Instant;
import java.util.UUID;

public record WalletBalanceResponseDTO(
        UUID walletId,
        String balance,
        String currency,
        Instant updatedAt
) {
}
