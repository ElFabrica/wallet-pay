package ElFabrica.Wallet_pay.wallet.service;

import ElFabrica.Wallet_pay.wallet.domain.WalletEntity;
import ElFabrica.Wallet_pay.wallet.repository.WalletRepository;
import java.math.RoundingMode;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetWalletBalanceUseCase {

    private final WalletRepository walletRepository;

    public GetWalletBalanceUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public WalletBalanceResult getBalance(UUID userId) {
        WalletEntity wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Carteira nao encontrada para o usuario autenticado"));

        return new WalletBalanceResult(
                wallet.getId(),
                wallet.getBalance().setScale(2, RoundingMode.UNNECESSARY).toPlainString(),
                wallet.getCurrency(),
                wallet.getUpdatedAt()
        );
    }
}
