package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ConstStringDeclaration extends ConstDeclaration {

public IntegerLiteral IL;
	
	public ConstStringDeclaration(Identifier iAST, Expression eAST, IntegerLiteral ilAST, SourcePosition thePosition) {
		super(iAST, eAST, thePosition);
		IL = ilAST;
	}

	@Override
	public Object visit(Visitor v, Object o) {
	    return v.visitConstStringDeclaration(this, o);
	  }

	  public void display(int indent) {
	      super.display(indent);
	      IL.display(indent+1);
	  }
}
