package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class MultExpression extends Expression {

	public MultExpression(Expression e1AST, Operator mopAST,
			Expression e2AST, SourcePosition thePosition) {
		super(thePosition);
		E1 = e1AST;
		MOP = mopAST;
		E2 = e2AST;
	}

	public Object visit(Visitor v, Object o) {
		return v.visitMultExpression(this, o);
	}

	public void display(int indent) {
		super.display(indent);
		E1.display(indent+1);
		MOP.display(indent+1);
		E2.display(indent+1);
	}

	public Expression E1;
	public Expression E2;
	public Operator MOP;

}
