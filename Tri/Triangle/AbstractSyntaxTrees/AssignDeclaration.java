package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class AssignDeclaration extends Declaration {

	public AssignDeclaration(Identifier iAST, Expression eAST, SourcePosition declarationPos) {
		super(declarationPos);
		I = iAST;
		E = eAST;
		Vn = new SimpleVname(I, declarationPos);
		A= new AssignCommand(Vn, E, declarationPos);
	}
	public Object visit(Visitor v, Object o) {
		return v.visitAssignDeclaration(this, o);
	}

	public void display(int indent){
		super.display(indent);
		I.display(indent+1);
		E.display(indent+1);
	}

	public Identifier I;
	public Expression E;
	public TypeDenoter T;
	public VarDeclaration V;
	public AssignCommand A;
	public SimpleVname Vn;


}
