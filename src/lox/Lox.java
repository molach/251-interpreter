package lox;

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

//        System.out.println("Here are the tokens: ");
//        for (Token token : tokens) {
//            System.out.println(token);
//        }

         Parser parser = new Parser(tokens);
        // Expr expr = parser.parseExpression();
        // expr is the root of the AST

//        System.out.println("\nHere is the AST: ");
//        System.out.println(expr);

        // interpreter.interpret(expr); // prints the result

        List<Stmt> stmts = parser.parse();

    }

    static void error(String message) {
        System.out.println(message);
    }

    static void runtimeError(RuntimeError error) {
        System.out.println(error.getMessage());
    }
}