package Triangle.Optimizer;

import sun.tools.tree.AddExpression;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.*;

public final class CopyOfOptimizer implements Visitor {
  public Object visitAssignCommand(AssignCommand ast, Object o) {
      AssignCommand newAST = (AssignCommand) ast.clone();
      if (newAST.V != null) {
        Vname newChildAST = (Vname) newAST.V.visit(this,o);
        if (newChildAST != null) {
          newAST.V = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitCallCommand(CallCommand ast, Object o) {
      CallCommand newAST = (CallCommand) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.APS != null) {
        ActualParameterSequence newChildAST = (ActualParameterSequence) newAST.APS.visit(this,o);
        if (newChildAST != null) {
          newAST.APS = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitCaseCommand(CaseCommand ast, Object o) {
      CaseCommand newAST = (CaseCommand) ast.clone();
      if (newAST.C != null) {
        Command newChildAST = (Command) newAST.C.visit(this,o);
        if (newChildAST != null) {
          newAST.C = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitEmptyCommand(EmptyCommand ast, Object o) {
      EmptyCommand newAST = (EmptyCommand) ast.clone();
      return newAST;
  }

  public Object visitIfCommand(IfCommand ast, Object o) {
      IfCommand newAST = (IfCommand) ast.clone();
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      if (newAST.C1 != null) {
        Command newChildAST = (Command) newAST.C1.visit(this,o);
        if (newChildAST != null) {
          newAST.C1 = newChildAST;
        }
      }
      if (newAST.C2 != null) {
        Command newChildAST = (Command) newAST.C2.visit(this,o);
        if (newChildAST != null) {
          newAST.C2 = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitLetCommand(LetCommand ast, Object o) {
      LetCommand newAST = (LetCommand) ast.clone();
      if (newAST.D != null) {
        Declaration newChildAST = (Declaration) newAST.D.visit(this,o);
        if (newChildAST != null) {
          newAST.D = newChildAST;
        }
      }
      if (newAST.C != null) {
        Command newChildAST = (Command) newAST.C.visit(this,o);
        if (newChildAST != null) {
          newAST.C = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSequentialCommand(SequentialCommand ast, Object o) {
      SequentialCommand newAST = (SequentialCommand) ast.clone();
      if (newAST.C1 != null) {
        Command newChildAST = (Command) newAST.C1.visit(this,o);
        if (newChildAST != null) {
          newAST.C1 = newChildAST;
        }
      }
      if (newAST.C2 != null) {
        Command newChildAST = (Command) newAST.C2.visit(this,o);
        if (newChildAST != null) {
          newAST.C2 = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitWhileCommand(WhileCommand ast, Object o) {
      WhileCommand newAST = (WhileCommand) ast.clone();
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      if (newAST.C != null) {
        Command newChildAST = (Command) newAST.C.visit(this,o);
        if (newChildAST != null) {
          newAST.C = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitRepeatCommand(RepeatCommand ast, Object o) {
      RepeatCommand newAST = (RepeatCommand) ast.clone();
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      if (newAST.C != null) {
        Command newChildAST = (Command) newAST.C.visit(this,o);
        if (newChildAST != null) {
          newAST.C = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitForCommand(ForCommand ast, Object o) {
      ForCommand newAST = (ForCommand) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.E1 != null) {
        Expression newChildAST = (Expression) newAST.E1.visit(this,o);
        if (newChildAST != null) {
          newAST.E1 = newChildAST;
        }
      }
      if (newAST.E2 != null) {
        Expression newChildAST = (Expression) newAST.E2.visit(this,o);
        if (newChildAST != null) {
          newAST.E2 = newChildAST;
        }
      }
      if (newAST.C != null) {
        Command newChildAST = (Command) newAST.C.visit(this,o);
        if (newChildAST != null) {
          newAST.C = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitArrayExpression(ArrayExpression ast, Object o) {
      ArrayExpression newAST = (ArrayExpression) ast.clone();
      if (newAST.AA != null) {
        ArrayAggregate newChildAST = (ArrayAggregate) newAST.AA.visit(this,o);
        if (newChildAST != null) {
          newAST.AA = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitBinaryExpression(BinaryExpression ast, Object o) {
      BinaryExpression newAST = (BinaryExpression) ast.clone();
      if (newAST.E1 != null) {
        Expression newChildAST = (Expression) newAST.E1.visit(this,o);
        if (newChildAST != null) {
          newAST.E1 = newChildAST;
        }
      }
      if (newAST.E2 != null) {
        Expression newChildAST = (Expression) newAST.E2.visit(this,o);
        if (newChildAST != null) {
          newAST.E2 = newChildAST;
        }
      }
      if (newAST.O != null) {
        Operator newChildAST = (Operator) newAST.O.visit(this,o);
        if (newChildAST != null) {
          newAST.O = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitMultExpression(MultExpression ast, Object o) {
	  MultExpression newAST = (MultExpression) ast.clone();
      if (newAST.E1 != null) {
        Expression newChildAST = (Expression) newAST.E1.visit(this,o);
        if (newChildAST != null) {
          newAST.E1 = newChildAST;
        }
      }
      if (newAST.E2 != null) {
        Expression newChildAST = (Expression) newAST.E2.visit(this,o);
        if (newChildAST != null) {
          newAST.E2 = newChildAST;
        }
      }
      if (newAST.MOP != null) {
        MultOperator newChildAST = (MultOperator) newAST.MOP.visit(this,o);
        if (newChildAST != null) {
          newAST.MOP = newChildAST;
        }
      }
      
      if(newAST.MOP.spelling.equals("*")){
    	  if(newAST.E1 instanceof IntegerExpression){
    		 IntegerExpression IE = (IntegerExpression) newAST.E1;
    		 if(IE.IL.spelling.equals("2")){
    			 newAST.E1 = (Expression)newAST.E2.visit(this, o);
    			 newAST.MOP.decl = StdEnvironment.addDecl;
    		 }
    	  }
    	  if(newAST.E2 instanceof IntegerExpression){
     		 IntegerExpression IE = (IntegerExpression) newAST.E2;
     		 if(IE.IL.spelling.equals("2")){
     			 newAST.MOP.decl = StdEnvironment.addDecl;
     			 newAST.E2 = (Expression)newAST.E1.clone();
     			 //return new MultExpression(newAST.E1, newAST.MOP, newAST.E1, newAST.getPosition());
     		 }
     	  }
      }
      return newAST;
  }

  public Object visitCallExpression(CallExpression ast, Object o) {
      CallExpression newAST = (CallExpression) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.APS != null) {
        ActualParameterSequence newChildAST = (ActualParameterSequence) newAST.APS.visit(this,o);
        if (newChildAST != null) {
          newAST.APS = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitCharacterExpression(CharacterExpression ast, Object o) {
      CharacterExpression newAST = (CharacterExpression) ast.clone();
      if (newAST.CL != null) {
        CharacterLiteral newChildAST = (CharacterLiteral) newAST.CL.visit(this,o);
        if (newChildAST != null) {
          newAST.CL = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitStringExpression(StringExpression ast, Object o) {
      StringExpression newAST = (StringExpression) ast.clone();
      if (newAST.SL != null) {
        StringLiteral newChildAST = (StringLiteral) newAST.SL.visit(this,o);
        if (newChildAST != null) {
          newAST.SL = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitEmptyExpression(EmptyExpression ast, Object o) {
      EmptyExpression newAST = (EmptyExpression) ast.clone();
      return newAST;
  }

  public Object visitIfExpression(IfExpression ast, Object o) {
      IfExpression newAST = (IfExpression) ast.clone();
      if (newAST.E1 != null) {
        Expression newChildAST = (Expression) newAST.E1.visit(this,o);
        if (newChildAST != null) {
          newAST.E1 = newChildAST;
        }
      }
      if (newAST.E2 != null) {
        Expression newChildAST = (Expression) newAST.E2.visit(this,o);
        if (newChildAST != null) {
          newAST.E2 = newChildAST;
        }
      }
      if (newAST.E3 != null) {
        Expression newChildAST = (Expression) newAST.E3.visit(this,o);
        if (newChildAST != null) {
          newAST.E3 = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitIntegerExpression(IntegerExpression ast, Object o) {
      IntegerExpression newAST = (IntegerExpression) ast.clone();
      if (newAST.IL != null) {
        IntegerLiteral newChildAST = (IntegerLiteral) newAST.IL.visit(this,o);
        if (newChildAST != null) {
          newAST.IL = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitLetExpression(LetExpression ast, Object o) {
      LetExpression newAST = (LetExpression) ast.clone();
      if (newAST.D != null) {
        Declaration newChildAST = (Declaration) newAST.D.visit(this,o);
        if (newChildAST != null) {
          newAST.D = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitRecordExpression(RecordExpression ast, Object o) {
      RecordExpression newAST = (RecordExpression) ast.clone();
      if (newAST.RA != null) {
        RecordAggregate newChildAST = (RecordAggregate) newAST.RA.visit(this,o);
        if (newChildAST != null) {
          newAST.RA = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitUnaryExpression(UnaryExpression ast, Object o) {
      UnaryExpression newAST = (UnaryExpression) ast.clone();
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      if (newAST.O != null) {
        Operator newChildAST = (Operator) newAST.O.visit(this,o);
        if (newChildAST != null) {
          newAST.O = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitVnameExpression(VnameExpression ast, Object o) {
      VnameExpression newAST = (VnameExpression) ast.clone();
      if (newAST.V != null) {
        Vname newChildAST = (Vname) newAST.V.visit(this,o);
        if (newChildAST != null) {
          newAST.V = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object o) {
      BinaryOperatorDeclaration newAST = (BinaryOperatorDeclaration) ast.clone();
      if (newAST.O != null) {
        Operator newChildAST = (Operator) newAST.O.visit(this,o);
        if (newChildAST != null) {
          newAST.O = newChildAST;
        }
      }
      if (newAST.ARG1 != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.ARG1.visit(this,o);
        if (newChildAST != null) {
          newAST.ARG1 = newChildAST;
        }
      }
      if (newAST.ARG2 != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.ARG2.visit(this,o);
        if (newChildAST != null) {
          newAST.ARG2 = newChildAST;
        }
      }
      if (newAST.RES != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.RES.visit(this,o);
        if (newChildAST != null) {
          newAST.RES = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitAssignDeclaration(AssignDeclaration ast, Object o) {
      AssignDeclaration newAST = (AssignDeclaration) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      if (newAST.V != null) {
        VarDeclaration newChildAST = (VarDeclaration) newAST.V.visit(this,o);
        if (newChildAST != null) {
          newAST.V = newChildAST;
        }
      }
      if (newAST.A != null) {
        AssignCommand newChildAST = (AssignCommand) newAST.A.visit(this,o);
        if (newChildAST != null) {
          newAST.A = newChildAST;
        }
      }
      if (newAST.Vn != null) {
        SimpleVname newChildAST = (SimpleVname) newAST.Vn.visit(this,o);
        if (newChildAST != null) {
          newAST.Vn = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitConstDeclaration(ConstDeclaration ast, Object o) {
      ConstDeclaration newAST = (ConstDeclaration) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
      FuncDeclaration newAST = (FuncDeclaration) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.FPS != null) {
        FormalParameterSequence newChildAST = (FormalParameterSequence) newAST.FPS.visit(this,o);
        if (newChildAST != null) {
          newAST.FPS = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitFuncOperDeclaration(FuncOperDeclaration ast, Object o) {
      FuncOperDeclaration newAST = (FuncOperDeclaration) ast.clone();
      if (newAST.O != null) {
        Operator newChildAST = (Operator) newAST.O.visit(this,o);
        if (newChildAST != null) {
          newAST.O = newChildAST;
        }
      }
      if (newAST.FPS != null) {
        FormalParameterSequence newChildAST = (FormalParameterSequence) newAST.FPS.visit(this,o);
        if (newChildAST != null) {
          newAST.FPS = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
      ProcDeclaration newAST = (ProcDeclaration) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.FPS != null) {
        FormalParameterSequence newChildAST = (FormalParameterSequence) newAST.FPS.visit(this,o);
        if (newChildAST != null) {
          newAST.FPS = newChildAST;
        }
      }
      if (newAST.C != null) {
        Command newChildAST = (Command) newAST.C.visit(this,o);
        if (newChildAST != null) {
          newAST.C = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o) {
      SequentialDeclaration newAST = (SequentialDeclaration) ast.clone();
      if (newAST.D1 != null) {
        Declaration newChildAST = (Declaration) newAST.D1.visit(this,o);
        if (newChildAST != null) {
          newAST.D1 = newChildAST;
        }
      }
      if (newAST.D2 != null) {
        Declaration newChildAST = (Declaration) newAST.D2.visit(this,o);
        if (newChildAST != null) {
          newAST.D2 = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitStringDeclaration(StringDeclaration ast, Object o) {
      StringDeclaration newAST = (StringDeclaration) ast.clone();
      if (newAST.IL != null) {
        IntegerLiteral newChildAST = (IntegerLiteral) newAST.IL.visit(this,o);
        if (newChildAST != null) {
          newAST.IL = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitConstStringDeclaration(ConstStringDeclaration ast, Object o) {
      ConstStringDeclaration newAST = (ConstStringDeclaration) ast.clone();
      if (newAST.IL != null) {
        IntegerLiteral newChildAST = (IntegerLiteral) newAST.IL.visit(this,o);
        if (newChildAST != null) {
          newAST.IL = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitTypeDeclaration(TypeDeclaration ast, Object o) {
      TypeDeclaration newAST = (TypeDeclaration) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object o) {
      UnaryOperatorDeclaration newAST = (UnaryOperatorDeclaration) ast.clone();
      if (newAST.O != null) {
        Operator newChildAST = (Operator) newAST.O.visit(this,o);
        if (newChildAST != null) {
          newAST.O = newChildAST;
        }
      }
      if (newAST.ARG != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.ARG.visit(this,o);
        if (newChildAST != null) {
          newAST.ARG = newChildAST;
        }
      }
      if (newAST.RES != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.RES.visit(this,o);
        if (newChildAST != null) {
          newAST.RES = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitVarDeclaration(VarDeclaration ast, Object o) {
      VarDeclaration newAST = (VarDeclaration) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object o) {
      MultipleArrayAggregate newAST = (MultipleArrayAggregate) ast.clone();
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      if (newAST.AA != null) {
        ArrayAggregate newChildAST = (ArrayAggregate) newAST.AA.visit(this,o);
        if (newChildAST != null) {
          newAST.AA = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o) {
      SingleArrayAggregate newAST = (SingleArrayAggregate) ast.clone();
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object o) {
      MultipleRecordAggregate newAST = (MultipleRecordAggregate) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      if (newAST.RA != null) {
        RecordAggregate newChildAST = (RecordAggregate) newAST.RA.visit(this,o);
        if (newChildAST != null) {
          newAST.RA = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object o) {
      SingleRecordAggregate newAST = (SingleRecordAggregate) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
      ConstFormalParameter newAST = (ConstFormalParameter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitConstStringFormalParameter(ConstStringFormalParameter ast, Object o) {
      ConstStringFormalParameter newAST = (ConstStringFormalParameter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      if (newAST.IL != null) {
        IntegerLiteral newChildAST = (IntegerLiteral) newAST.IL.visit(this,o);
        if (newChildAST != null) {
          newAST.IL = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o) {
      FuncFormalParameter newAST = (FuncFormalParameter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.FPS != null) {
        FormalParameterSequence newChildAST = (FormalParameterSequence) newAST.FPS.visit(this,o);
        if (newChildAST != null) {
          newAST.FPS = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitFuncOperFormalParameter(FuncOperFormalParameter ast, Object o) {
      FuncOperFormalParameter newAST = (FuncOperFormalParameter) ast.clone();
      if (newAST.O != null) {
        Operator newChildAST = (Operator) newAST.O.visit(this,o);
        if (newChildAST != null) {
          newAST.O = newChildAST;
        }
      }
      if (newAST.FPS != null) {
        FormalParameterSequence newChildAST = (FormalParameterSequence) newAST.FPS.visit(this,o);
        if (newChildAST != null) {
          newAST.FPS = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitProcFormalParameter(ProcFormalParameter ast, Object o) {
      ProcFormalParameter newAST = (ProcFormalParameter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.FPS != null) {
        FormalParameterSequence newChildAST = (FormalParameterSequence) newAST.FPS.visit(this,o);
        if (newChildAST != null) {
          newAST.FPS = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
      VarFormalParameter newAST = (VarFormalParameter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object o) {
      EmptyFormalParameterSequence newAST = (EmptyFormalParameterSequence) ast.clone();
      return newAST;
  }

  public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object o) {
      MultipleFormalParameterSequence newAST = (MultipleFormalParameterSequence) ast.clone();
      if (newAST.FP != null) {
        FormalParameter newChildAST = (FormalParameter) newAST.FP.visit(this,o);
        if (newChildAST != null) {
          newAST.FP = newChildAST;
        }
      }
      if (newAST.FPS != null) {
        FormalParameterSequence newChildAST = (FormalParameterSequence) newAST.FPS.visit(this,o);
        if (newChildAST != null) {
          newAST.FPS = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object o) {
      SingleFormalParameterSequence newAST = (SingleFormalParameterSequence) ast.clone();
      if (newAST.FP != null) {
        FormalParameter newChildAST = (FormalParameter) newAST.FP.visit(this,o);
        if (newChildAST != null) {
          newAST.FP = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
      ConstActualParameter newAST = (ConstActualParameter) ast.clone();
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitFuncActualParameter(FuncActualParameter ast, Object o) {
      FuncActualParameter newAST = (FuncActualParameter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitFuncOperActualParameter(FuncOperActualParameter ast, Object o) {
      FuncOperActualParameter newAST = (FuncOperActualParameter) ast.clone();
      if (newAST.O != null) {
        Operator newChildAST = (Operator) newAST.O.visit(this,o);
        if (newChildAST != null) {
          newAST.O = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
      ProcActualParameter newAST = (ProcActualParameter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitVarActualParameter(VarActualParameter ast, Object o) {
      VarActualParameter newAST = (VarActualParameter) ast.clone();
      if (newAST.V != null) {
        Vname newChildAST = (Vname) newAST.V.visit(this,o);
        if (newChildAST != null) {
          newAST.V = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object o) {
      EmptyActualParameterSequence newAST = (EmptyActualParameterSequence) ast.clone();
      return newAST;
  }

  public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object o) {
      MultipleActualParameterSequence newAST = (MultipleActualParameterSequence) ast.clone();
      if (newAST.AP != null) {
        ActualParameter newChildAST = (ActualParameter) newAST.AP.visit(this,o);
        if (newChildAST != null) {
          newAST.AP = newChildAST;
        }
      }
      if (newAST.APS != null) {
        ActualParameterSequence newChildAST = (ActualParameterSequence) newAST.APS.visit(this,o);
        if (newChildAST != null) {
          newAST.APS = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object o) {
      SingleActualParameterSequence newAST = (SingleActualParameterSequence) ast.clone();
      if (newAST.AP != null) {
        ActualParameter newChildAST = (ActualParameter) newAST.AP.visit(this,o);
        if (newChildAST != null) {
          newAST.AP = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o) {
      AnyTypeDenoter newAST = (AnyTypeDenoter) ast.clone();
      return newAST;
  }

  public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o) {
      ArrayTypeDenoter newAST = (ArrayTypeDenoter) ast.clone();
      if (newAST.IL != null) {
        IntegerLiteral newChildAST = (IntegerLiteral) newAST.IL.visit(this,o);
        if (newChildAST != null) {
          newAST.IL = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o) {
      BoolTypeDenoter newAST = (BoolTypeDenoter) ast.clone();
      return newAST;
  }

  public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o) {
      CharTypeDenoter newAST = (CharTypeDenoter) ast.clone();
      return newAST;
  }

  public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o) {
      ErrorTypeDenoter newAST = (ErrorTypeDenoter) ast.clone();
      return newAST;
  }

  public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast, Object o) {
      SimpleTypeDenoter newAST = (SimpleTypeDenoter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o) {
      IntTypeDenoter newAST = (IntTypeDenoter) ast.clone();
      return newAST;
  }

  public Object visitStringTypeDenoter(StringTypeDenoter ast, Object o) {
      StringTypeDenoter newAST = (StringTypeDenoter) ast.clone();
      if (newAST.IL != null) {
        IntegerLiteral newChildAST = (IntegerLiteral) newAST.IL.visit(this,o);
        if (newChildAST != null) {
          newAST.IL = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o) {
      RecordTypeDenoter newAST = (RecordTypeDenoter) ast.clone();
      if (newAST.FT != null) {
        FieldTypeDenoter newChildAST = (FieldTypeDenoter) newAST.FT.visit(this,o);
        if (newChildAST != null) {
          newAST.FT = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Object o) {
      MultipleFieldTypeDenoter newAST = (MultipleFieldTypeDenoter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      if (newAST.FT != null) {
        FieldTypeDenoter newChildAST = (FieldTypeDenoter) newAST.FT.visit(this,o);
        if (newChildAST != null) {
          newAST.FT = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Object o) {
      SingleFieldTypeDenoter newAST = (SingleFieldTypeDenoter) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.T != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.T.visit(this,o);
        if (newChildAST != null) {
          newAST.T = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitCharacterLiteral(CharacterLiteral ast, Object o) {
      CharacterLiteral newAST = (CharacterLiteral) ast.clone();
      return newAST;
  }

  public Object visitStringLiteral(StringLiteral ast, Object o) {
      StringLiteral newAST = (StringLiteral) ast.clone();
      return newAST;
  }

  public Object visitIdentifier(Identifier ast, Object o) {
      Identifier newAST = (Identifier) ast.clone();
      if (newAST.type != null) {
        TypeDenoter newChildAST = (TypeDenoter) newAST.type.visit(this,o);
        if (newChildAST != null) {
          newAST.type = newChildAST;
        }
      }
//      if (newAST.decl != null) {
//        AST newChildAST = (AST) newAST.decl.visit(this,o);
//        if (newChildAST != null) {
//          newAST.decl = newChildAST;
//        }
//      }
      return newAST;
  }

  public Object visitIntegerLiteral(IntegerLiteral ast, Object o) {
      IntegerLiteral newAST = (IntegerLiteral) ast.clone();
      return newAST;
  }

  public Object visitOperator(Operator ast, Object o) {
      Operator newAST = (Operator) ast.clone();
      if (newAST.decl != null) {
        Declaration newChildAST = (Declaration) newAST.decl.visit(this,o);
        if (newChildAST != null) {
          newAST.decl = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitMultOperator(MultOperator ast, Object o) {
      MultOperator newAST = (MultOperator) ast.clone();
      if (newAST.decl != null) {
        Declaration newChildAST = (Declaration) newAST.decl.visit(this,o);
        if (newChildAST != null) {
          newAST.decl = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitDotVname(DotVname ast, Object o) {
      DotVname newAST = (DotVname) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      if (newAST.V != null) {
        Vname newChildAST = (Vname) newAST.V.visit(this,o);
        if (newChildAST != null) {
          newAST.V = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSimpleVname(SimpleVname ast, Object o) {
      SimpleVname newAST = (SimpleVname) ast.clone();
      if (newAST.I != null) {
        Identifier newChildAST = (Identifier) newAST.I.visit(this,o);
        if (newChildAST != null) {
          newAST.I = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitSubscriptVname(SubscriptVname ast, Object o) {
      SubscriptVname newAST = (SubscriptVname) ast.clone();
      if (newAST.E != null) {
        Expression newChildAST = (Expression) newAST.E.visit(this,o);
        if (newChildAST != null) {
          newAST.E = newChildAST;
        }
      }
      if (newAST.V != null) {
        Vname newChildAST = (Vname) newAST.V.visit(this,o);
        if (newChildAST != null) {
          newAST.V = newChildAST;
        }
      }
      return newAST;
  }

  public Object visitProgram(Program ast, Object o) {
      Program newAST = (Program) ast.clone();
      if (newAST.C != null) {
        Command newChildAST = (Command) newAST.C.visit(this,o);
        if (newChildAST != null) {
          newAST.C = newChildAST;
        }
      }
      return newAST;
  }

  public Program optimize(Program ast) {
    return (Program) ast.visit(this, null);
  }
}
