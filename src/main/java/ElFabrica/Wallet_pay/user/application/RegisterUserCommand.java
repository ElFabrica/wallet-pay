package ElFabrica.Wallet_pay.user.application;

public record RegisterUserCommand(
        String name,
        String email,
        String password,
        String document
) {
}
