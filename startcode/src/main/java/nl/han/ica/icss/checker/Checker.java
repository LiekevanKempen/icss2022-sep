package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;


    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet node) {
        HashMap<String, ExpressionType> map;
        for (int i = 0; i < node.getChildren().size(); i++) {
           if (node.getChildren().get(i) instanceof Stylerule) {
               checkStylerule((Stylerule) node.getChildren().get(i));
           } else if (node.getChildren().get(i) instanceof VariableAssignment) {
               if (variableTypes.getSize() == 0) {
                   map = new HashMap<>();
               } else {
                   map = variableTypes.getFirst();
                   variableTypes.removeFirst();
               }
               map = saveVariableAssignement((VariableAssignment) node.getChildren().get(i), map);
               variableTypes.addFirst(map);
           }
        }
    }

    private HashMap<String, ExpressionType> saveVariableAssignement(VariableAssignment variableAssignment, HashMap<String, ExpressionType> map) {
        if (variableAssignment.expression instanceof ColorLiteral ) {
            map.put(variableAssignment.name.name, ExpressionType.COLOR);
        }
        else if (variableAssignment.expression instanceof PercentageLiteral ) {
            map.put(variableAssignment.name.name, ExpressionType.PERCENTAGE);
        }
        else if (variableAssignment.expression instanceof PixelLiteral ) {
            map.put(variableAssignment.name.name, ExpressionType.PIXEL);
        } else if (variableAssignment.expression instanceof BoolLiteral) {
            map.put(variableAssignment.name.name, ExpressionType.BOOL);
        }
        return map;
    }


    private void checkStylerule(Stylerule rule) {
        for (ASTNode child: rule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration( (Declaration) child);
            }
            else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            } else if (child instanceof ElseClause) {
                checkElseClause((ElseClause) child);
            }
        }
    }

    private void checkIfClause(IfClause node) {
        if (!checkVariableExistence((VariableReference) node.conditionalExpression)) {
            node.conditionalExpression.setError("Variable does not exist");
        }

        for (int i = 0; i < node.body.size(); i++) {
            if (node.body.get(i) instanceof Declaration) {
                checkDeclaration((Declaration) node.body.get(i));
            } else if (node.body.get(i) instanceof IfClause) {
                checkIfClause((IfClause) node.body.get(i));
            } else if (node.body.get(i) instanceof ElseClause) {
                checkElseClause((ElseClause) node.body.get(i));
            }
        }


    }

    private void checkElseClause(ElseClause node) {
        for (int i = 0; i < node.body.size(); i++) {
            if (node.body.get(i) instanceof Declaration) {
                checkDeclaration((Declaration) node.body.get(i));
            } else if (node.body.get(i) instanceof IfClause) {
                checkIfClause((IfClause) node.body.get(i));
            }
        }
    }

    private void checkDeclaration(Declaration node) {
        if (node.expression instanceof VariableReference ) {
            if (!checkVariableExistence((VariableReference) node.expression)) {
                node.expression.setError("Variable does not exist");
            };
        } else if (node.expression instanceof AddOperation || node.expression instanceof SubtractOperation || node.expression instanceof MultiplyOperation) {
            for (int i = 0; i < node.expression.getChildren().size(); i++) {
                if (node.expression.getChildren().get(i) instanceof VariableReference) {
                    if (!checkVariableExistence((VariableReference) node.expression.getChildren().get(i))) {
                        node.expression.getChildren().get(i).setError("Variable does not exist");
                    }
                }
            }
            if (node.expression instanceof AddOperation || node.expression instanceof SubtractOperation || node.expression instanceof MultiplyOperation) {
                CheckOperation((Operation) node.expression);
            }
        }

        else {
            switch (node.property.name) {
                case "width":
                    if (!(node.expression instanceof PixelLiteral) && !(node.expression instanceof PercentageLiteral) && !(node.expression instanceof Operation)) {
                        node.expression.setError("Property 'width' has invalid value");
                        // TODO: USE VARIABLETYPES
                    }
                    break;
                case "height":
                    if (!(node.expression instanceof PixelLiteral) && !(node.expression instanceof PercentageLiteral) && !(node.expression instanceof Operation)) {
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

    private void CheckOperation(Operation operation) {
        if (operation.lhs instanceof VariableReference) {
            //TODO
        } else if (operation.rhs instanceof VariableReference) {
            //TODO
        }
        else if (operation instanceof MultiplyOperation) {
            if ((operation.lhs instanceof PercentageLiteral && (!(operation.rhs instanceof PercentageLiteral) && !(operation.rhs instanceof ScalarLiteral)))
                    || (operation.lhs instanceof PixelLiteral && (!(operation.rhs instanceof PixelLiteral) && !(operation.rhs instanceof ScalarLiteral)))
                    || (operation.lhs instanceof ScalarLiteral && (!(operation.rhs instanceof PixelLiteral) && !(operation.rhs instanceof PercentageLiteral)))) {

                operation.lhs.setError("Operation operants aren't compatible");
            }
        } else {
            if ((operation.lhs instanceof PercentageLiteral && !(operation.rhs instanceof PercentageLiteral)) || (operation.lhs instanceof PixelLiteral && !(operation.rhs instanceof PixelLiteral)) || operation.lhs instanceof ScalarLiteral) {
                operation.lhs.setError("Operation operants aren't compatible");
            }
        }
        System.out.println(operation.lhs + " - " + operation.rhs);
    }

    private Boolean checkVariableExistence(VariableReference expression) {
        HashMap<String, ExpressionType> map = variableTypes.getFirst();
            if (map.containsKey(expression.name)) {
                return true;
            }
            else {
                return false;
            }
    }

}
