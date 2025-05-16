package lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*; // "static import"

// source code -> list of tokens
class Lexer {
    //           s  c
    // "10000*(3+545) <= 15"

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;   // index of first character
                             // of current token
    private int current = 0; // index of current character in source

    private static final Map<String, TokenType> keywords = Map.of(
            "and",   AND,
            "false", FALSE,
            "nil",   NIL,
            "or",    OR,
            "true",  TRUE,
            "print", PRINT,
            "var",   VAR
    );

    Lexer(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // start a new token
            start = current;
            scanToken(); // read entire token and add to the list
        }

        tokens.add(new Token(EOF, "", null));
        return tokens;
    }

    private void scanToken() {
        char ch = advance();

        switch (ch) {
            // tokens that have exactly one character
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case '*' -> addToken(STAR);
            case ';' -> addToken(SEMICOLON);

            // token that has exactly two characters: ==
            case '=' -> {
                if (match('=')) {
                    addToken(EQUAL_EQUAL);
                } else {
                    Lox.error("Unexpected character.");
                }
            }

            // tokens made of one or two characters: !, !=, <, <=, etc.
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> { // division, or start of single-line comment
                          // (Lox doesn't allow multi-line comments)
                if (match('/')) { // a comment
                    // a comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else { // division
                    addToken(SLASH);
                }
            }

            case ' ' -> {
                // ignore spaces
            }

            case '"' -> scanString();

            default -> {
                if (isDigit(ch)) { // start of a number
                    scanNumber();
                } else if (isAlpha(ch)) { // start of keyword, like nil
                    scanKeywordOrIdentifier();
                } else { // for example, @ ~ ^
                    Lox.error("Unexpected character.");
                }
            }
        }
    }

    /**
     * Scans a string token from the source code and
     * adds a corresponding Token to the list.
     * Assumes that we just read the opening " of a
     * string.
     */
    private void scanString() {
        // read until closing ".
        while (peek() != '"' && !isAtEnd()) {
            advance();
        }

        if (isAtEnd()) {
            Lox.error("Unterminated string.");
            return;
        }

        // Consume the closing "
        advance();

        // user types: "abc"
        // we store: abc inside a Java String

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    /**
     * Scans a number token from the source code and
     * adds a corresponding Token to the list.
     * Assume that we just read the first digit of a number.
     */
    private void scanNumber() {
        // Consume digits until reaching
        // the end of the number or a decimal point.
        while (isDigit(peek())) {
            advance();
        }

        // Look for a fractional part (one or more digits following the decimal point)
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            // Consume digits after decimal point.
            while (isDigit(peek())) {
                advance();
            }
        }

        double num = Double.parseDouble(source.substring(start, current));
        addToken(NUMBER, num);
    }

    private void scanKeywordOrIdentifier() {
        // read until the end of the keyword or identifier
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, IDENTIFIER);
        // text: "and"
        // type: AND
        addToken(type);
    }

    /**
     * Determines whether the current character is
     * equal to the provided expected character.
     * If it is, advances to the next character.
     * If at end of source, returns false.
     */
    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(current) != expected) {
            return false;
        } else {
            current++;
            return true;
        }
    }

    /**
     * Returns the current character without advancing
     * to the next character. If at end, returns '\0'.
     */
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    /**
     * Returns the next character without advancing
     * to the next character. If there is no next
     * character, returns '\0'.
     */
    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }

        return source.charAt(current + 1);
    }

    /**
     * Determines whether the provided character is
     * in a-z or in A-Z or is _; that is, in [a-zA-Z_].
     */
    private boolean isAlpha(char ch) {
        return (ch >= 'a' && ch <= 'z')
               || (ch >= 'A' && ch <= 'Z')
               || ch == '_';
    }

    /**
     * Determines whether the provided character is
     * in [0-9].
     */
    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Determines whether the provided character is
     * in [a-zA-Z_0-9].
     */
    private boolean isAlphaNumeric(char ch) {
        return isAlpha(ch) || isDigit(ch);
    }

    /**
     * Determines whether we have reached the end of
     * the source code.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Returns the current character and advances to
     * the next character.
     */
    private char advance() { // first, current: 2
                             // indexes: 012345678
                             // source: "987+789+1"
                             // returns: '7'
                             // now, current: 3
        return source.charAt(current++);
    }

    /**
     * Adds to the list of tokens a token containing
     * the provided token type and current lexeme.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds to the list of tokens a token containing
     * the provided token type, current lexeme, and
     * provided literal.
     */
    private void addToken(TokenType type, Object literal) {
        // current: 8
        // start:   5
        // indexes: 01234567890123
        // source: "true and false"
        String text = source.substring(start, current); // lexeme
        tokens.add(new Token(type, text, literal));
    }
}
