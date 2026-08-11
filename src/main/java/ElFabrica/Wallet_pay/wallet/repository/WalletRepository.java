package ElFabrica.Wallet_pay.wallet.repository;

import ElFabrica.Wallet_pay.wallet.domain.WalletEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {

    Optional<WalletEntity> findByUserId(UUID userId);
}
