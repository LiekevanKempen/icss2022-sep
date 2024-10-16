package nl.han.ica.icss.checker;

import com.sun.prism.paint.Color;
import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;


    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet node) {
        for (int i = 0; i < node.getChildren().size(); i++) {
           if (node.getChildren().get(i) instanceof Stylerule) {
               checkStylerule((Stylerule) node.getChildren().get(i));
           } else if (node.getChildren().get(i) instanceof VariableAssignment) {
               //checkVariableAssignement((VariableAssignment) node.getChildren().get(i));
               //saveVariableAssignement((VariableAssignment) node.getChildren().get(i));
           }



        }
    }

//    private void saveVariableAssignement(VariableAssignment variableAssignment) {
//        HashMap<String, ExpressionType> map = new HashMap<>();
//
//        if (variableAssignment.expression instanceof ColorLiteral ) {
//            map.put(variableAssignment.name.name, ExpressionType.COLOR);
//        }
//    }
//
//    private void checkVariableAssignement(VariableAssignment variableAssignment) {
//
//
//
//
//    }


    private void checkStylerule(Stylerule rule) {
        for (ASTNode child: rule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration( (Declaration) child);
            }
        }
    }

    private void checkDeclaration(Declaration node) {
        if (node.expression instanceof VariableReference ) {
            //TODO
        } else {
            switch (node.property.name) {
                case "width":
                    if (!(node.expression instanceof PixelLiteral) && !(node.expression instanceof PercentageLiteral)) {
                        node.expression.setError("Property 'width' has invalid value");
                    }
                    break;
                case "height":
                    if (!(node.expression instanceof PixelLiteral) && !(node.expression instanceof PercentageLiteral)) {
                        node.expression.setError("Property 'height' has invalid value");
                    }
                    break;
                case "color":
                    if (!(node.expression instanceof ColorLiteral)) {
                        node.expression.setError("Property 'color' has invalid value");
                    }
                    break;
                case "background-color":
                    if (!(node.expression instanceof ColorLiteral)) {
                        node.expression.setError("Property 'background-color' has invalid value");
                    }
                    break;
            }
        }
    }


}
