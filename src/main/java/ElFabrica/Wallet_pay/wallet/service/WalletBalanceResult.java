package ElFabrica.Wallet_pay.wallet.service;

import java.time.Instant;
import java.util.UUID;

public record WalletBalanceResult(
        UUID walletId,
        String balance,
        String currency,
        Instant updatedAt
) {
}
