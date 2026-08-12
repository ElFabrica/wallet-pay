package ElFabrica.Wallet_pay.auth.repository;

import ElFabrica.Wallet_pay.auth.domain.RefreshTokenEntity;
import ElFabrica.Wallet_pay.user.domain.UserEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("""
            update RefreshTokenEntity token
            set token.invalidatedAt = :invalidatedAt,
                token.updatedAt = :invalidatedAt
            where token.user = :user
              and token.invalidatedAt is null
            """)
    void invalidateActiveTokensByUser(UserEntity user, Instant invalidatedAt);
}
