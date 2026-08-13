package ElFabrica.Wallet_pay.transaction.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ElFabrica.Wallet_pay.config.SecurityConfig;
import ElFabrica.Wallet_pay.transaction.domain.TransactionStatus;
import ElFabrica.Wallet_pay.transaction.domain.TransactionType;
import ElFabrica.Wallet_pay.transaction.service.CreateTransferUseCase;
import ElFabrica.Wallet_pay.transaction.service.InsufficientBalanceException;
import ElFabrica.Wallet_pay.transaction.service.TransferCommand;
import ElFabrica.Wallet_pay.transaction.service.TransferResult;
import ElFabrica.Wallet_pay.transaction.service.TransferSenderWalletResult;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
@Import({SecurityConfig.class, TransactionControllerTest.TestBeans.class})
@TestPropertySource(properties = "wallet-pay.auth.jwt-secret=wallet-pay-test-secret-with-32-bytes")
class TransactionControllerTest {

    private static final String SECRET = "wallet-pay-test-secret-with-32-bytes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FakeCreateTransferUseCase createTransferUseCase;

    @BeforeEach
    void setUp() {
        createTransferUseCase.command = null;
        createTransferUseCase.result = null;
        createTransferUseCase.exception = null;
    }

    @Test
    void shouldCreateAuthenticatedTransfer() throws Exception {
        UUID senderUserId = UUID.randomUUID();
        UUID receiverUserId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UUID senderWalletId = UUID.randomUUID();
        UUID receiverWalletId = UUID.randomUUID();
        Instant now = Instant.parse("2026-08-06T10:30:00Z");
        createTransferUseCase.result = new TransferResult(
                transactionId,
                TransactionType.TRANSFER,
                TransactionStatus.COMPLETED,
                "25.50",
                "BRL",
                senderWalletId,
                receiverWalletId,
                new TransferSenderWalletResult(senderWalletId, "74.50", "BRL", now),
                now,
                now
        );

        mockMvc.perform(post("/transactions/transfers")
                        .header("Authorization", "Bearer " + token(senderUserId, Instant.now().plusSeconds(60)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverUserId": "%s",
                                  "amount": "25.50",
                                  "description": "Almoco"
                                }
                                """.formatted(receiverUserId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$.type").value("TRANSFER"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.amount").value("25.50"))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.senderWalletId").value(senderWalletId.toString()))
                .andExpect(jsonPath("$.receiverWalletId").value(receiverWalletId.toString()))
                .andExpect(jsonPath("$.senderWallet.id").value(senderWalletId.toString()))
                .andExpect(jsonPath("$.senderWallet.balance").value("74.50"))
                .andExpect(jsonPath("$.senderWallet.currency").value("BRL"))
                .andExpect(jsonPath("$.senderWallet.updatedAt").value("2026-08-06T10:30:00Z"))
                .andExpect(jsonPath("$.createdAt").value("2026-08-06T10:30:00Z"))
                .andExpect(jsonPath("$.completedAt").value("2026-08-06T10:30:00Z"));

        assertThat(createTransferUseCase.command.senderUserId()).isEqualTo(senderUserId);
        assertThat(createTransferUseCase.command.receiverUserId()).isEqualTo(receiverUserId);
        assertThat(createTransferUseCase.command.amount()).isEqualTo("25.50");
        assertThat(createTransferUseCase.command.description()).isEqualTo("Almoco");
    }

    @Test
    void shouldRejectTransferWithoutJwt() throws Exception {
        mockMvc.perform(post("/transactions/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverUserId": "%s",
                                  "amount": "25.50"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectTransferWithExpiredJwt() throws Exception {
        mockMvc.perform(post("/transactions/transfers")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), Instant.now().minusSeconds(1)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverUserId": "%s",
                                  "amount": "25.50"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        mockMvc.perform(post("/transactions/transfers")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), Instant.now().plusSeconds(60)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.receiverUserId").exists())
                .andExpect(jsonPath("$.fields.amount").exists());
    }

    @Test
    void shouldReturnNotFoundWhenWalletIsMissing() throws Exception {
        createTransferUseCase.exception = new WalletNotFoundException("Carteira de destino nao encontrada");

        mockMvc.perform(post("/transactions/transfers")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), Instant.now().plusSeconds(60)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverUserId": "%s",
                                  "amount": "25.50"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnUnprocessableEntityForInsufficientBalance() throws Exception {
        createTransferUseCase.exception = new InsufficientBalanceException("Saldo insuficiente");

        mockMvc.perform(post("/transactions/transfers")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), Instant.now().plusSeconds(60)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverUserId": "%s",
                                  "amount": "25.50"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Entity"));
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
        FakeCreateTransferUseCase fakeCreateTransferUseCase() {
            return new FakeCreateTransferUseCase();
        }
    }

    static class FakeCreateTransferUseCase extends CreateTransferUseCase {

        private TransferCommand command;
        private TransferResult result;
        private RuntimeException exception;

        FakeCreateTransferUseCase() {
            super(null, null);
        }

        @Override
        public TransferResult transfer(TransferCommand command) {
            this.command = command;
            if (exception != null) {
                throw exception;
            }
            return result;
        }
    }
}
