package ElFabrica.Wallet_pay.auth.controllers;

import ElFabrica.Wallet_pay.auth.service.AccessTokenResult;
import ElFabrica.Wallet_pay.auth.service.AuthService;
import ElFabrica.Wallet_pay.auth.service.AuthTokenResult;
import ElFabrica.Wallet_pay.auth.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService, EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthTokenResult result = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn(),
                result.refreshToken()
        ));
    }

    @PostMapping("/refresh")
    ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        AccessTokenResult result = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(new RefreshResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn()
        ));
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email-verification/resend")
    ResponseEntity<Void> resendEmailVerification(@Valid @RequestBody EmailVerificationResendRequest request) {
        emailVerificationService.resend(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email-verification/confirm")
    ResponseEntity<Void> confirmEmailVerification(@Valid @RequestBody EmailVerificationConfirmRequest request) {
        emailVerificationService.confirm(request.token());
        return ResponseEntity.noContent().build();
    }
}
