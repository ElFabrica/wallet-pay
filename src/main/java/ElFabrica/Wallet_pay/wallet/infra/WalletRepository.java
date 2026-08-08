package ElFabrica.Wallet_pay.wallet.infra;

import ElFabrica.Wallet_pay.wallet.domain.WalletEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
}
