package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;

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
        HashMap<String, Literal> map;
        for (int i = 0; i < node.getChildren().size(); i++) {
            if (node.getChildren().get(i) instanceof Stylerule) {
                applyStylerule( (Stylerule) node.getChildren().get(i));
            } if (node.getChildren().get(i) instanceof VariableAssignment) {
                if (variableValues.getSize() == 0) {
                    map = new HashMap<>();
                } else {
                    map = variableValues.getFirst();
                    variableValues.removeFirst();
                }
                map = saveVariableAssignement((VariableAssignment) node.getChildren().get(i), map);
                variableValues.addFirst(map);
                applyVariableAssigment((VariableAssignment) node.getChildren().get(i));
            }
        }
    }

    private VariableAssignment applyVariableAssigment(VariableAssignment variableAssignment) {
        if (variableAssignment.expression instanceof VariableReference) {
            for (int i = 0; i < variableValues.getSize(); i++) {
                HashMap<String, Literal> map = variableValues.get(i);
                if(map.containsKey(((VariableReference) variableAssignment.expression).name)) {
                    variableAssignment.expression = map.get(((VariableReference) variableAssignment.expression).name);
                }
            }
        } else if (variableAssignment.expression instanceof Operation) {
            variableAssignment.expression = evaluateExpression((Operation) variableAssignment.expression);
        } else {
            return variableAssignment;
        }
        return null;
    }

    private HashMap<String, Literal> saveVariableAssignement(VariableAssignment variableAssignment, HashMap<String, Literal> map) {
        if (variableAssignment.expression instanceof ColorLiteral) {
            map.put(variableAssignment.name.name, (ColorLiteral) variableAssignment.expression);
        }
        else if (variableAssignment.expression instanceof PercentageLiteral ) {
            map.put(variableAssignment.name.name, (PercentageLiteral) variableAssignment.expression);
        }
        else if (variableAssignment.expression instanceof PixelLiteral ) {
            map.put(variableAssignment.name.name, (PixelLiteral) variableAssignment.expression);
        }
        else if (variableAssignment.expression instanceof BoolLiteral) {
            map.put(variableAssignment.name.name, (BoolLiteral) variableAssignment.expression);
        }
        else if ( variableAssignment.expression instanceof VariableReference){
            if (variableValues.getSize() == 0) {
                if (map.containsKey(((VariableReference) variableAssignment.expression).name)) {
                    map.put(variableAssignment.name.name, (map.get(((VariableReference) variableAssignment.expression).name)) );
                }
            } else {
                for (int i = 0; i < variableValues.getSize(); i++) {
                    if (variableValues.get(i).containsKey(((VariableReference) variableAssignment.expression).name)) {
                        map.put(variableAssignment.name.name, (map.get(((VariableReference) variableAssignment.expression).name)) );
                    }

                }
            }
        }
        else if (variableAssignment.expression instanceof Operation) {
            map.put(variableAssignment.name.name, evaluateExpression(variableAssignment.expression));
        }
        return map;
    }


    private void applyStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                if (((IfClause) child).conditionalExpression instanceof VariableReference) {
                    ((IfClause) child).conditionalExpression = evaluateExpression((VariableReference) ((IfClause) child).conditionalExpression);
                }
                boolean conditionalExpression = ((BoolLiteral) ((IfClause) child).conditionalExpression).value ;

                if (conditionalExpression) {
                    applyIfClause((IfClause) child);
                    node.body = ((IfClause) child).body;
                } else {
                    if( ((IfClause) child).elseClause != null ) {
                        ((IfClause) child).body = ((IfClause) child).elseClause.body;
                        node.body = ((IfClause) child).body;
                    } else {
                        node.body.remove(child);
                    }
                }


            } else if (child instanceof ElseClause) {
                applyElseClause((ElseClause) child);
            }
        }
    }

    private void applyIfClause(IfClause node) {
        if (node.conditionalExpression instanceof VariableReference) {
            node.conditionalExpression = evaluateExpression(node.conditionalExpression);
        }

        if (((BoolLiteral) node.conditionalExpression).value){
            for (int i = 0; i < node.body.size(); i++) {
                if (node.body.get(i) instanceof VariableAssignment) {
                    ((VariableAssignment) node.body.get(i)).expression = evaluateExpression(((VariableAssignment) node.body.get(i)).expression);
                } else if (node.body.get(i) instanceof Declaration) {
                    applyDeclaration((Declaration) node.body.get(i));
                } else if (node.body.get(i) instanceof IfClause) {
                    applyIfClause((IfClause) node.body.get(i));
                } else if (node.body.get(i) instanceof ElseClause) {
                    applyElseClause((ElseClause) node.body.get(i));
                }
                applyElseClause(node.elseClause);
            }
        }

    }


    private void applyElseClause(ElseClause node) {
        for (int i = 0; i < node.body.size(); i++) {
            if (node.body.get(i) instanceof VariableAssignment) {
                ((VariableAssignment) node.body.get(i)).expression = evaluateExpression(((VariableAssignment) node.body.get(i)).expression);
            } else if (node.body.get(i) instanceof Declaration) {
                applyDeclaration((Declaration) node.body.get(i));
            } else if (node.body.get(i) instanceof IfClause) {
                applyIfClause((IfClause) node.body.get(i));
            }
        }
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evaluateExpression(node.expression);

    }


    private Literal evaluateExpression(Expression expression) {
        if (expression instanceof AddOperation) {
            if (evaluateExpression(((AddOperation) expression).lhs) instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) evaluateExpression(((AddOperation) expression).lhs)).value + ((PixelLiteral) evaluateExpression(((AddOperation) expression).rhs)).value);
            } else if (evaluateExpression(((AddOperation) expression).lhs) instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) evaluateExpression(((AddOperation) expression).lhs)).value + ((PercentageLiteral) evaluateExpression(((AddOperation) expression).rhs)).value);
            } else if (evaluateExpression(((AddOperation) expression).lhs) instanceof ScalarLiteral) {
                return new ScalarLiteral(((ScalarLiteral) evaluateExpression(((AddOperation) expression).lhs)).value + ((ScalarLiteral) evaluateExpression(((AddOperation) expression).rhs)).value);
            }
        } else if (expression instanceof SubtractOperation) {
            if (evaluateExpression(((SubtractOperation) expression).lhs) instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) evaluateExpression(((SubtractOperation) expression).lhs)).value - ((PixelLiteral) evaluateExpression(((SubtractOperation) expression).rhs)).value);
            } else if (evaluateExpression(((SubtractOperation) expression).lhs) instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) evaluateExpression(((SubtractOperation) expression).lhs)).value - ((PercentageLiteral) evaluateExpression(((SubtractOperation) expression).rhs)).value);
            } else if (evaluateExpression(((SubtractOperation) expression).lhs) instanceof ScalarLiteral) {
                return new ScalarLiteral(((ScalarLiteral) evaluateExpression(((SubtractOperation) expression).lhs)).value - ((ScalarLiteral) evaluateExpression(((SubtractOperation) expression).rhs)).value);
            }
        } else if (expression instanceof MultiplyOperation) {
            if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof PixelLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((PixelLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof PixelLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof ScalarLiteral) {
                return new PixelLiteral(((PixelLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof ScalarLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof PixelLiteral) {
                return new PixelLiteral(((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((PixelLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);

            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof PercentageLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((PercentageLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof PercentageLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof ScalarLiteral) {
                return new PercentageLiteral(((PercentageLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof ScalarLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof PercentageLiteral) {
                return new PercentageLiteral(((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((PercentageLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);

            } else if (evaluateExpression(((MultiplyOperation) expression).lhs) instanceof ScalarLiteral && evaluateExpression(((MultiplyOperation) expression).rhs) instanceof ScalarLiteral) {
                return new ScalarLiteral(((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).lhs)).value * ((ScalarLiteral) evaluateExpression(((MultiplyOperation) expression).rhs)).value);
            }
        }
        if (expression instanceof Literal) {
            return (Literal) expression;
        } if (expression instanceof VariableReference){
            for (int i = 0; i < variableValues.getSize(); i++) {
                HashMap<String, Literal> map = variableValues.get(i);
                if(map.containsKey(((VariableReference) expression).name)) {
                    return map.get(((VariableReference) expression).name);
                }
            }
        }
        return null;
    }
}
