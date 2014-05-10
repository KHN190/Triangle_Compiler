package Triangle.AbstractSyntaxTrees;

import java.util.LinkedHashMap;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class CaseCommand extends Command {

    public CaseCommand(Expression eAST, LinkedHashMap<IntegerLiteral, Command> map,
                       Command cAST, SourcePosition thePosition) {
      super(thePosition);
      E = eAST;
      MAP = map;
      C = cAST;
    }

    public Object visit(Visitor v, Object o) {
      return v.visitCaseCommand(this, o);
    }

    public LinkedHashMap<IntegerLiteral, Command> MAP;
    public Command C;
    public Expression E;
}