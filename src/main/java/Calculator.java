import exception.InvalidExpressionException;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    int add(int i, int j) {
        return i + j;
    }

    int subtract(int i, int j) {
        return i - j;
    }

    int multiply(int i, int j) {
        return i * j;
    }

    int divide(int i, int j) {
        if (j == 0) {
            throw new ArithmeticException("0으로 나눌 수 없습니다.");
        }
        return i / j;
    }

    public int calculate(String expression) {
        if (isNullOrEmpty(expression)) {
            return 0;
        }

        validateExpression(expression.trim());

        expression = preprocessExpression(expression);

        List<Integer> numbers = extractNumbers(expression);
        List<String> operators = extractOperators(expression);

        if (numbers.size() == 1) {
            return numbers.get(0);
        }

        return computeResult(numbers, operators);
    }

    private void validateExpression(String expression) {
        if (expression.matches(".*[+\\-*/]{2,}.*")) {
            throw new InvalidExpressionException("잘못된 수식입니다: 연산자가 연속으로 두 번 이상 사용되었습니다.");
        }
        if (expression.matches("^[+\\-*/].*") || expression.matches(".*[+\\-*/]$")) {
            throw new InvalidExpressionException("잘못된 수식입니다: 연산자가 잘못된 위치에 있습니다.");
        }
    }

    private boolean isNullOrEmpty(String expression) {
        return expression == null || expression.trim().isEmpty();
    }

    private String preprocessExpression(String expression) {
        return expression.trim()
                .replace(",", "+")
                .replace(":", "+");
    }

    private List<Integer> extractNumbers(String expression) {
        String[] tokens = splitExpression(expression);
        List<Integer> numbers = new ArrayList<>();

        for (String token : tokens) {
            if (!isOperator(token)) {
                if (isNumeric(token.trim())) {
                    numbers.add(Integer.parseInt(token.trim()));
                } else {
                    throw new InvalidExpressionException("잘못된 기호입니다: '" + token.trim() + "'");
                }
            }
        }

        if (numbers.isEmpty()) {
            throw new InvalidExpressionException("잘못된 입력입니다: 수식에서 유효한 숫자를 찾을 수 없습니다.");
        }

        return numbers;
    }

    private List<String> extractOperators(String expression) {
        String[] tokens = splitExpression(expression);
        List<String> operators = new ArrayList<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                operators.add(token.trim());
            } else if (!isNumeric(token.trim())) {
                throw new InvalidExpressionException("잘못된 기호입니다: '" + token.trim() + "'");
            }
        }
        return operators;
    }

    private boolean isNumeric(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String[] splitExpression(String expression) {
        return expression.split("(?=[+\\-*/])|(?<=[+\\-*/])");
    }

    private boolean isOperator(String token) {
        return token.matches("[+\\-*/]");
    }

    private int computeResult(List<Integer> numbers, List<String> operators) {
        int result = numbers.get(0);

        for (int i = 0; i < operators.size(); i++) {
            String operator = operators.get(i);
            int operand = numbers.get(i + 1);

            result = applyOperator(result, operand, operator);
        }

        return result;
    }

    private int applyOperator(int left, int right, String operator) {
        return switch (operator) {
            case "+" -> add(left, right);
            case "-" -> subtract(left, right);
            case "*" -> multiply(left, right);
            case "/" -> divide(left, right);
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    public static void main(String[] args) {
        Calculator cal = new Calculator();
        System.out.println(cal.add(3, 4));
        System.out.println(cal.subtract(5, 4));
        System.out.println(cal.multiply(2, 6));
        System.out.println(cal.divide(8, 4));
    }

}