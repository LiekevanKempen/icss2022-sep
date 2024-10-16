package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;

public class Generator {

	public String generate(AST ast) {
        return generateStylesheet((Stylesheet) ast.root);


	}

	private String generateStylesheet(Stylesheet root) {
		return generateStylerule((Stylerule) root.getChildren().get(0));

	}

	private String generateStylerule(Stylerule stylerule) {
		String result = stylerule.selectors.get(0) + "{\n";
		result += "\t" + generateDeclaration( (Declaration) stylerule.body.get(0)) ;
		result += "}";
		return result;
	}

	private String generateDeclaration(Declaration node) {
		String result = node.property.name + ": " + node.expression + "\n";
		return result;
	}


}
