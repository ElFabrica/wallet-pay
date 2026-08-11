package ElFabrica.Wallet_pay.auth.service;

import ElFabrica.Wallet_pay.user.domain.UserEntity;

public interface EmailVerificationTokenIssuer {

    void issueFor(UserEntity user);
}
