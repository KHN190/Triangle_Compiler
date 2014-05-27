package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RepeatCommand extends Command {

	  public Expression E;
	  public Command C;
	  
	public RepeatCommand(Command cAST, Expression eAST, SourcePosition thePosition) {
		super (thePosition);
		C = cAST;
		E = eAST;
	}

	public Object visit(Visitor v, Object o) {
		  return v.visitRepeatCommand(this, o);
	  }
	  
	  public void display(int indent) {
		  super.display(indent);
		  E.display(indent+1);
		  C.display(indent+1);
	  }
}
