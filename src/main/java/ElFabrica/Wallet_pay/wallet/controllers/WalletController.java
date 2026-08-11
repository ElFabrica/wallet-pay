package ElFabrica.Wallet_pay.wallet.controllers;

import ElFabrica.Wallet_pay.wallet.dto.WalletBalanceResponseDTO;
import ElFabrica.Wallet_pay.wallet.service.GetWalletBalanceUseCase;
import ElFabrica.Wallet_pay.wallet.service.WalletBalanceResult;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final GetWalletBalanceUseCase getWalletBalanceUseCase;

    public WalletController(GetWalletBalanceUseCase getWalletBalanceUseCase) {
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
    }

    @GetMapping("/me/balance")
    ResponseEntity<WalletBalanceResponseDTO> getMyBalance(@AuthenticationPrincipal Jwt jwt) {
        WalletBalanceResult result = getWalletBalanceUseCase.getBalance(UUID.fromString(jwt.getSubject()));

        return ResponseEntity.ok(new WalletBalanceResponseDTO(
                result.walletId(),
                result.balance(),
                result.currency(),
                result.updatedAt()
        ));
    }
}
