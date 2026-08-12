package ElFabrica.Wallet_pay.auth.service;

public class InvalidPasswordPolicyException extends RuntimeException {

    public InvalidPasswordPolicyException(String message) {
        super(message);
    }
}
