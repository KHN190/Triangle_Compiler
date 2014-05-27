package Triangle.Optimizer;

import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.*;


public final class Optimizer_noClone implements Visitor {
	private ErrorReporter reporter;

	public Optimizer_noClone(ErrorReporter reporter) {
		this.reporter = reporter;
	}

	public Object visitAssignCommand(AssignCommand ast, Object o) {
		if (ast.V != null) {
			Vname newAST = (Vname) ast.V.visit(this,o);
			if (newAST != null) {
				ast.V = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitCallCommand(CallCommand ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.APS != null) {
			ActualParameterSequence newAST = (ActualParameterSequence) ast.APS.visit(this,o);
			if (newAST != null) {
				ast.APS = newAST;
			}
		}
		return ast;
	}

	public Object visitCaseCommand(CaseCommand ast, Object o) {
		if (ast.C != null) {
			Command newAST = (Command) ast.C.visit(this,o);
			if (newAST != null) {
				ast.C = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitEmptyCommand(EmptyCommand ast, Object o) {
		return ast;
	}

	public Object visitIfCommand(IfCommand ast, Object o) {
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		if (ast.C1 != null) {
			Command newAST = (Command) ast.C1.visit(this,o);
			if (newAST != null) {
				ast.C1 = newAST;
			}
		}
		if (ast.C2 != null) {
			Command newAST = (Command) ast.C2.visit(this,o);
			if (newAST != null) {
				ast.C2 = newAST;
			}
		}
		if(ast.E instanceof VnameExpression){
			VnameExpression VE = (VnameExpression)ast.E;
			if(VE.V instanceof SimpleVname){
				SimpleVname SV = (SimpleVname)VE.V;
				if(SV.I.spelling.equals("true"))
					return ast.C1;
				else if (SV.I.spelling.equals("false"))
					return ast.C2;
			}
		}
		return ast;
	}

	public Object visitLetCommand(LetCommand ast, Object o) {
		if (ast.D != null) {
			Declaration newAST = (Declaration) ast.D.visit(this,o);
			if (newAST != null) {
				ast.D = newAST;
			}
		}
		if (ast.C != null) {
			Command newAST = (Command) ast.C.visit(this,o);
			if (newAST != null) {
				ast.C = newAST;
			}
		}
		return ast;
	}

	public Object visitSequentialCommand(SequentialCommand ast, Object o) {
		if (ast.C1 != null) {
			Command newAST = (Command) ast.C1.visit(this,o);
			if (newAST != null) {
				ast.C1 = newAST;
			}
		}
		if (ast.C2 != null) {
			Command newAST = (Command) ast.C2.visit(this,o);
			if (newAST != null) {
				ast.C2 = newAST;
			}
		}
		return ast;
	}

	public Object visitWhileCommand(WhileCommand ast, Object o) {
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		if (ast.C != null) {
			Command newAST = (Command) ast.C.visit(this,o);
			if (newAST != null) {
				ast.C = newAST;
			}
		}
		return ast;
	}

	public Object visitRepeatCommand(RepeatCommand ast, Object o) {
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		if (ast.C != null) {
			Command newAST = (Command) ast.C.visit(this,o);
			if (newAST != null) {
				ast.C = newAST;
			}
		}
		return ast;
	}

	public Object visitForCommand(ForCommand ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.E1 != null) {
			Expression newAST = (Expression) ast.E1.visit(this,o);
			if (newAST != null) {
				ast.E1 = newAST;
			}
		}
		if (ast.E2 != null) {
			Expression newAST = (Expression) ast.E2.visit(this,o);
			if (newAST != null) {
				ast.E2 = newAST;
			}
		}
		if (ast.C != null) {
			Command newAST = (Command) ast.C.visit(this,o);
			if (newAST != null) {
				ast.C = newAST;
			}
		}
		return ast;
	}

	public Object visitArrayExpression(ArrayExpression ast, Object o) {
		if (ast.AA != null) {
			ArrayAggregate newAST = (ArrayAggregate) ast.AA.visit(this,o);
			if (newAST != null) {
				ast.AA = newAST;
			}
		}
		return ast;
	}

	public Object visitBinaryExpression(BinaryExpression ast, Object o) {
		BinaryExpression binAST = (BinaryExpression)ast;
		if (binAST.E1 != null) {
			Expression newAST = (Expression) binAST.E1.visit(this,o);
			if (newAST != null) {
				binAST.E1 = newAST;
			}
		}
		if (binAST.E2 != null) {
			Expression newAST = (Expression) binAST.E2.visit(this,o);
			if (newAST != null) {
				binAST.E2 = newAST;
			}
		}
		if (binAST.O != null && (binAST.O.decl instanceof BinaryOperatorDeclaration)) {
			Operator newAST = (Operator) binAST.O.visit(this,o);
			if (newAST != null) {
				binAST.O = newAST;
			}
		}
		if(binAST.type instanceof IntTypeDenoter){
			if(binAST.E1 instanceof IntegerExpression && binAST.E2 instanceof IntegerExpression){
				IntegerExpression IE1 = (IntegerExpression)binAST.E1;
				IntegerExpression IE2 = (IntegerExpression)binAST.E2;
				String operator = binAST.O.spelling;
				int res = 0;
				if(operator.equals("+")){
					res = Integer.parseInt(IE1.IL.spelling) + Integer.parseInt(IE2.IL.spelling);
				}
				else if(operator.equals("-")){
					res = Integer.parseInt(IE1.IL.spelling) - Integer.parseInt(IE2.IL.spelling);
				}
				else if(operator.equals("//")){
					res = Integer.parseInt(IE1.IL.spelling) % Integer.parseInt(IE2.IL.spelling);
				}
				IntegerLiteral IL = new IntegerLiteral(res + "", ((IntegerExpression)(binAST.E1)).IL.getPosition());
				IntegerExpression IE = new IntegerExpression(IL, binAST.E1.getPosition());
				return IE;
			}
		}
		else if(binAST.type instanceof BoolTypeDenoter){
			if(binAST.E1 instanceof IntegerExpression && binAST.E2 instanceof IntegerExpression){
				IntegerExpression IE1 = (IntegerExpression)binAST.E1;
				IntegerExpression IE2 = (IntegerExpression)binAST.E2;
				String operator = binAST.O.spelling;
				Boolean res = true;
				if(operator.equals(">")){
					res = Integer.parseInt(IE1.IL.spelling) > Integer.parseInt(IE2.IL.spelling);
				}
				else if(operator.equals(">=")){
					res = Integer.parseInt(IE1.IL.spelling) >= Integer.parseInt(IE2.IL.spelling);
				}
				else if(operator.equals("<")){
					res = Integer.parseInt(IE1.IL.spelling) < Integer.parseInt(IE2.IL.spelling);
				}
				else if(operator.equals("<=")){
					res = Integer.parseInt(IE1.IL.spelling) <= Integer.parseInt(IE2.IL.spelling);
				}
				if(!operator.equals("=") && !operator.equals("\\")){
					Identifier I = new Identifier(res + "", ((IntegerExpression)(binAST.E1)).IL.getPosition());
					I.decl = StdEnvironment.trueDecl;
					I.type = StdEnvironment.booleanType;
					SimpleVname SV = new SimpleVname(I, ((IntegerExpression)(binAST.E1)).IL.getPosition());
					VnameExpression VE = new VnameExpression(SV, binAST.E1.getPosition());
					System.out.println(((SimpleVname)VE.V).I.spelling);
					return VE;
				}
			}
			if(binAST.E1 instanceof VnameExpression && binAST.E2 instanceof VnameExpression){
				VnameExpression IE1 = (VnameExpression)binAST.E1;
				VnameExpression IE2 = (VnameExpression)binAST.E2;
				String operator = binAST.O.spelling;
				Boolean res = true;
				if(operator.equals("/\\")){
					if(((SimpleVname)IE1.V).declaration instanceof ConstDeclaration){
						ConstDeclaration CD1 = (ConstDeclaration)((SimpleVname)IE1.V).declaration;
						ConstDeclaration CD2 = (ConstDeclaration)((SimpleVname)IE2.V).declaration;
						VnameExpression VN1 = (VnameExpression)CD1.E;
						VnameExpression VN2 = (VnameExpression)CD2.E;
						SimpleVname SV1 = (SimpleVname)VN1.V;
						SimpleVname SV2 = (SimpleVname)VN2.V;
						res = Boolean.parseBoolean(SV1.I.spelling) && Boolean.parseBoolean(SV2.I.spelling);
					}
					else res = Boolean.parseBoolean(((SimpleVname)IE1.V).I.spelling) && Boolean.parseBoolean(((SimpleVname)IE2.V).I.spelling);
				}
				else if(operator.equals("\\/")){
					if(((SimpleVname)IE1.V).declaration instanceof ConstDeclaration){
						ConstDeclaration CD1 = (ConstDeclaration)((SimpleVname)IE1.V).declaration;
						ConstDeclaration CD2 = (ConstDeclaration)((SimpleVname)IE2.V).declaration;
						VnameExpression VN1 = (VnameExpression)CD1.E;
						VnameExpression VN2 = (VnameExpression)CD2.E;
						SimpleVname SV1 = (SimpleVname)VN1.V;
						SimpleVname SV2 = (SimpleVname)VN2.V;
						res = Boolean.parseBoolean(SV1.I.spelling) || Boolean.parseBoolean(SV2.I.spelling);
					}
					else res = Boolean.parseBoolean(((SimpleVname)IE1.V).I.spelling) || Boolean.parseBoolean(((SimpleVname)IE2.V).I.spelling);
				}
				Identifier I = new Identifier(res + "", ((VnameExpression)(binAST.E1)).V.getPosition());
				I.decl = StdEnvironment.trueDecl;
				I.type = StdEnvironment.booleanType;
				SimpleVname SV = new SimpleVname(I, ((VnameExpression)(binAST.E1)).V.getPosition());
				VnameExpression VE = new VnameExpression(SV, binAST.E1.getPosition());
				return VE;
			}
		}
		return binAST;
	}

	public Object visitMultExpression(MultExpression ast, Object o) {
		MultExpression binAST = (MultExpression)ast;

		if (ast.E1 != null) {
			Expression newAST = (Expression) ast.E1.visit(this,o);
			if (newAST != null) {
				ast.E1 = newAST;
			}
		}
		if (ast.E2 != null) {
			Expression newAST = (Expression) ast.E2.visit(this,o);
			if (newAST != null) {
				ast.E2 = newAST;
			}
		}
		if (ast.MOP != null) {
			MultOperator newAST = (MultOperator) ast.MOP.visit(this,o);
			if (newAST != null) {
				ast.MOP = newAST;
			}
		}
		if(ast.MOP.decl instanceof FuncOperDeclaration){
			return ast;
		}
		if(binAST.E1 instanceof IntegerExpression && binAST.E2 instanceof IntegerExpression){
			IntegerExpression IE1 = (IntegerExpression)binAST.E1;
			IntegerExpression IE2 = (IntegerExpression)binAST.E2;
			String operator = binAST.MOP.spelling;
			int res = 0;
			if(operator.equals("*")){
				res = Integer.parseInt(IE1.IL.spelling) * Integer.parseInt(IE2.IL.spelling);
			}
			else if(operator.equals("/")){
				res = Integer.parseInt(IE1.IL.spelling) / Integer.parseInt(IE2.IL.spelling);
			}
			IntegerLiteral IL = new IntegerLiteral(res + "", ((IntegerExpression)(binAST.E1)).IL.getPosition());
			IntegerExpression IE = new IntegerExpression(IL, binAST.E1.getPosition());
			return IE;
		}
		else if(binAST.E1 instanceof VnameExpression && binAST.E2 instanceof VnameExpression){
			VnameExpression VE1 = (VnameExpression)binAST.E1;
			VnameExpression VE2 = (VnameExpression)binAST.E2;
			if(VE1.V.declaration instanceof ConstDeclaration && VE1.V.declaration instanceof ConstDeclaration){
				ConstDeclaration CD1 = (ConstDeclaration)VE1.V.declaration;
				ConstDeclaration CD2 = (ConstDeclaration)VE2.V.declaration;
				if(CD1.E instanceof IntegerExpression && CD2.E instanceof IntegerExpression){
					IntegerExpression IE1 = (IntegerExpression)CD1.E;
					IntegerExpression IE2 = (IntegerExpression)CD2.E;
					String operator = binAST.MOP.spelling;
					int res = 0;
					if(operator.equals("*")){
						res = Integer.parseInt(IE1.IL.spelling) * Integer.parseInt(IE2.IL.spelling);
					}
					else if(operator.equals("/")){
						res = Integer.parseInt(IE1.IL.spelling) / Integer.parseInt(IE2.IL.spelling);
					}
					IntegerLiteral IL = new IntegerLiteral(res + "", binAST.getPosition());
					IntegerExpression IE = new IntegerExpression(IL, binAST.getPosition());
					return IE;
				}
			}
		}
		if(ast.MOP.spelling.equals("*") && (ast.E1 instanceof IntegerExpression || ast.E2 instanceof IntegerExpression)){
			BinaryExpression beAST = new BinaryExpression(ast.E1, new AddOperator("+", ast.position), ast.E2, ast.position);
			if(ast.E1 instanceof IntegerExpression && !(ast.E2 instanceof IntegerExpression)){
				IntegerExpression IE1 = (IntegerExpression) ast.E1;
				if(IE1.IL.spelling.equals("2")){
					beAST.E1 = (Expression)ast.E2.visit(this, o);
					return beAST;
				}
			}
			else if(ast.E2 instanceof IntegerExpression && !(ast.E1 instanceof IntegerExpression)){
				IntegerExpression IE2 = (IntegerExpression) ast.E2;
				if(IE2.IL.spelling.equals("2")){
					beAST.E2 = (Expression)ast.E1.visit(this, o);
					return beAST;
				}
			}
			else if (ast.E1 instanceof IntegerExpression && ast.E2 instanceof IntegerExpression){
				IntegerExpression IE1 = (IntegerExpression) ast.E1;
				if(IE1.IL.spelling.equals("2")){
					beAST.E1 = (Expression)ast.E2.visit(this, o);
					return beAST;
				}
				IntegerExpression IE2 = (IntegerExpression) ast.E2;
				if(IE2.IL.spelling.equals("2")){
					beAST.E2 = (Expression)ast.E1.visit(this, o);
					return beAST;
				}
			}
		}
		return ast;
	}

	public Object visitCallExpression(CallExpression ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.APS != null) {
			ActualParameterSequence newAST = (ActualParameterSequence) ast.APS.visit(this,o);
			if (newAST != null) {
				ast.APS = newAST;
			}
		}
		return ast;
	}

	public Object visitCharacterExpression(CharacterExpression ast, Object o) {
		if (ast.CL != null) {
			CharacterLiteral newAST = (CharacterLiteral) ast.CL.visit(this,o);
			if (newAST != null) {
				ast.CL = newAST;
			}
		}
		return ast;
	}

	public Object visitStringExpression(StringExpression ast, Object o) {
		if (ast.SL != null) {
			StringLiteral newAST = (StringLiteral) ast.SL.visit(this,o);
			if (newAST != null) {
				ast.SL = newAST;
			}
		}
		return ast;
	}

	public Object visitEmptyExpression(EmptyExpression ast, Object o) {
		return ast;
	}

	public Object visitIfExpression(IfExpression ast, Object o) {
		if (ast.E1 != null) {
			Expression newAST = (Expression) ast.E1.visit(this,o);
			if (newAST != null) {
				ast.E1 = newAST;
			}
		}
		if (ast.E2 != null) {
			Expression newAST = (Expression) ast.E2.visit(this,o);
			if (newAST != null) {
				ast.E2 = newAST;
			}
		}
		if (ast.E3 != null) {
			Expression newAST = (Expression) ast.E3.visit(this,o);
			if (newAST != null) {
				ast.E3 = newAST;
			}
		}
		if(ast.E1 instanceof VnameExpression){
			VnameExpression VE = (VnameExpression)ast.E1;
			if(VE.V instanceof SimpleVname){
				SimpleVname SV = (SimpleVname)VE.V;
				if(SV.I.spelling.equals("true"))
					return ast.E2;
				else if (SV.I.spelling.equals("false"))
					return ast.E3;
			}
		}
		return ast;
	}

	public Object visitIntegerExpression(IntegerExpression ast, Object o) {
		if (ast.IL != null) {
			IntegerLiteral newAST = (IntegerLiteral) ast.IL.visit(this,o);
			if (newAST != null) {
				ast.IL = newAST;
			}
		}
		return ast;
	}

	public Object visitLetExpression(LetExpression ast, Object o) {
		if (ast.D != null) {
			Declaration newAST = (Declaration) ast.D.visit(this,o);
			if (newAST != null) {
				ast.D = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitRecordExpression(RecordExpression ast, Object o) {
		if (ast.RA != null) {
			RecordAggregate newAST = (RecordAggregate) ast.RA.visit(this,o);
			if (newAST != null) {
				ast.RA = newAST;
			}
		}
		return ast;
	}

	public Object visitUnaryExpression(UnaryExpression ast, Object o) {
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		if (ast.O != null) {
			Operator newAST = (Operator) ast.O.visit(this,o);
			if (newAST != null) {
				ast.O = newAST;
			}
		}
		if(ast.type instanceof BoolTypeDenoter && ast.O.spelling.equals("\\")){
			if(ast.E instanceof VnameExpression){
				VnameExpression IE1 = (VnameExpression)ast.E;
				Boolean res = true;
				res = !Boolean.parseBoolean(((SimpleVname)IE1.V).I.spelling);
				Identifier I = new Identifier(res + "", ((VnameExpression)(ast.E)).V.getPosition());
				I.decl = StdEnvironment.trueDecl;
				I.type = StdEnvironment.booleanType;
				SimpleVname SV = new SimpleVname(I, ((VnameExpression)(ast.E)).V.getPosition());
				VnameExpression VE = new VnameExpression(SV, ast.E.getPosition());
				return VE;
			}
		}
		return ast;
	}

	public Object visitVnameExpression(VnameExpression ast, Object o) {
		if(ast.V != null && ast.V.declaration instanceof ConstDeclaration && !((SimpleVname)ast.V).I.spelling.equals("true") && !((SimpleVname)ast.V).I.spelling.equals("false")){
			ConstDeclaration conAST = (ConstDeclaration) ast.V.declaration.visit(this, o);
			if(conAST.E instanceof VnameExpression){
				VnameExpression VE = (VnameExpression) conAST.E;
				return VE;
			}
			else if((conAST.E.type instanceof BoolTypeDenoter) && !conAST.isFor){
				IntegerExpression intAST = (IntegerExpression) conAST.E.visit(this, o);
				return intAST;
			}
		}
		else if (ast.V != null) {
			Vname newAST = (Vname) ast.V.visit(this,o);
			if (newAST != null) {
				ast.V = newAST;
			}
		}
		return ast;
	}

	public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object o) {
		if (ast.O != null) {
			Operator newAST = (Operator) ast.O.visit(this,o);
			if (newAST != null) {
				ast.O = newAST;
			}
		}
		if (ast.ARG1 != null) {
			TypeDenoter newAST = (TypeDenoter) ast.ARG1.visit(this,o);
			if (newAST != null) {
				ast.ARG1 = newAST;
			}
		}
		if (ast.ARG2 != null) {
			TypeDenoter newAST = (TypeDenoter) ast.ARG2.visit(this,o);
			if (newAST != null) {
				ast.ARG2 = newAST;
			}
		}
		if (ast.RES != null) {
			TypeDenoter newAST = (TypeDenoter) ast.RES.visit(this,o);
			if (newAST != null) {
				ast.RES = newAST;
			}
		}
		return ast;
	}

	public Object visitAssignDeclaration(AssignDeclaration ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		if (ast.V != null) {
			VarDeclaration newAST = (VarDeclaration) ast.V.visit(this,o);
			if (newAST != null) {
				ast.V = newAST;
			}
		}
		if (ast.A != null) {
			AssignCommand newAST = (AssignCommand) ast.A.visit(this,o);
			if (newAST != null) {
				ast.A = newAST;
				ast.A.varDec = true;
			}
		}
		if (ast.Vn != null) {
			SimpleVname newAST = (SimpleVname) ast.Vn.visit(this,o);
			if (newAST != null) {
				ast.Vn = newAST;
			}
		}
		return ast;
	}

	public Object visitConstDeclaration(ConstDeclaration ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.FPS != null) {
			FormalParameterSequence newAST = (FormalParameterSequence) ast.FPS.visit(this,o);
			if (newAST != null) {
				ast.FPS = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitFuncOperDeclaration(FuncOperDeclaration ast, Object o) {
		if (ast.O != null) {
			Operator newAST = (Operator) ast.O.visit(this,o);
			if (newAST != null) {
				ast.O = newAST;
			}
		}
		if (ast.FPS != null) {
			FormalParameterSequence newAST = (FormalParameterSequence) ast.FPS.visit(this,o);
			if (newAST != null) {
				ast.FPS = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.FPS != null) {
			FormalParameterSequence newAST = (FormalParameterSequence) ast.FPS.visit(this,o);
			if (newAST != null) {
				ast.FPS = newAST;
			}
		}
		if (ast.C != null) {
			Command newAST = (Command) ast.C.visit(this,o);
			if (newAST != null) {
				ast.C = newAST;
			}
		}
		return ast;
	}

	public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o) {
		if (ast.D1 != null) {
			Declaration newAST = (Declaration) ast.D1.visit(this,o);
			if (newAST != null) {
				ast.D1 = newAST;
			}
		}
		if (ast.D2 != null) {
			Declaration newAST = (Declaration) ast.D2.visit(this,o);
			if (newAST != null) {
				ast.D2 = newAST;
			}
		}
		return ast;
	}

	public Object visitStringDeclaration(StringDeclaration ast, Object o) {
		if (ast.IL != null) {
			IntegerLiteral newAST = (IntegerLiteral) ast.IL.visit(this,o);
			if (newAST != null) {
				ast.IL = newAST;
			}
		}
		return ast;
	}

	public Object visitConstStringDeclaration(ConstStringDeclaration ast, Object o) {
		if (ast.IL != null) {
			IntegerLiteral newAST = (IntegerLiteral) ast.IL.visit(this,o);
			if (newAST != null) {
				ast.IL = newAST;
			}
		}
		return ast;
	}

	public Object visitTypeDeclaration(TypeDeclaration ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		return ast;
	}

	public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object o) {
		if (ast.O != null) {
			Operator newAST = (Operator) ast.O.visit(this,o);
			if (newAST != null) {
				ast.O = newAST;
			}
		}
		if (ast.ARG != null) {
			TypeDenoter newAST = (TypeDenoter) ast.ARG.visit(this,o);
			if (newAST != null) {
				ast.ARG = newAST;
			}
		}
		if (ast.RES != null) {
			TypeDenoter newAST = (TypeDenoter) ast.RES.visit(this,o);
			if (newAST != null) {
				ast.RES = newAST;
			}
		}
		return ast;
	}

	public Object visitVarDeclaration(VarDeclaration ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		return ast;
	}

	public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object o) {
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		if (ast.AA != null) {
			ArrayAggregate newAST = (ArrayAggregate) ast.AA.visit(this,o);
			if (newAST != null) {
				ast.AA = newAST;
			}
		}
		return ast;
	}

	public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o) {
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		if (ast.RA != null) {
			RecordAggregate newAST = (RecordAggregate) ast.RA.visit(this,o);
			if (newAST != null) {
				ast.RA = newAST;
			}
		}
		return ast;
	}

	public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		return ast;
	}

	public Object visitConstStringFormalParameter(ConstStringFormalParameter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		if (ast.IL != null) {
			IntegerLiteral newAST = (IntegerLiteral) ast.IL.visit(this,o);
			if (newAST != null) {
				ast.IL = newAST;
			}
		}
		return ast;
	}

	public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.FPS != null) {
			FormalParameterSequence newAST = (FormalParameterSequence) ast.FPS.visit(this,o);
			if (newAST != null) {
				ast.FPS = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		return ast;
	}

	public Object visitFuncOperFormalParameter(FuncOperFormalParameter ast, Object o) {
		if (ast.O != null) {
			Operator newAST = (Operator) ast.O.visit(this,o);
			if (newAST != null) {
				ast.O = newAST;
			}
		}
		if (ast.FPS != null) {
			FormalParameterSequence newAST = (FormalParameterSequence) ast.FPS.visit(this,o);
			if (newAST != null) {
				ast.FPS = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		return ast;
	}

	public Object visitProcFormalParameter(ProcFormalParameter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.FPS != null) {
			FormalParameterSequence newAST = (FormalParameterSequence) ast.FPS.visit(this,o);
			if (newAST != null) {
				ast.FPS = newAST;
			}
		}
		return ast;
	}

	public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		return ast;
	}

	public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object o) {
		return ast;
	}

	public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object o) {
		if (ast.FP != null) {
			FormalParameter newAST = (FormalParameter) ast.FP.visit(this,o);
			if (newAST != null) {
				ast.FP = newAST;
			}
		}
		if (ast.FPS != null) {
			FormalParameterSequence newAST = (FormalParameterSequence) ast.FPS.visit(this,o);
			if (newAST != null) {
				ast.FPS = newAST;
			}
		}
		return ast;
	}

	public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object o) {
		if (ast.FP != null) {
			FormalParameter newAST = (FormalParameter) ast.FP.visit(this,o);
			if (newAST != null) {
				ast.FP = newAST;
			}
		}
		return ast;
	}

	public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		return ast;
	}

	public Object visitFuncActualParameter(FuncActualParameter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		return ast;
	}

	public Object visitFuncOperActualParameter(FuncOperActualParameter ast, Object o) {
		if (ast.O != null) {
			Operator newAST = (Operator) ast.O.visit(this,o);
			if (newAST != null) {
				ast.O = newAST;
			}
		}
		return ast;
	}

	public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		return ast;
	}

	public Object visitVarActualParameter(VarActualParameter ast, Object o) {
		if (ast.V != null) {
			Vname newAST = (Vname) ast.V.visit(this,o);
			if (newAST != null) {
				ast.V = newAST;
			}
		}
		return ast;
	}

	public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object o) {
		return ast;
	}

	public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object o) {
		if (ast.AP != null) {
			ActualParameter newAST = (ActualParameter) ast.AP.visit(this,o);
			if (newAST != null) {
				ast.AP = newAST;
			}
		}
		if (ast.APS != null) {
			ActualParameterSequence newAST = (ActualParameterSequence) ast.APS.visit(this,o);
			if (newAST != null) {
				ast.APS = newAST;
			}
		}
		return ast;
	}

	public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object o) {
		if (ast.AP != null) {
			ActualParameter newAST = (ActualParameter) ast.AP.visit(this,o);
			if (newAST != null) {
				ast.AP = newAST;
			}
		}
		return ast;
	}

	public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o) {
		return ast;
	}

	public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o) {
		if (ast.IL != null) {
			IntegerLiteral newAST = (IntegerLiteral) ast.IL.visit(this,o);
			if (newAST != null) {
				ast.IL = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		return ast;
	}

	public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o) {
		return ast;
	}

	public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o) {
		return ast;
	}

	public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o) {
		return ast;
	}

	public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		return ast;
	}

	public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o) {
		return ast;
	}

	public Object visitStringTypeDenoter(StringTypeDenoter ast, Object o) {
		if (ast.IL != null) {
			IntegerLiteral newAST = (IntegerLiteral) ast.IL.visit(this,o);
			if (newAST != null) {
				ast.IL = newAST;
			}
		}
		return ast;
	}

	public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o) {
		if (ast.FT != null) {
			FieldTypeDenoter newAST = (FieldTypeDenoter) ast.FT.visit(this,o);
			if (newAST != null) {
				ast.FT = newAST;
			}
		}
		return ast;
	}

	public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		if (ast.FT != null) {
			FieldTypeDenoter newAST = (FieldTypeDenoter) ast.FT.visit(this,o);
			if (newAST != null) {
				ast.FT = newAST;
			}
		}
		return ast;
	}

	public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.T != null) {
			TypeDenoter newAST = (TypeDenoter) ast.T.visit(this,o);
			if (newAST != null) {
				ast.T = newAST;
			}
		}
		return ast;
	}

	public Object visitCharacterLiteral(CharacterLiteral ast, Object o) {
		return ast;
	}

	public Object visitStringLiteral(StringLiteral ast, Object o) {
		return ast;
	}

	public Object visitIdentifier(Identifier ast, Object o) {
		if (ast.type != null) {
			TypeDenoter newAST = (TypeDenoter) ast.type.visit(this,o);
			if (newAST != null) {
				ast.type = newAST;
			}
		}
		if (ast.decl != null) {
			//        AST newAST = (AST) ast.decl.visit(this,o);
			//        if (newAST != null) {
				//          ast.decl = newAST;
				//        }
		}
		return ast;
	}

	public Object visitIntegerLiteral(IntegerLiteral ast, Object o) {
		return ast;
	}

	public Object visitOperator(Operator ast, Object o) {
		if (ast.decl != null) {
			Declaration newAST = (Declaration) ast.decl.visit(this,o);
			if (newAST != null) {
				ast.decl = newAST;
			}
		}
		return ast;
	}

	public Object visitMultOperator(MultOperator ast, Object o) {
		if (ast.decl != null) {
			Declaration newAST = (Declaration) ast.decl.visit(this,o);
			if (newAST != null) {
				ast.decl = newAST;
			}
		}
		return ast;
	}

	public Object visitDotVname(DotVname ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		if (ast.V != null) {
			Vname newAST = (Vname) ast.V.visit(this,o);
			if (newAST != null) {
				ast.V = newAST;
			}
		}
		return ast;
	}

	public Object visitSimpleVname(SimpleVname ast, Object o) {
		if (ast.I != null) {
			Identifier newAST = (Identifier) ast.I.visit(this,o);
			if (newAST != null) {
				ast.I = newAST;
			}
		}
		return ast;
	}

	public Object visitSubscriptVname(SubscriptVname ast, Object o) {
		int indexSize = 0;
		if (ast.E != null) {
			Expression newAST = (Expression) ast.E.visit(this,o);
			if (newAST != null) {
				ast.E = newAST;
			}
		}
		if (ast.V != null) {
			Vname newAST = (Vname) ast.V.visit(this,o);
			if (newAST != null) {
				ast.V = newAST;
			}
		}
		if(ast.V.type instanceof StringTypeDenoter){
			indexSize = ast.V.len;
		}
		else{
			ArrayTypeDenoter ATD = (ArrayTypeDenoter)ast.V.type;
			indexSize = Integer.parseInt(ATD.IL.spelling);
		}
		if (ast.E instanceof IntegerExpression) {
			IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
			if(Integer.parseInt(IL.spelling) >= indexSize){
				reporter.reportError("Array index " + Integer.parseInt(IL.spelling) +
						" out of bounds for array size " + indexSize," at line # ", ast.E.position);
			}
		}
//		if (ast.E instanceof VnameExpression) {
//				VnameExpression VE = (VnameExpression)ast.E;
//				IntegerLiteral IL = new IntegerLiteral("" + indexSize, ast.getPosition());
//				IntegerExpression E2 = new IntegerExpression(IL, ast.getPosition());
//				Operator O = new Operator(">=", ast.getPosition());
//				BinaryExpression BE = new BinaryExpression(VE, O, E2, ast.getPosition());
//				Identifier I = new Identifier("halt", ast.getPosition());
//				EmptyExpression EE = new EmptyExpression(ast.getPosition());
//				EmptyActualParameterSequence EAPS = new EmptyActualParameterSequence(ast.getPosition());
//				CallExpression CE = new CallExpression(I, EAPS, ast.getPosition());
//				CE.type = StdEnvironment.integerType;
//				EE.type = StdEnvironment.integerType;
//				IfExpression IE= new IfExpression(BE, CE, VE, ast.getPosition());
//				ast.E = IE;
//		}
		return ast;
	}

	public Object visitProgram(Program ast, Object o) {
		if (ast.C != null) {
			Command newAST = (Command) ast.C.visit(this,o);
			if (newAST != null) {
				ast.C = newAST;
			}
		}
		return ast;
	}

	public Program optimize(Program ast) {
		return (Program) ast.visit(this, null);
	}
}
