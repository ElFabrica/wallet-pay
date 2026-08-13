package ElFabrica.Wallet_pay.transaction.dto;

import ElFabrica.Wallet_pay.transaction.domain.TransactionStatus;
import ElFabrica.Wallet_pay.transaction.domain.TransactionType;
import java.time.Instant;
import java.util.UUID;

public record TransferResponseDTO(
        UUID transactionId,
        TransactionType type,
        TransactionStatus status,
        String amount,
        String currency,
        UUID senderWalletId,
        UUID receiverWalletId,
        TransferSenderWalletResponseDTO senderWallet,
        Instant createdAt,
        Instant completedAt
) {
}
