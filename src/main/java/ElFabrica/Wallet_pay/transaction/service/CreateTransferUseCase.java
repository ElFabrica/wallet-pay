package ElFabrica.Wallet_pay.transaction.service;

import ElFabrica.Wallet_pay.transaction.domain.TransactionEntity;
import ElFabrica.Wallet_pay.transaction.repository.TransactionRepository;
import ElFabrica.Wallet_pay.wallet.domain.WalletEntity;
import ElFabrica.Wallet_pay.wallet.repository.WalletRepository;
import ElFabrica.Wallet_pay.wallet.service.WalletNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTransferUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public CreateTransferUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransferResult transfer(TransferCommand command) {
        if (command.senderUserId().equals(command.receiverUserId())) {
            throw new InvalidTransferException("Usuario nao pode transferir para si mesmo");
        }

        BigDecimal amount = parseAmount(command.amount());
        WalletEntity senderWallet = walletRepository.findByUserId(command.senderUserId())
                .orElseThrow(() -> new WalletNotFoundException("Carteira de origem nao encontrada"));
        WalletEntity receiverWallet = walletRepository.findByUserId(command.receiverUserId())
                .orElseThrow(() -> new WalletNotFoundException("Carteira de destino nao encontrada"));

        if (!WalletEntity.DEFAULT_CURRENCY.equals(senderWallet.getCurrency())
                || !WalletEntity.DEFAULT_CURRENCY.equals(receiverWallet.getCurrency())) {
            throw new InvalidTransferException("Moeda da transferencia deve ser BRL");
        }

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        senderWallet.debit(amount);
        receiverWallet.credit(amount);

        TransactionEntity transaction = transactionRepository.saveAndFlush(TransactionEntity.completedTransfer(
                senderWallet,
                receiverWallet,
                amount,
                normalizeDescription(command.description())
        ));

        return new TransferResult(
                transaction.getId(),
                transaction.getType(),
                transaction.getStatus(),
                money(transaction.getAmount()),
                transaction.getCurrency(),
                senderWallet.getId(),
                receiverWallet.getId(),
                new TransferSenderWalletResult(
                        senderWallet.getId(),
                        money(senderWallet.getBalance()),
                        senderWallet.getCurrency(),
                        senderWallet.getUpdatedAt()
                ),
                transaction.getCreatedAt(),
                transaction.getCompletedAt()
        );
    }

    private BigDecimal parseAmount(String rawAmount) {
        if (rawAmount == null || rawAmount.isBlank()) {
            throw new InvalidTransferException("Valor e obrigatorio");
        }

        try {
            BigDecimal amount = new BigDecimal(rawAmount.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTransferException("Valor deve ser maior que zero");
            }
            if (amount.scale() > 2) {
                throw new InvalidTransferException("Valor deve ter no maximo duas casas decimais");
            }
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (NumberFormatException exception) {
            throw new InvalidTransferException("Valor invalido");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }

    private String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.UNNECESSARY).toPlainString();
    }
}
