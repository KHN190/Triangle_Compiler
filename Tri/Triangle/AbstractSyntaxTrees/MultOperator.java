package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class MultOperator extends Operator {

	public MultOperator(String spelling, SourcePosition previousTokenPosition) {
		super(spelling, previousTokenPosition);
		decl = null;

	}

	public Object visit(Visitor v, Object o) {
		return v.visitOperator(this, o);
	}

	public Declaration decl;	

}
