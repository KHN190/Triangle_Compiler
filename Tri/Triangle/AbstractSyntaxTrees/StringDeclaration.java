package Triangle.AbstractSyntaxTrees;

import Triangle.StdEnvironment;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class StringDeclaration extends VarDeclaration {

	public IntegerLiteral IL;
	
	public StringDeclaration(Identifier iAST, IntegerLiteral iLAST, SourcePosition thePosition) {
		super(iAST, StdEnvironment.stringType, thePosition);
		IL = iLAST;
	}

	@Override
	public Object visit(Visitor v, Object o) {
	    return v.visitStringDeclaration(this, o);
	  }

	  public void display(int indent) {
	      super.display(indent);
	      IL.display(indent+1);
	  }
}
