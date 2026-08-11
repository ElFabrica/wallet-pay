package ElFabrica.Wallet_pay.auth.application;

public class EmailVerificationTokenException extends RuntimeException {

    private final String code;

    public EmailVerificationTokenException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
