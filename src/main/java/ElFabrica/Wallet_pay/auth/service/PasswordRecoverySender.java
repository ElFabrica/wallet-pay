package ElFabrica.Wallet_pay.auth.service;

public interface PasswordRecoverySender {

    void sendPasswordRecovery(String email, String token);
}
