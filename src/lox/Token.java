package lox;

/**
 * @param type    The token's type, for example PLUS or NUMBER.
 * @param lexeme  The text in the source code, for example "+" or "12"
 * @param literal The value of the token, if applicable.
 *                For example, PLUS doesn't have a value,
 *                but a NUMBER does.
 */
record Token(TokenType type, String lexeme, Object literal) {}