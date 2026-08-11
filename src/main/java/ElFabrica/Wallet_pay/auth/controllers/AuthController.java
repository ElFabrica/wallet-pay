package ElFabrica.Wallet_pay.auth.controllers;

import ElFabrica.Wallet_pay.auth.dto.EmailVerificationConfirmRequestDTO;
import ElFabrica.Wallet_pay.auth.dto.EmailVerificationResendRequestDTO;
import ElFabrica.Wallet_pay.auth.dto.LoginRequestDTO;
import ElFabrica.Wallet_pay.auth.dto.LoginResponseDTO;
import ElFabrica.Wallet_pay.auth.dto.LogoutRequestDTO;
import ElFabrica.Wallet_pay.auth.dto.RefreshRequestDTO;
import ElFabrica.Wallet_pay.auth.dto.RefreshResponseDTO;
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
    ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthTokenResult result = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponseDTO(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn(),
                result.refreshToken()
        ));
    }

    @PostMapping("/refresh")
    ResponseEntity<RefreshResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO request) {
        AccessTokenResult result = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(new RefreshResponseDTO(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn()
        ));
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequestDTO request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email-verification/resend")
    ResponseEntity<Void> resendEmailVerification(@Valid @RequestBody EmailVerificationResendRequestDTO request) {
        emailVerificationService.resend(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email-verification/confirm")
    ResponseEntity<Void> confirmEmailVerification(@Valid @RequestBody EmailVerificationConfirmRequestDTO request) {
        emailVerificationService.confirm(request.token());
        return ResponseEntity.noContent().build();
    }
}
