package Triangle.AbstractSyntaxTrees;

import java.util.LinkedHashMap;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class CaseCommand extends Command {

	public LinkedHashMap<IntegerLiteral, Command> MAP;
	public Command C;
	public Expression E;

	public CaseCommand(Expression eAST, LinkedHashMap<IntegerLiteral, Command> map, Command cAST,
			SourcePosition thePosition) {
		super(thePosition);
		E = eAST;
		MAP = map;
		C = cAST;		
	}

	public Object visit(Visitor v, Object o) {
		  return v.visitCaseCommand(this, o);
	  }
	  
	  public void display(int indent) {
		  super.display(indent);
		  E.display(indent + 1);
		  for(IntegerLiteral IL : MAP.keySet()){
			  IL.display(indent+1);
			  MAP.get(IL).display(indent+1);
		  }
		  C.display(indent+1);
	  }
}
