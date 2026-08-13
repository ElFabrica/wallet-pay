package ElFabrica.Wallet_pay.transaction.repository;

import ElFabrica.Wallet_pay.transaction.domain.TransactionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
