package ElFabrica.Wallet_pay.auth.repository;

import ElFabrica.Wallet_pay.auth.domain.PasswordResetTokenEntity;
import ElFabrica.Wallet_pay.user.domain.UserEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {

    Optional<PasswordResetTokenEntity> findByToken(String token);

    @Modifying
    @Query("""
            update PasswordResetTokenEntity token
            set token.usedAt = :usedAt
            where token.user = :user
              and token.usedAt is null
            """)
    void markPendingTokensAsUsed(UserEntity user, Instant usedAt);
}
