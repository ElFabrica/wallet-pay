package ElFabrica.Wallet_pay.user.service;

final class CpfValidator {

    private CpfValidator() {
    }

    static boolean isValid(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || hasOnlyRepeatedDigits(cpf)) {
            return false;
        }

        int firstDigit = calculateDigit(cpf, 9, 10);
        int secondDigit = calculateDigit(cpf, 10, 11);
        return firstDigit == Character.digit(cpf.charAt(9), 10)
                && secondDigit == Character.digit(cpf.charAt(10), 10);
    }

    private static boolean hasOnlyRepeatedDigits(String value) {
        return value.chars().distinct().count() == 1;
    }

    private static int calculateDigit(String cpf, int length, int weight) {
        int sum = 0;
        for (int index = 0; index < length; index++) {
            sum += Character.digit(cpf.charAt(index), 10) * (weight - index);
        }

        int remainder = (sum * 10) % 11;
        return remainder == 10 ? 0 : remainder;
    }
}
