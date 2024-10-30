package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet node) {
        for (int i = 0; i < node.getChildren().size(); i++) {
            if (node.getChildren().get(i) instanceof Stylerule) {
            applyStylerule( (Stylerule) node.getChildren().get(i));
            }
        }
    }

    private void applyStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
            applyDeclaration((Declaration) child);
            }
        }
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evaluateExpression(node.expression);



    }

    private Literal evaluateExpression(Expression expression) {
        if (expression instanceof AddOperation) {
            if (evaluateExpression(((AddOperation) expression).lhs) instanceof PixelLiteral) {
                PixelLiteral pixelLiteral = new PixelLiteral(((PixelLiteral) evaluateExpression(((AddOperation) expression).lhs)).value + ((PixelLiteral) evaluateExpression(((AddOperation) expression).rhs)).value);
                return pixelLiteral;
            } else if (evaluateExpression(((AddOperation) expression).lhs) instanceof PercentageLiteral) {
                PercentageLiteral percentageLiteral = new PercentageLiteral(((PercentageLiteral) evaluateExpression(((AddOperation) expression).lhs)).value + ((PercentageLiteral) evaluateExpression(((AddOperation) expression).rhs)).value);
                return percentageLiteral;
            } else if (evaluateExpression(((AddOperation) expression).lhs) instanceof ScalarLiteral) {
                ScalarLiteral scalarLiteral = new ScalarLiteral(((ScalarLiteral) evaluateExpression(((AddOperation) expression).lhs)).value + ((ScalarLiteral) evaluateExpression(((AddOperation) expression).rhs)).value);
                return scalarLiteral;
            }
        } else if (expression instanceof SubtractOperation) {
            if (evaluateExpression(((SubtractOperation) expression).lhs) instanceof PixelLiteral) {
                PixelLiteral pixelLiteral = new PixelLiteral(((PixelLiteral) evaluateExpression(((SubtractOperation) expression).lhs)).value - ((PixelLiteral) evaluateExpression(((SubtractOperation) expression).rhs)).value);
                return pixelLiteral;
            } else if (evaluateExpression(((SubtractOperation) expression).lhs) instanceof PercentageLiteral) {
                PercentageLiteral percentageLiteral = new PercentageLiteral(((PercentageLiteral) evaluateExpression(((SubtractOperation) expression).lhs)).value - ((PercentageLiteral) evaluateExpression(((SubtractOperation) expression).rhs)).value);
                return percentageLiteral;
            } else if (evaluateExpression(((SubtractOperation) expression).lhs) instanceof ScalarLiteral) {
                ScalarLiteral scalarLiteral = new ScalarLiteral(((ScalarLiteral) evaluateExpression(((SubtractOperation) expression).lhs)).value - ((ScalarLiteral) evaluateExpression(((SubtractOperation) expression).rhs)).value);
                return scalarLiteral;
            }
        } else if (expression instanceof MultiplyOperation) {
            if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof PixelLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof PixelLiteral) {
                PixelLiteral pixelLiteral = new PixelLiteral(((PixelLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((PixelLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
                return pixelLiteral;
            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof PixelLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof ScalarLiteral) {
                PixelLiteral pixelLiteral = new PixelLiteral(((PixelLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
                return pixelLiteral;
            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof ScalarLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof PixelLiteral) {
                PixelLiteral pixelLiteral = new PixelLiteral(((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((PixelLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
                return pixelLiteral;

            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof PercentageLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof PercentageLiteral) {
                PercentageLiteral percentageLiteral = new PercentageLiteral(((PercentageLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((PercentageLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
                return percentageLiteral;
            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof PercentageLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof ScalarLiteral) {
                PercentageLiteral percentageLiteral = new PercentageLiteral(((PercentageLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
                return percentageLiteral;
            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof ScalarLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof PercentageLiteral) {
                PercentageLiteral percentageLiteral = new PercentageLiteral(((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((PercentageLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
                return percentageLiteral;

            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof ScalarLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof ScalarLiteral) {
                ScalarLiteral scalarLiteral = new ScalarLiteral(((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
                return scalarLiteral;
            }
        }
        if (expression instanceof Literal) {
            return (Literal) expression;
        }



        return null;
    }


}
