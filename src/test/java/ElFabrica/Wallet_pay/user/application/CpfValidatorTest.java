package ElFabrica.Wallet_pay.user.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CpfValidatorTest {

    @Test
    void shouldAcceptValidCpf() {
        assertThat(CpfValidator.isValid("52998224725")).isTrue();
    }

    @Test
    void shouldRejectCpfWithInvalidCheckDigits() {
        assertThat(CpfValidator.isValid("52998224724")).isFalse();
    }

    @Test
    void shouldRejectCpfWithRepeatedDigits() {
        assertThat(CpfValidator.isValid("11111111111")).isFalse();
    }
}
