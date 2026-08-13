package ElFabrica.Wallet_pay.transaction.service;

public class InvalidTransferException extends RuntimeException {

    public InvalidTransferException(String message) {
        super(message);
    }
}
