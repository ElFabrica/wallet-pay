package ElFabrica.Wallet_pay.user.service;

public record RegisterUserCommand(
        String name,
        String email,
        String password,
        String document
) {
}
