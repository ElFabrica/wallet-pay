package ElFabrica.Wallet_pay.auth.service;

public interface EmailVerificationSender {

    void sendVerification(String email, String token);
}
