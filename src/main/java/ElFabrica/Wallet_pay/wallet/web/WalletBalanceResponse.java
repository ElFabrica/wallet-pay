package ElFabrica.Wallet_pay.wallet.web;

import java.time.Instant;
import java.util.UUID;

public record WalletBalanceResponse(
        UUID walletId,
        String balance,
        String currency,
        Instant updatedAt
) {
}
