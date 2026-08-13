package ElFabrica.Wallet_pay.transaction.service;

import ElFabrica.Wallet_pay.transaction.domain.TransactionStatus;
import ElFabrica.Wallet_pay.transaction.domain.TransactionType;
import java.time.Instant;
import java.util.UUID;

public record TransferResult(
        UUID transactionId,
        TransactionType type,
        TransactionStatus status,
        String amount,
        String currency,
        UUID senderWalletId,
        UUID receiverWalletId,
        TransferSenderWalletResult senderWallet,
        Instant createdAt,
        Instant completedAt
) {
}
