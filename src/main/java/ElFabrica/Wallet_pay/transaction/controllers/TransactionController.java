package ElFabrica.Wallet_pay.transaction.controllers;

import ElFabrica.Wallet_pay.transaction.dto.TransferRequestDTO;
import ElFabrica.Wallet_pay.transaction.dto.TransferResponseDTO;
import ElFabrica.Wallet_pay.transaction.dto.TransferSenderWalletResponseDTO;
import ElFabrica.Wallet_pay.transaction.service.CreateTransferUseCase;
import ElFabrica.Wallet_pay.transaction.service.TransferCommand;
import ElFabrica.Wallet_pay.transaction.service.TransferResult;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransferUseCase createTransferUseCase;

    public TransactionController(CreateTransferUseCase createTransferUseCase) {
        this.createTransferUseCase = createTransferUseCase;
    }

    @PostMapping("/transfers")
    ResponseEntity<TransferResponseDTO> transfer(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransferRequestDTO request
    ) {
        TransferResult result = createTransferUseCase.transfer(new TransferCommand(
                UUID.fromString(jwt.getSubject()),
                request.receiverUserId(),
                request.amount(),
                request.description()
        ));

        TransferResponseDTO response = new TransferResponseDTO(
                result.transactionId(),
                result.type(),
                result.status(),
                result.amount(),
                result.currency(),
                result.senderWalletId(),
                result.receiverWalletId(),
                new TransferSenderWalletResponseDTO(
                        result.senderWallet().id(),
                        result.senderWallet().balance(),
                        result.senderWallet().currency(),
                        result.senderWallet().updatedAt()
                ),
                result.createdAt(),
                result.completedAt()
        );

        return ResponseEntity
                .created(URI.create("/transactions/" + result.transactionId()))
                .body(response);
    }
}
