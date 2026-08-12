package ElFabrica.Wallet_pay.auth.service;

public class PasswordResetTokenException extends RuntimeException {

    private final String code;

    public PasswordResetTokenException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
