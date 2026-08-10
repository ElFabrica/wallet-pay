package ElFabrica.Wallet_pay.wallet.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ElFabrica.Wallet_pay.user.domain.UserEntity;
import ElFabrica.Wallet_pay.wallet.domain.WalletEntity;
import ElFabrica.Wallet_pay.wallet.infra.WalletRepository;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetWalletBalanceUseCaseTest {

    @Test
    void shouldReturnWalletBalanceForAuthenticatedUserAsString() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        Instant updatedAt = Instant.parse("2026-08-06T10:30:00Z");
        WalletEntity wallet = wallet(userId, walletId, updatedAt);
        FakeWalletRepository walletRepository = new FakeWalletRepository(wallet);
        GetWalletBalanceUseCase useCase = new GetWalletBalanceUseCase(walletRepository.proxy());

        WalletBalanceResult result = useCase.getBalance(userId);

        assertThat(walletRepository.userIdSearched).isEqualTo(userId);
        assertThat(result.walletId()).isEqualTo(walletId);
        assertThat(result.balance()).isEqualTo("0.00");
        assertThat(result.currency()).isEqualTo("BRL");
        assertThat(result.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void shouldThrowNotFoundWhenAuthenticatedUserHasNoWallet() {
        UUID userId = UUID.randomUUID();
        GetWalletBalanceUseCase useCase = new GetWalletBalanceUseCase(new FakeWalletRepository(null).proxy());

        assertThatThrownBy(() -> useCase.getBalance(userId))
                .isInstanceOf(WalletNotFoundException.class);
    }

    private static WalletEntity wallet(UUID userId, UUID walletId, Instant updatedAt) {
        UserEntity user = new UserEntity("Joao Silva", "joao@email.com", "hash", "52998224725");
        setField(user, "id", userId);
        WalletEntity wallet = new WalletEntity(user);
        setField(wallet, "id", walletId);
        setField(wallet, "updatedAt", updatedAt);
        return wallet;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static final class FakeWalletRepository {

        private final WalletEntity wallet;
        private UUID userIdSearched;

        private FakeWalletRepository(WalletEntity wallet) {
            this.wallet = wallet;
        }

        private WalletRepository proxy() {
            return (WalletRepository) Proxy.newProxyInstance(
                    WalletRepository.class.getClassLoader(),
                    new Class<?>[] {WalletRepository.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "findByUserId" -> {
                            userIdSearched = (UUID) args[0];
                            yield Optional.ofNullable(wallet);
                        }
                        default -> throw new UnsupportedOperationException(method.getName());
                    }
            );
        }
    }
}
