package lox;

public sealed interface Expr
        permits Binary, Grouping, Literal, Logical, Unary, Variable {}

/*
-2 + 3

         Binary
        /  |  \
   Unary   +  Literal:3
 /    \
-    Literal:2
 */

record Binary(Expr left, Token operator, Expr right) implements Expr {}

record Grouping(Expr expression) implements Expr {}

record Literal(Object value) implements Expr {}
// e.g., the value can be "abc", 12, 12.3, true, false, etc.

record Logical(Expr left, Token operator, Expr right) implements Expr {}

record Unary(Token operator, Expr expr) implements Expr {}

record Variable(Token name) implements Expr {}