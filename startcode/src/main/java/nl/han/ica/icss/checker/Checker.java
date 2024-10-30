package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.sql.SQLOutput;
import java.util.DoubleSummaryStatistics;
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
        }
        else if (variableAssignment.expression instanceof BoolLiteral) {
            map.put(variableAssignment.name.name, ExpressionType.BOOL);
        }
        else if ( variableAssignment.expression instanceof VariableReference){
            if (variableTypes.getSize() == 0) {
                if (map.containsKey(((VariableReference) variableAssignment.expression).name)) {
                    map.put(variableAssignment.name.name, (map.get(((VariableReference) variableAssignment.expression).name)) );
                }
            } else {
            for (int i = 0; i < variableTypes.getSize(); i++) {
                if (variableTypes.get(i).containsKey(((VariableReference) variableAssignment.expression).name)) {
                    map.put(variableAssignment.name.name, (map.get(((VariableReference) variableAssignment.expression).name)) );
                }

            }
            }
        }
        else if (variableAssignment.expression instanceof Operation) {
            map.put(variableAssignment.name.name, findOperationValue((Operation) variableAssignment.expression));
        }
        return map;
    }

    private ExpressionType findOperationValue(Operation operation) {
        if (operation.lhs instanceof Operation) {
            findOperationValue((Operation) operation.lhs);
        } else if (operation.rhs instanceof Operation) {
            findOperationValue((Operation) operation.rhs);
        } else {
            if (operation.lhs instanceof PixelLiteral || operation.rhs instanceof PixelLiteral) {
                return ExpressionType.PIXEL;
            } else if (operation.lhs instanceof PercentageLiteral || operation.rhs instanceof PercentageLiteral) {
                return ExpressionType.PERCENTAGE;
            } else if (operation.lhs instanceof ScalarLiteral || operation.rhs instanceof ScalarLiteral) {
                return ExpressionType.SCALAR;
            }


        }
        return ExpressionType.UNDEFINED;
    }


    private void checkStylerule(Stylerule rule) {
        HashMap<String, ExpressionType> map = new HashMap<>();
        boolean variableCheck = false;
        for (ASTNode child: rule.getChildren()) {
            if (child instanceof VariableAssignment) {
                if (variableTypes.getFirst() == map) {
                    variableTypes.removeFirst();
                }
                map = saveVariableAssignement((VariableAssignment) child, map);
                variableTypes.addFirst(map);
                variableCheck = true;
            }
            else if (child instanceof Declaration) {
                checkDeclaration( (Declaration) child);
            }
            else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
            else if (child instanceof ElseClause) {
                checkElseClause((ElseClause) child);
            }

        }
        if (variableCheck) {
            variableTypes.removeFirst();
            variableCheck = false;
        }
    }

    private void checkIfClause(IfClause node) {
        if (!checkVariableExistence((VariableReference) node.conditionalExpression)) {
            node.conditionalExpression.setError("Variable does not exist");
        }

        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> map = variableTypes.get(i);
            if (map.containsKey(((VariableReference) node.conditionalExpression).name)) {
                if (map.get(((VariableReference) node.conditionalExpression).name) != ExpressionType.BOOL) {
                    node.conditionalExpression.setError("Variable needs to be boolean");
                }
            }
        }

        HashMap<String, ExpressionType> map = new HashMap<>();
        boolean variableCheck = false;
        for (int i = 0; i < node.body.size(); i++) {
            if (node.body.get(i) instanceof VariableAssignment) {
                if (variableTypes.getFirst() == map) {
                    variableTypes.removeFirst();
                }
                map = saveVariableAssignement((VariableAssignment) node.body.get(i), map);
                variableTypes.addFirst(map);
                variableCheck = true;
            } else if (node.body.get(i) instanceof Declaration) {
                checkDeclaration((Declaration) node.body.get(i));
            } else if (node.body.get(i) instanceof IfClause) {
                checkIfClause((IfClause) node.body.get(i));
            } else if (node.body.get(i) instanceof ElseClause) {
                checkElseClause((ElseClause) node.body.get(i));
            }
        }
        if (variableCheck) {
        variableTypes.removeFirst();
        }
    }

    private void checkElseClause(ElseClause node) {
        HashMap<String, ExpressionType> map = new HashMap<>();
        boolean variableCheck = false;
        for (int i = 0; i < node.body.size(); i++) {
            if (node.body.get(i) instanceof VariableAssignment) {
                if (variableTypes.getFirst() == map) {
                    variableTypes.removeFirst();
                }
                map = saveVariableAssignement((VariableAssignment) node.body.get(i), map);
                variableTypes.addFirst(map);
                variableCheck = true;
            } else if (node.body.get(i) instanceof Declaration) {
                checkDeclaration((Declaration) node.body.get(i));
            } else if (node.body.get(i) instanceof IfClause) {
                checkIfClause((IfClause) node.body.get(i));
            }
        }
        if (variableCheck) {
        variableTypes.removeFirst();
        }
    }

    private void checkDeclaration(Declaration node) {
        if (node.expression instanceof VariableReference ) {
            if (!checkVariableExistence((VariableReference) node.expression)) {
                node.expression.setError("Variable does not exist");
            };
            for (int i = 0; i < variableTypes.getSize(); i++) {

            HashMap<String, ExpressionType> map = variableTypes.get(i);
            switch (node.property.name) {
                case "width":
                case "height":
                    if (map.containsKey(((VariableReference) node.expression).name)) {
                        if ((map.get(((VariableReference) node.expression).name) != ExpressionType.PIXEL) && (map.get(((VariableReference) node.expression).name) != ExpressionType.PERCENTAGE)) {
                            node.expression.setError("Variable has invalid value");
                        }
                    }
                    break;
                case "color":
                case "background-color":
                    if (map.containsKey(((VariableReference) node.expression).name)) {
                        if ((map.get(((VariableReference) node.expression).name) != ExpressionType.COLOR)) {
                            node.expression.setError("Variable has invalid value");
                        }
                    }
                    break;
            }

            }
        } else if (node.expression instanceof AddOperation || node.expression instanceof SubtractOperation || node.expression instanceof MultiplyOperation) {
            for (int i = 0; i < node.expression.getChildren().size(); i++) {
                if (node.expression.getChildren().get(i) instanceof VariableReference) {
                    if (!checkVariableExistence((VariableReference) node.expression.getChildren().get(i))) {
                        node.expression.getChildren().get(i).setError("Variable does not exist 1");
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
            checkhs(operation.lhs, operation.rhs, operation);
        } else if (operation.rhs instanceof VariableReference) {
            checkhs(operation.rhs, operation.lhs, operation);
        } else if (operation instanceof MultiplyOperation) {
            if ((operation.lhs instanceof PercentageLiteral && (!(operation.rhs instanceof PercentageLiteral) && !(operation.rhs instanceof ScalarLiteral))  && (!(operation.rhs instanceof Operation)))
                    || (operation.lhs instanceof PixelLiteral && (!(operation.rhs instanceof PixelLiteral) && !(operation.rhs instanceof ScalarLiteral))) && (!(operation.rhs instanceof Operation))) {

                operation.lhs.setError("Operation operants aren't compatible");
            }
        } else {
            if ((operation.lhs instanceof PercentageLiteral && !(operation.rhs instanceof PercentageLiteral)) || (operation.lhs instanceof PixelLiteral && !(operation.rhs instanceof PixelLiteral)) || (operation.lhs instanceof ScalarLiteral && !(operation.rhs instanceof ScalarLiteral)) ) {
                operation.lhs.setError("Operation operants aren't compatible");
            }
        }

    }

    private void checkhs(Expression node, Expression otherNode, Operation operation) {
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> map = variableTypes.get(i);
            if (map.containsKey(((VariableReference) node).name)) {

                if (operation instanceof MultiplyOperation) {
                    if ((map.get(((VariableReference) node).name) == ExpressionType.PIXEL) && ((!(otherNode instanceof PixelLiteral)) && (!(otherNode instanceof ScalarLiteral)) && (!(otherNode instanceof Operation)))) {
                        node.setError("Operation operants aren't compatible");
                    } else if ((map.get(((VariableReference) node).name) == ExpressionType.PERCENTAGE) && (!(otherNode instanceof PercentageLiteral)) && ((!(otherNode instanceof ScalarLiteral))) && (!(otherNode instanceof Operation))) {
                        node.setError("Operation operants aren't compatible");
                    }
                } else {
                    if ((map.get(((VariableReference) node).name) == ExpressionType.PIXEL) && ((!(otherNode instanceof PixelLiteral)) && (!(otherNode instanceof Operation)) && (!(otherNode instanceof VariableReference)))) {
                        node.setError("Operation operants aren't compatible");
                    } else if ((map.get(((VariableReference) node).name) == ExpressionType.PERCENTAGE) && ((!(otherNode instanceof PercentageLiteral)) && (!(otherNode instanceof Operation))  && (!(otherNode instanceof VariableReference)))) {
                        node.setError("Operation operants aren't compatible");
                    }
                }
            }
        }
    }

    private Boolean checkVariableExistence(VariableReference expression) {
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> map = variableTypes.get(i);
            if (map.containsKey(expression.name)) {
                return true;
            }
        }
        return false;
    }

}
