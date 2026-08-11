package ElFabrica.Wallet_pay.auth.repository;

import ElFabrica.Wallet_pay.auth.domain.EmailVerificationTokenEntity;
import ElFabrica.Wallet_pay.user.domain.UserEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, UUID> {

    Optional<EmailVerificationTokenEntity> findByToken(String token);

    @Modifying
    @Query("""
            update EmailVerificationTokenEntity token
            set token.usedAt = :usedAt
            where token.user = :user
              and token.usedAt is null
            """)
    void markPendingTokensAsUsed(UserEntity user, Instant usedAt);
}
