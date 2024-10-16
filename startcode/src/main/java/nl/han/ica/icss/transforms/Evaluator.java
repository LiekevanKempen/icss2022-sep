package nl.han.ica.icss.transforms;

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
        //variableValues = new HANLinkedList<>();

    }

    @Override
    public void apply(AST ast) {
        //variableValues = new HANLinkedList<>();
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

    private Expression evaluateExpression(Expression expression) {
        if (expression instanceof MultiplyOperation) {

            System.out.println(((MultiplyOperation) expression).lhs);
            System.out.println(((MultiplyOperation) expression).rhs);
        }
        return null;
    }


}
