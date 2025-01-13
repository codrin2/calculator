import exception.InvalidExpressionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    @DisplayName("더하기 연산 테스트")
    void add() {
        // given
        int num1 = 3;
        int num2 = 4;

        // when
        int result = calculator.add(num1, num2);

        // then
        assertThat(result).isEqualTo(7);
    }

    @Test
    @DisplayName("빼기 연산 테스트")
    void subtract() {
        // given
        int num1 = 5;
        int num2 = 4;

        // when
        int result = calculator.subtract(num1, num2);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("곱하기 연산 테스트")
    void multiply() {
        // given
        int num1 = 2;
        int num2 = 6;

        // when
        int result = calculator.multiply(num1, num2);

        // then
        assertThat(result).isEqualTo(12);
    }

    @Test
    @DisplayName("나누기 연산 테스트")
    void divide() {
        // given
        int num1 = 8;
        int num2 = 4;

        // when
        int result = calculator.divide(num1, num2);

        // then
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("0으로 나눌 때 예외 발생 테스트")
    void divideByZero() {
        // given
        int num1 = 8;
        int num2 = 0;

        // when & then
        assertThatThrownBy(() -> calculator.divide(num1, num2))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("0으로 나눌 수 없습니다.");
    }

    @ParameterizedTest(name = "Expression: \"{0}\", Result: {1}")
    @MethodSource("validExpressionsAndResults")
    @DisplayName("정상적인 사칙연산 테스트")
    void calculate_validExpressions(String expression, int expectedResult) {
        // given
        // validExpressionsAndResults

        // when
        int result = calculator.calculate(expression);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest(name = "Expression: \"{0}\" throws {1}")
    @MethodSource("invalidExpressionsAndExceptions")
    @DisplayName("잘못된 표현식 테스트")
    void calculate_invalidExpressions(String expression, Class<? extends Throwable> expectedException) {
        // given
        // invalidExpressionsAndExceptions

        // when & then
        assertThatThrownBy(() -> calculator.calculate(expression))
                .isInstanceOf(expectedException);
    }

    @Test
    @DisplayName("빈 문자열 및 null 처리 테스트")
    void calculate_emptyOrNull() {
        // given & when & then
        assertThat(calculator.calculate("")).isEqualTo(0); // 빈 문자열
        assertThat(calculator.calculate(null)).isEqualTo(0); // null 입력
        assertThat(calculator.calculate("   ")).isEqualTo(0); // 공백 문자열
    }

    @Test
    @DisplayName("하나의 숫자만 있는 경우")
    void calculate_singleNumber() {
        // given & when & then
        assertThat(calculator.calculate("42")).isEqualTo(42);
        assertThat(calculator.calculate("  7  ")).isEqualTo(7); // 앞뒤 공백 포함
    }

    @Test
    @DisplayName("연산자가 없는 경우")
    void calculate_noOperators() {
        // given & when & then
        assertThat(calculator.calculate("42")).isEqualTo(42); // 단일 숫자 처리
        assertThat(calculator.calculate("100")).isEqualTo(100);
    }

    @Test
    @DisplayName("숫자 사이에 공백이 있는 경우")
    void calculate_withSpaces() {
        // given & when & then
        assertThat(calculator.calculate("2 + 3 *    4 /  2")).isEqualTo(10); // 공백 무시
        assertThat(calculator.calculate("   2+3*4/2   ")).isEqualTo(10); // 앞뒤 공백 처리
    }

    @Test
    @DisplayName("잘못된 기호가 포함된 경우")
    void calculate_withInvalidCharacters() {
        // given & when & then
        assertThatThrownBy(() -> calculator.calculate("2 + 3 @ 4"))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("잘못된 기호입니다");

        assertThatThrownBy(() -> calculator.calculate("2 + 3 & 4"))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("잘못된 기호입니다");

        assertThatThrownBy(() -> calculator.calculate("2 + 3 $ 4"))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("잘못된 기호입니다");
    }

    @Test
    @DisplayName("다양한 표현식 조합 테스트")
    void calculate_mixedExpressions() {
        // given & when & then
        assertThat(calculator.calculate("1+2,3:4/2")).isEqualTo(5); // 모든 구분자를 +로 처리
        assertThat(calculator.calculate("5+4/2")).isEqualTo(4); // 정수 나눗셈
    }

    static Stream<Object[]> validExpressionsAndResults() {
        return Stream.of(
                new Object[]{"2 + 3 * 4 / 2", 10},
                new Object[]{"2 + 3 * 4 , 2", 22}, // ,를 +로 처리
                new Object[]{"2 + 3 * 4,2", 22}, // ,를 +로 처리
                new Object[]{"42", 42}, // 단일 숫자
                new Object[]{"   2+3*4/2   ", 10}, // 공백 처리
                new Object[]{"2 + 3 *    4 /  2", 10} // 숫자 사이 공백
        );
    }

    static Stream<Object[]> invalidExpressionsAndExceptions() {
        return Stream.of(
                new Object[]{"2 ++ 3 * 4 / 2", InvalidExpressionException.class},
                new Object[]{"2 + + 3 * 4 / 2", InvalidExpressionException.class},
                new Object[]{"2 + 3 ** 4 / 2", InvalidExpressionException.class},
                new Object[]{"2 + 3 $ 4", InvalidExpressionException.class},
                new Object[]{"++++", InvalidExpressionException.class},
                new Object[]{"2 + 3 / ", InvalidExpressionException.class},
                new Object[]{"/ 2 + 3", InvalidExpressionException.class}
        );
    }
}