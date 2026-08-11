package ElFabrica.Wallet_pay.user.controllers;

import java.time.Instant;
import java.util.UUID;

public record CreateUserResponse(
        UUID id,
        String name,
        String email,
        boolean emailVerified,
        Instant createdAt
) {
}
