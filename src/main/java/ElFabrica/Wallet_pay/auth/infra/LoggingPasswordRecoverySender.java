package ElFabrica.Wallet_pay.auth.infra;

import ElFabrica.Wallet_pay.auth.service.PasswordRecoverySender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingPasswordRecoverySender implements PasswordRecoverySender {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingPasswordRecoverySender.class);

    @Override
    public void sendPasswordRecovery(String email, String token) {
        LOGGER.info("Password recovery requested for {} with token {}", email, token);
    }
}
