package A_expressions;

import java.util.List;
import java.util.Scanner;

public class Lox {
    public static void main(String[] args) {
        Scanner keyboardScanner = new Scanner(System.in);
        System.out.print("> "); // prompt

        // read expressions from the user until no more input
        while (keyboardScanner.hasNextLine()) {
            String sourceCodeLine = keyboardScanner.nextLine();
            run(sourceCodeLine);
            System.out.print("> "); // prompt again
        }
    }

    private static final Interpreter interpreter = new Interpreter();

    static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }

//        Parser parser = new Parser(tokens);
//        Expr expr = parser.parseExpression();
//        // expr is the root of the AST
//
//        interpreter.interpret(expr); // prints the result
    }

    static void error(String message) {
        System.out.println(message);
    }
}
