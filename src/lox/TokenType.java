package lox;

/**
 * All valid kinds of tokens in Lox.
 */
enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, MINUS, PLUS, SLASH, STAR,
    SEMICOLON,

    // Two-character tokens
    EQUAL_EQUAL,

    // One or two character tokens.
    BANG, BANG_EQUAL, // !, !=
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    STRING, NUMBER,

    IDENTIFIER,

    // Keywords.
    AND, FALSE, NIL, OR, TRUE,
    PRINT, VAR,

    EOF
}