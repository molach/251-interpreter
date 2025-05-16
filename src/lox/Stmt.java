package lox;

sealed interface Stmt permits Print, Expression, Var {
}

record Print(Expr expr) implements Stmt {}

record Expression(Expr expr) implements Stmt {}

/**
 * A statement that creates a variable, such as: var a = 1 + 2;
 * @param name the name of the variable, such as "a"
 * @param initializer provides the value, such as "1 + 2"
 */
record Var(Token name, Expr initializer) implements Stmt {}


