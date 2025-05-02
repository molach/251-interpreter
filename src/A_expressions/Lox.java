package A_expressions;

import java.util.List;

public class Lox {
    public static void main(String[] args) {
        // read the source code (using a Scanner)
        // and pass it to run method
    }

    static Interpreter interpreter = new Interpreter();

    public static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expr = parser.parseExpression();
        // expr is the root of the AST

        interpreter.interpret(expr); // prints the result
    }
}
