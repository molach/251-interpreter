package lox;

import java.util.ArrayList;
import java.util.List;

import static lox.TokenType.*;

// list of tokens --> abstract syntax tree
class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0; // index of current token

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    private Stmt statement() {
        if (match(PRINT))
            return printStatement();
        else
            return expressionStatement();
    }

    private Stmt printStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Print(expr);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Expression(expr);
    }

    Expr parseExpression() {
        try {
            return expression();
        } catch (ParseError e) {
            return null;
        }
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

    // equality    →  comparison ( ( "!=" | "==" ) comparison )*
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous(); // BANG_EQUAL or EQUAL_EQUAL
            Expr right = comparison();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    // comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )*
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    // term        →  factor ( ( "-" | "+" ) factor )*
    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    // factor → unary ( ( "/" | "*" ) unary )* ;
    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    // unary       →  ( "!" | "-" ) unary
    //               | primary
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Unary(operator, right);
        } else {
            return primary();
        }
    }

    // primary     →  "true" | "false" | "nil"
    //               | NUMBER | STRING
    //               | "(" expression ")"
    private Expr primary() {
        if (match(FALSE)) {
            return new Literal(false);
        } else if (match(TRUE)) {
            return new Literal(true);
        } else if (match(NIL)) {
            return new Literal(null);
        } else if (match(NUMBER, STRING)) {
            return new Literal(previous().literal());
        } else if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Grouping(expr);
        } else {
            throw error("Expect expression.");
        }
    }

    // helper methods

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

    /**
     * Checks to see if the current token is of the given type.
     * If so, consumes the token and returns it.
     * Otherwise, reports an error and throws a ParseError.
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw error(message);
    }

    /**
     * Reports an error and returns a ParseError.
     */
    private ParseError error(String message) {
        Lox.error(message);
        return new ParseError();
    }
}
