package ElFabrica.Wallet_pay.user.application;

import java.time.Instant;
import java.util.UUID;

public record RegisterUserResult(
        UUID id,
        String name,
        String email,
        boolean emailVerified,
        Instant createdAt
) {
}
