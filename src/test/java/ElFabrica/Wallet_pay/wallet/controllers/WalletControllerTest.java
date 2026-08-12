package ElFabrica.Wallet_pay.wallet.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ElFabrica.Wallet_pay.config.SecurityConfig;
import ElFabrica.Wallet_pay.wallet.service.GetWalletBalanceUseCase;
import ElFabrica.Wallet_pay.wallet.service.WalletBalanceResult;
import ElFabrica.Wallet_pay.wallet.service.WalletNotFoundException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WalletController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "wallet-pay.auth.jwt-secret=wallet-pay-test-secret-with-32-bytes")
class WalletControllerTest {

    private static final String SECRET = "wallet-pay-test-secret-with-32-bytes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FakeGetWalletBalanceUseCase getWalletBalanceUseCase;

    @BeforeEach
    void setUp() {
        getWalletBalanceUseCase.result = null;
        getWalletBalanceUseCase.exception = null;
        getWalletBalanceUseCase.userId = null;
    }

    @Test
    void shouldReturnAuthenticatedUserBalance() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        Instant updatedAt = Instant.parse("2026-08-06T10:30:00Z");
        getWalletBalanceUseCase.result = new WalletBalanceResult(walletId, "0.00", "BRL", updatedAt);

        mockMvc.perform(get("/wallets/me/balance")
                        .header("Authorization", "Bearer " + token(userId, Instant.now().plusSeconds(60))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value("0.00"))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.updatedAt").value("2026-08-06T10:30:00Z"));

        org.assertj.core.api.Assertions.assertThat(getWalletBalanceUseCase.userId).isEqualTo(userId);
    }

    @Test
    void shouldRejectBalanceQueryWithoutJwt() throws Exception {
        mockMvc.perform(get("/wallets/me/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectBalanceQueryWithExpiredJwt() throws Exception {
        mockMvc.perform(get("/wallets/me/balance")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), Instant.now().minusSeconds(1))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFoundWhenAuthenticatedUserHasNoWallet() throws Exception {
        getWalletBalanceUseCase.exception =
                new WalletNotFoundException("Carteira nao encontrada para o usuario autenticado");

        mockMvc.perform(get("/wallets/me/balance")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), Instant.now().plusSeconds(60))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    private static String token(UUID userId, Instant expiresAt) throws Exception {
        Instant issuedAt = expiresAt.minusSeconds(60);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(new MACSigner(SECRET));
        return jwt.serialize();
    }

    @TestConfiguration
    static class TestBeans {

        @Bean
        FakeGetWalletBalanceUseCase fakeGetWalletBalanceUseCase() {
            return new FakeGetWalletBalanceUseCase();
        }
    }

    static class FakeGetWalletBalanceUseCase extends GetWalletBalanceUseCase {

        private UUID userId;
        private WalletBalanceResult result;
        private RuntimeException exception;

        FakeGetWalletBalanceUseCase() {
            super(null);
        }

        @Override
        public WalletBalanceResult getBalance(UUID userId) {
            this.userId = userId;
            if (exception != null) {
                throw exception;
            }
            return result;
        }
    }
}
