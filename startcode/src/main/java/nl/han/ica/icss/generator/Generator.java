package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

	public String generate(AST ast) {
        return generateStylesheet((Stylesheet) ast.root);


	}

	private String generateStylesheet(Stylesheet root) {
		String result = "";
		for (int i = 0; i < root.getChildren().size(); i++) {
			if (root.getChildren().get(i) instanceof Stylerule) {
				result += generateStylerule((Stylerule) root.getChildren().get(i));
			}
		}
		return result;
	}


	private String generateStylerule(Stylerule stylerule) {
		String result = "";
		result = stylerule.selectors.get(0) + " {\n";
		for (int i = 0; i < stylerule.body.size(); i++) {
			if (stylerule.body.get(i) instanceof Declaration) {
			result += "  " + generateDeclaration((Declaration) stylerule.body.get(i)) ;
			}
		}
		result += "} \n";
		return result;
	}

	private String generateDeclaration(Declaration node) {
        return node.property.name + ": " + generateExpression(node.expression) + "\n";
	}

	private String generateExpression(Expression expression) {
		if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value + "%";
		} else if (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value + "px";
		} else if (expression instanceof BoolLiteral) {
            return ((BoolLiteral) expression).value ? "true" : "false";
		} else if (expression instanceof ScalarLiteral) {
            return ((ScalarLiteral) expression).value + "";
		} else if (expression instanceof ColorLiteral) {
            return ((ColorLiteral) expression).value;
		} else {
			return null;
		}
	}


}
