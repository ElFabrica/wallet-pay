package ElFabrica.Wallet_pay.auth.application;

public interface EmailVerificationSender {

    void sendVerification(String email, String token);
}
