package ElFabrica.Wallet_pay.user.controllers;

import ElFabrica.Wallet_pay.user.service.RegisterUserCommand;
import ElFabrica.Wallet_pay.user.service.RegisterUserResult;
import ElFabrica.Wallet_pay.user.service.RegisterUserUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        RegisterUserResult result = registerUserUseCase.register(new RegisterUserCommand(
                request.name(),
                request.email(),
                request.password(),
                request.document()
        ));

        CreateUserResponse response = new CreateUserResponse(
                result.id(),
                result.name(),
                result.email(),
                result.emailVerified(),
                result.createdAt()
        );

        return ResponseEntity
                .created(URI.create("/users/" + result.id()))
                .body(response);
    }
}
