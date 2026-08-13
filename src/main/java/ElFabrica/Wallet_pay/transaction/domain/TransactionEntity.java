package ElFabrica.Wallet_pay.transaction.domain;

import ElFabrica.Wallet_pay.wallet.domain.WalletEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_wallet_id", nullable = false)
    private WalletEntity senderWallet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_wallet_id", nullable = false)
    private WalletEntity receiverWallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    protected TransactionEntity() {
    }

    private TransactionEntity(
            WalletEntity senderWallet,
            WalletEntity receiverWallet,
            BigDecimal amount,
            String description
    ) {
        this.senderWallet = senderWallet;
        this.receiverWallet = receiverWallet;
        this.type = TransactionType.TRANSFER;
        this.status = TransactionStatus.COMPLETED;
        this.amount = amount;
        this.currency = WalletEntity.DEFAULT_CURRENCY;
        this.description = description;
        Instant now = Instant.now();
        this.createdAt = now;
        this.completedAt = now;
    }

    public static TransactionEntity completedTransfer(
            WalletEntity senderWallet,
            WalletEntity receiverWallet,
            BigDecimal amount,
            String description
    ) {
        return new TransactionEntity(senderWallet, receiverWallet, amount, description);
    }

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.completedAt == null && this.status == TransactionStatus.COMPLETED) {
            this.completedAt = this.createdAt;
        }
    }

    public UUID getId() {
        return id;
    }

    public WalletEntity getSenderWallet() {
        return senderWallet;
    }

    public WalletEntity getReceiverWallet() {
        return receiverWallet;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
