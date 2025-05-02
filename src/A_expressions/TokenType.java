package A_expressions;

/**
 * All valid kinds of tokens in Lox.
 */
enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, MINUS, PLUS, SLASH, STAR,

    // Two-character tokens
    EQUAL_EQUAL,

    // One or two character tokens.
    BANG, BANG_EQUAL, // !, !=
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    STRING, NUMBER,

    // Keywords.
    AND, FALSE, NIL, OR, TRUE,

    EOF
}