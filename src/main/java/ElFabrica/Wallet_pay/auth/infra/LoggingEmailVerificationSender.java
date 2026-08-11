package ElFabrica.Wallet_pay.auth.infra;

import ElFabrica.Wallet_pay.auth.service.EmailVerificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEmailVerificationSender implements EmailVerificationSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEmailVerificationSender.class);

    @Override
    public void sendVerification(String email, String token) {
        LOGGER.info("Email verification requested for {} with token {}", email, token);
    }
}
