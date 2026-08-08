package ElFabrica.Wallet_pay.user.application;

public class DuplicateUserDataException extends RuntimeException {

    public DuplicateUserDataException(String message) {
        super(message);
    }
}
