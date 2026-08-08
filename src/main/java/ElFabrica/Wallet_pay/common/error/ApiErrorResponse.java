package ElFabrica.Wallet_pay.common.error;

import java.util.Map;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> fields
) {
    public ApiErrorResponse(int status, String error, String message) {
        this(status, error, message, Map.of());
    }
}
