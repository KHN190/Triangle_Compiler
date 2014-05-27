package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;


public class ForCommand extends Command {

	public Identifier I;
	public Expression E1;
	public Expression E2;
	public Command C;

	public ForCommand(Identifier iAST, Expression e1AST, Expression e2AST, Command cAST, SourcePosition thePosition) {
		super(thePosition);
		I = iAST;
		E1 = e1AST;
		E2 = e2AST;
		C = cAST;
	}

	public Object visit(Visitor v, Object o) {
		  return v.visitForCommand(this, o);
	  }
	  
	  public void display(int indent) {
		  super.display(indent);
		  I.display(indent+1);
		  E1.display(indent+1);
		  E2.display(indent+1);
		  C.display(indent+1);
	  }
}
