package A_expressions;

import java.util.List;

import static A_expressions.TokenType.*;

// list of tokens --> abstract syntax tree
class Parser {
    private final List<Token> tokens;
    private int current = 0; // index of current token

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parseExpression() {
        return expression();
    }

    // expression  →  logic_or
    private Expr expression() {
        return or();
    }

    // logic_or    →  logic_and ( "or" logic_and )*
    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous(); // OR
            Expr right = and();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    // logic_and   →  equality ( "and" equality )*
    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous(); // AND
            Expr right = equality();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        return null;
    }

    /**
     * Checks if the current token has any of the given types.
     * If so, consumes the token and returns true.
     * Otherwise, returns false and does not consume the token.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if there are more tokens to process
     * and the current token is of the provided type.
     */
    private boolean check(TokenType type) {
        return notAtEnd() && peek().type() == type;
    }

    /**
     * Returns true if there is at least one more token
     * left to process.
     */
    private boolean notAtEnd() {
        return peek().type() != EOF;
    }

    /**
     * Returns the current token.
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Consumes the current token and returns it.
     */
    private Token advance() {
        if (notAtEnd()) {
            current++;
        }

        return previous();
    }

    /**
     * Returns the most recently consumed token.
     */
    private Token previous() {
        return tokens.get(current - 1);
    }
}
