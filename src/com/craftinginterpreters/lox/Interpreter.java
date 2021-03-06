package com.craftinginterpreters.lox;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    void interpret(List<Stmt> statements) {
        try {
            for(Stmt stmt: statements) {
                execute(stmt);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    Object execute(Stmt stmt) {
        return stmt.accept(this);
    }

    Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expr);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        return switch (expr.operator.type) {
            case MINUS -> { checkNumberOperand(expr.operator, right); yield -(double) right; }
            case BANG -> !isTruthy(right);
            // should never reach to it.
            default -> null;
        };
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        return switch (expr.operator.type) {
            case EQUAL_EQUAL -> isEqual(left, right);
            case BANG_EQUAL -> !isEqual(left, right);
            case LESS -> { checkNumberOperands(expr.operator, left, right); yield (double) left < (double) right; }
            case LESS_EQUAL -> { checkNumberOperands(expr.operator, left, right); yield (double) left <= (double) right; }
            case GREATER -> { checkNumberOperands(expr.operator, left, right); yield (double) left > (double) right; }
            case GREATER_EQUAL -> { checkNumberOperands(expr.operator, left, right); yield (double) left >= (double) right; }
            case MINUS -> { checkNumberOperands(expr.operator, left, right); yield (double) left - (double) right; }
            case STAR -> { checkNumberOperands(expr.operator, left, right); yield (double) left * (double) right; }
            case SLASH -> { checkNumberOperands(expr.operator, left, right); yield (double) left / (double) right; }
            case PLUS -> {
                if ((left instanceof Double) && (right instanceof Double))  yield (double) left + (double) right;
                if ((left instanceof String) && (right instanceof String))  yield (String) left + (String) right;

                throw new RuntimeError(expr.operator, "Operands must be two numbers or to strings.");
            }
            default -> null;
        };
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }
    private boolean isTruthy(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (boolean) val;
        return true;
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
}
