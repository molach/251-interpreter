package lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static lox.TokenType.*;

// abstract syntax tree --> result
class Interpreter {
//    void interpret(Expr expr) { // expr is the root of the AST
//        try {
//            Object value = evaluate(expr);
//            System.out.println(stringify(value));
//        } catch (RuntimeError error) {
//            Lox.runtimeError(error);
//        }
//    }

    Map<String, Object> varsMap = new HashMap<>();

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private void execute(Stmt stmt) {
        switch (stmt) {
            case Expression(Expr expr) -> {
                evaluate(expr);
            }
            case Print(Expr expr) -> {
                Object result = evaluate(expr);
                System.out.println(stringify(result));
            }
            case Var(Token name, Expr initializer) -> {
                // store the variable name along with its value in a map
                varsMap.put(name.lexeme(), evaluate(initializer));
            }
        }
    }

    private Object evaluate(Expr expr) {
        return switch (expr) {
            case Binary(Expr left, Token operator, Expr right)
                    -> evaluateBinaryExpr(left, operator, right);
            case Grouping(Expr expression) -> evaluate(expression);
            case Literal(Object value) -> value;
            case Logical(Expr left, Token operator, Expr right)
                    -> evaluateLogicalExpr(left, operator, right);
            case Unary(Token operator, Expr right)
                    -> evaluateUnaryExpr(operator, right);
            case Variable(Token name) -> varsMap.get(name.lexeme());
        };
    }

    private Object evaluateBinaryExpr(Expr left, Token operator, Expr right) {
        Object evaluatedLeft = evaluate(left);
        Object evaluatedRight = evaluate(right);

        return switch (operator.type()) {
            case GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, MINUS, SLASH, STAR -> {
                // only numbers in this case
                double leftNum = requireNumberOperand(operator, evaluatedLeft);
                double rightNum = requireNumberOperand(operator, evaluatedRight);

                yield switch (operator.type()) {
                    case GREATER -> leftNum > rightNum;
                    case GREATER_EQUAL -> leftNum >= rightNum;
                    case LESS -> leftNum < rightNum;
                    case LESS_EQUAL -> leftNum <= rightNum;
                    case MINUS -> leftNum - rightNum;
                    case SLASH -> leftNum / rightNum;
                    case STAR -> leftNum * rightNum;
                    default -> throw new AssertionError("should be unreachable");
                };
            }
            case PLUS -> {
                // both numbers, or both strings
                if (evaluatedLeft instanceof Double d1 && evaluatedRight instanceof Double d2) {
                    yield d1 + d2; // numeric addition
                } else if (evaluatedLeft instanceof String s1 && evaluatedRight instanceof String s2) {
                    yield s1 + s2; // String concatenation
                } else {
                    throw new RuntimeError(operator, "Operands must be two numbers or two strings.");
                }
            }
            case EQUAL_EQUAL -> Objects.equals(evaluatedLeft, evaluatedRight);
            case BANG_EQUAL -> !Objects.equals(evaluatedLeft, evaluatedRight);
            default -> throw new AssertionError("should be unreachable");
        };
    }

    private Object evaluateLogicalExpr(Expr left, Token operator, Expr right) {
        Object evaluatedLeft = evaluate(left);

        if (operator.type() == OR && isTruthy(evaluatedLeft)) {
            return evaluatedLeft;
        } else if (operator.type() == AND && !isTruthy(evaluatedLeft)) {
            return evaluatedLeft;
        } else {
            return evaluate(right);
        }
    }

    private Object evaluateUnaryExpr(Token operator, Expr right) {
        Object evaluatedRight = evaluate(right);

        return switch (operator.type()) {
            case BANG -> !isTruthy(evaluatedRight);
            case MINUS -> -requireNumberOperand(operator, evaluatedRight);
            default -> throw new AssertionError("should be unreachable");
        };
    }

    private static boolean isTruthy(Object o) {
        return switch (o) {
            case null -> false;
            case Boolean b -> b;
            default -> true;
        };
    }

    private static double requireNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double d) {
            return d;
        } else {
            throw new RuntimeError(operator, "Operands must be numbers.");
        }
    }

    private String stringify(Object object) {
        return switch (object) {
            case null -> "nil";
            case Double d -> {
                String str = d.toString();
                yield str.endsWith(".0") ? str.substring(0, str.length() - 2) : str;
            }
            default -> object.toString();
        };
    }
}
