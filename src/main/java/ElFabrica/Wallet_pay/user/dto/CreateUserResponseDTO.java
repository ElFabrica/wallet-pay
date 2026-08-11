package ElFabrica.Wallet_pay.user.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateUserResponseDTO(
        UUID id,
        String name,
        String email,
        boolean emailVerified,
        Instant createdAt
) {
}
