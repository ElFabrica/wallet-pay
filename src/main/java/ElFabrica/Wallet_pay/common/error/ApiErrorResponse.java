package ElFabrica.Wallet_pay.common.error;

import java.util.Map;

public record ApiErrorResponse(
        int status,
        String error,
        String code,
        String message,
        Map<String, String> fields
) {
    public ApiErrorResponse(int status, String error, String message) {
        this(status, error, null, message, Map.of());
    }

    public ApiErrorResponse(int status, String error, String message, Map<String, String> fields) {
        this(status, error, null, message, fields);
    }

    public ApiErrorResponse(int status, String error, String code, String message) {
        this(status, error, code, message, Map.of());
    }
}
