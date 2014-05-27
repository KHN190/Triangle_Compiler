/*
 * @(#)Checker.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package Triangle.ContextualAnalyzer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.*;
import Triangle.SyntacticAnalyzer.SourcePosition;

public final class Checker implements Visitor {

	// Commands

	// Always returns null. Does not use the given object.

	public Object visitAssignCommand(AssignCommand ast, Object o) {
		TypeDenoter vType = (TypeDenoter) ast.V.visit(this, null);
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		if(eType == null || vType == null) return null;
		if (!ast.V.variable)
			reporter.reportError ("LHS of assignment is not a variable", "", ast.V.position);
		if (! eType.equals(vType))
			reporter.reportError ("assignment incompatibilty", "", ast.position);
		if(vType.equals(StdEnvironment.stringType)){
			if(!ast.varDec){
				if(ast.E instanceof StringExpression){
					StringExpression se = (StringExpression) ast.E;
					SimpleVname vn = (SimpleVname) ast.V;
					StringDeclaration strd = (StringDeclaration) vn.I.visit(this, null);
					if(Integer.parseInt(strd.IL.spelling) != se.SL.spelling.length()){
						reporter.reportError ("Invalid string length (%) expected (" + strd.IL.spelling + ")", (""+se.SL.spelling.length()), ast.position);
					}
				}
				else if(ast.E instanceof VnameExpression){
					VnameExpression ve = (VnameExpression) ast.E;

					SimpleVname vn = (SimpleVname) ve.V;
					SimpleVname vn2 = (SimpleVname) ast.V;
					Declaration decl = (Declaration) vn.I.visit(this, null);
					StringDeclaration strd2 = (StringDeclaration) vn2.I.visit(this, null);
					if(decl instanceof ConstStringDeclaration){
						ConstStringDeclaration csd = (ConstStringDeclaration) decl;
						if(Integer.parseInt(csd.IL.spelling) != Integer.parseInt(strd2.IL.spelling)){
							reporter.reportError ("Invalid string length (%) expected (" + strd2.IL.spelling + ")", (""+csd.IL.spelling), ast.position);
						}
					}
					else{
						StringDeclaration strd = (StringDeclaration) decl;
						if(Integer.parseInt(strd.IL.spelling) != Integer.parseInt(strd2.IL.spelling)){
							reporter.reportError ("Invalid string length (%) expected (" + strd2.IL.spelling + ")", (""+strd.IL.spelling), ast.position);
						}
					}
				}
			}

		}
		return null;
	}


	public Object visitCallCommand(CallCommand ast, Object o) {

		Declaration binding = (Declaration) ast.I.visit(this, null);
		if (binding == null)
			reportUndeclared(ast.I);
		else if (binding instanceof ProcDeclaration) {
			ast.APS.visit(this, ((ProcDeclaration) binding).FPS);
		} else if (binding instanceof ProcFormalParameter) {
			ast.APS.visit(this, ((ProcFormalParameter) binding).FPS);
		} else
			reporter.reportError("\"%\" is not a procedure identifier",
					ast.I.spelling, ast.I.position);
		return null;
	}

	public Object visitEmptyCommand(EmptyCommand ast, Object o) {
		return null;
	}

	public Object visitIfCommand(IfCommand ast, Object o) {
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		if (! eType.equals(StdEnvironment.booleanType))
			reporter.reportError("Boolean expression expected here", "", ast.E.position);
		ast.C1.visit(this, null);
		ast.C2.visit(this, null);
		return null;
	}

	public Object visitLetCommand(LetCommand ast, Object o) {
		idTable.openScope();
		ast.D.visit(this, null);
		ast.C.visit(this, null);
		idTable.closeScope();
		return null;
	}

	public Object visitSequentialCommand(SequentialCommand ast, Object o) {
		ast.C1.visit(this, null);
		ast.C2.visit(this, null);
		return null;
	}

	public Object visitWhileCommand(WhileCommand ast, Object o) {
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		if (! eType.equals(StdEnvironment.booleanType))
			reporter.reportError("Boolean expression expected here", "", ast.E.position);
		ast.C.visit(this, null);
		return null;
	}

	public Object visitRepeatCommand(RepeatCommand ast, Object o) {
		ast.C.visit(this, null);
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		if(eType == null){
			return null;
		}
		if (! eType.equals(StdEnvironment.booleanType))
			reporter.reportError("Boolean expression expected here", "", ast.E.position);
		return null;
	}

	public Object visitForCommand(ForCommand ast, Object o) {
		idTable.openScope();
		ast.E1.visit(this, null);
		ast.E2.visit(this, null);
		if (ast.E1.type != StdEnvironment.integerType)
			reporter.reportError ("Expression one must resolve to an integer", "", ast.position);
		if(ast.E2.type != StdEnvironment.integerType)
			reporter.reportError ("Expression two must resolve to an integer", "", ast.position);
		ConstDeclaration cd = new ConstDeclaration(ast.I, ast.E1, ast.position);
		cd.visit(this, null);
		cd.isFor = true;
		ast.C.visit(this, null);
		idTable.closeScope();
		return null;
	}


	public Object visitCaseCommand(CaseCommand ast, Object o) {
		idTable.openScope();
		ast.E.visit(this, null);
		if(ast.E.type != StdEnvironment.integerType)
			reporter.reportError("incompatible expression type (Integer Expression expected)", "", ast.position);
		LinkedHashMap<IntegerLiteral, Command> MAP = ast.MAP;
		ArrayList<IntegerLiteral> AL = new ArrayList<IntegerLiteral>();
		for(IntegerLiteral IL : MAP.keySet()){
			for(IntegerLiteral IL2 : AL){
				if (IL2.spelling.equals(IL.spelling)){
					reporter.reportError("re-used integer literal in case", "", ast.position);
				}
			}
			Command C = MAP.get(IL);
			C.visit(this, null);
			IL.visit(this, null);
			AL.add(IL);
		}
		Command C = ast.C;
		C.visit(this, null);
		idTable.closeScope();
		return null;
	}

	// Expressions

	// Returns the TypeDenoter denoting the type of the expression. Does
	// not use the given object.

	public Object visitArrayExpression(ArrayExpression ast, Object o) {
		TypeDenoter elemType = (TypeDenoter) ast.AA.visit(this, null);
		IntegerLiteral il = new IntegerLiteral(new Integer(ast.AA.elemCount).toString(),
				ast.position);
		ast.type = new ArrayTypeDenoter(il, elemType, ast.position);
		return ast.type;
	}

	public Object visitBinaryExpression(BinaryExpression ast, Object o) {

		TypeDenoter e1Type = (TypeDenoter) ast.E1.visit(this, null);
		TypeDenoter e2Type = (TypeDenoter) ast.E2.visit(this, null);
		Declaration binding = (Declaration) ast.O.visit(this, null);

		if (binding == null)
			reportUndeclared(ast.O);
		else {
			if(binding instanceof FuncOperDeclaration){
				TypeDenoter e1Local = StdEnvironment.errorType;
				TypeDenoter e2Local = StdEnvironment.errorType;
				MultipleFormalParameterSequence mFPS = (MultipleFormalParameterSequence)((FuncOperDeclaration) binding).FPS;
				if(mFPS.FP instanceof ConstFormalParameter){
					ConstFormalParameter cFP = (ConstFormalParameter)mFPS.FP;
					e1Local = cFP.T;
				} else if(mFPS.FP instanceof ConstStringFormalParameter){
					ConstStringFormalParameter csFP = (ConstStringFormalParameter)mFPS.FP;
					e1Local = csFP.T;
				}
				SingleFormalParameterSequence sFPS = (SingleFormalParameterSequence) (mFPS.FPS);
				if(sFPS.FP instanceof ConstFormalParameter){
					ConstFormalParameter cFP = (ConstFormalParameter)sFPS.FP;
					e2Local = cFP.T;
				} else if(sFPS.FP instanceof ConstStringFormalParameter){
					ConstStringFormalParameter csFP = (ConstStringFormalParameter)sFPS.FP;
					e2Local = csFP.T;
				}
				if(e1Type != e1Local){
					reporter.reportError ("Parameter 1: type mismatch",	ast.O.spelling, ast.O.position);
				}
				if(e2Type != e2Local){
					reporter.reportError ("Parameter 1: type mismatch",	ast.O.spelling, ast.O.position);
				}
				ast.type = ((FuncOperDeclaration)(binding)).T;

				return ast.type;
			}
			else if (binding instanceof FuncOperFormalParameter){
				ast.type = ((FuncOperFormalParameter)(binding)).T;
				return ast.type;
			}			
			else if (! (binding instanceof BinaryOperatorDeclaration))
				reporter.reportError ("\"%\" is not a binary or function operator",
						ast.O.spelling, ast.O.position);
			BinaryOperatorDeclaration bbinding = (BinaryOperatorDeclaration) binding;
			if (bbinding.ARG1 == StdEnvironment.anyType) {
				// this operator must be "=" or "\="
				if (! e1Type.equals(e2Type))
					reporter.reportError ("incompatible argument types for \"%\"",
							ast.O.spelling, ast.position);
			} else if (! e1Type.equals(bbinding.ARG1))
				reporter.reportError ("wrong argument type for \"%\"",
						ast.O.spelling, ast.E1.position);
			else if (! e2Type.equals(bbinding.ARG2))
				reporter.reportError ("wrong argument type for \"%\"",
						ast.O.spelling, ast.E2.position);
			ast.type = bbinding.RES;
		}
		return ast.type;
	}

	public Object visitCallExpression(CallExpression ast, Object o) {
		Declaration binding = (Declaration) ast.I.visit(this, null);
		if (binding == null) {
			reportUndeclared(ast.I);
			ast.type = StdEnvironment.errorType;
		} else if (binding instanceof FuncDeclaration) {
			ast.APS.visit(this, ((FuncDeclaration) binding).FPS);
			ast.type = ((FuncDeclaration) binding).T;
		} else if (binding instanceof FuncFormalParameter) {
			ast.APS.visit(this, ((FuncFormalParameter) binding).FPS);
			ast.type = ((FuncFormalParameter) binding).T;
		} else
			reporter.reportError("\"%\" is not a function identifier",
					ast.I.spelling, ast.I.position);
		return ast.type;
	}

	public Object visitCharacterExpression(CharacterExpression ast, Object o) {
		ast.type = StdEnvironment.charType;
		return ast.type;
	}

	public Object visitEmptyExpression(EmptyExpression ast, Object o) {
		//ast.type = null;
		ast.type = StdEnvironment.integerType;
		return ast.type;
	}

	public Object visitIfExpression(IfExpression ast, Object o) {
		TypeDenoter e1Type = (TypeDenoter) ast.E1.visit(this, null);
		if (! e1Type.equals(StdEnvironment.booleanType))
			reporter.reportError ("Boolean expression expected here", "",
					ast.E1.position);
		TypeDenoter e2Type = (TypeDenoter) ast.E2.visit(this, null);
		TypeDenoter e3Type = (TypeDenoter) ast.E3.visit(this, null);
		if (! e2Type.equals(e3Type))
			reporter.reportError ("incompatible limbs in if-expression", "", ast.position);
		ast.type = e2Type;
		return ast.type;
	}

	public Object visitIntegerExpression(IntegerExpression ast, Object o) {
		ast.type = StdEnvironment.integerType;
		return ast.type;
	}

	public Object visitLetExpression(LetExpression ast, Object o) {
		idTable.openScope();
		ast.D.visit(this, null);
		ast.type = (TypeDenoter) ast.E.visit(this, null);
		idTable.closeScope();
		return ast.type;
	}

	public Object visitRecordExpression(RecordExpression ast, Object o) {
		FieldTypeDenoter rType = (FieldTypeDenoter) ast.RA.visit(this, null);
		ast.type = new RecordTypeDenoter(rType, ast.position);
		return ast.type;
	}

	public Object visitUnaryExpression(UnaryExpression ast, Object o) {
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		Declaration binding = (Declaration) ast.O.visit(this, null);
		if (binding == null) {
			reportUndeclared(ast.O);
			ast.type = StdEnvironment.errorType;
		} 	
		else if (binding instanceof FuncOperDeclaration){
			TypeDenoter e1Local = StdEnvironment.errorType;
			SingleFormalParameterSequence mFPS = (SingleFormalParameterSequence)((FuncOperDeclaration) binding).FPS;
			if(mFPS.FP instanceof ConstFormalParameter){
				ConstFormalParameter cFP = (ConstFormalParameter)mFPS.FP;
				e1Local = cFP.T;
			} else if(mFPS.FP instanceof ConstStringFormalParameter){
				ConstStringFormalParameter csFP = (ConstStringFormalParameter)mFPS.FP;
				e1Local = csFP.T;
			}
			if(eType != e1Local){
				reporter.reportError ("Parameter 1: type mismatch",	ast.O.spelling, ast.O.position);
				ast.type = StdEnvironment.errorType;
			}
			ast.type = ((FuncOperDeclaration)(binding)).T;
		}
		else if (binding instanceof FuncOperFormalParameter){
			ast.type =  ((FuncOperFormalParameter)binding).T;
		}
		else if (! (binding instanceof UnaryOperatorDeclaration)){
			reporter.reportError ("\"%\" is not a unary operator",
					ast.O.spelling, ast.O.position);
		}
		else {
			UnaryOperatorDeclaration ubinding = (UnaryOperatorDeclaration) binding;
			if (! eType.equals(ubinding.ARG))
				reporter.reportError ("wrong argument type for \"%\"",
						ast.O.spelling, ast.O.position);
			ast.type = ubinding.RES;
		}
		return ast.type;
	}



	public Object visitMultExpression(MultExpression ast, Object o) {
		TypeDenoter eType1 = (TypeDenoter) ast.E1.visit(this, null);
		if(eType1 != StdEnvironment.integerType)
			reporter.reportError ("Integer expression expected",	"", ast.E1.position);
		TypeDenoter eType2 = (TypeDenoter) ast.E2.visit(this, null);
		if(eType2 != StdEnvironment.integerType)
			reporter.reportError ("Integer expression expected",	"", ast.E1.position);
		Declaration binding = (Declaration) ast.MOP.visit(this, null);
		if (binding == null) {
			reportUndeclared(ast.MOP);
			ast.type = StdEnvironment.errorType;
		}else if ((!(binding instanceof BinaryOperatorDeclaration) && !(binding instanceof FuncOperDeclaration)))
			reporter.reportError ("\"%\" is not a binary or function operator",
					ast.MOP.spelling, ast.MOP.position);
		else {
			if(binding instanceof BinaryOperatorDeclaration){
				BinaryOperatorDeclaration bbinding = (BinaryOperatorDeclaration) binding;
				if (! eType1.equals(bbinding.ARG1))
					reporter.reportError ("wrong argument type for \"%\"",
							ast.MOP.spelling, ast.MOP.position);
				if (! eType2.equals(bbinding.ARG2))
					reporter.reportError ("wrong argument type for \"%\"",
							ast.MOP.spelling, ast.MOP.position);
				ast.type = bbinding.RES;
			}
			else if(binding instanceof FuncOperDeclaration){
				FuncOperDeclaration bbinding = (FuncOperDeclaration) binding;
				MultipleFormalParameterSequence MFPS = (MultipleFormalParameterSequence)bbinding.FPS;
				SingleFormalParameterSequence MFPS2 = (SingleFormalParameterSequence)MFPS.FPS;
				ConstFormalParameter FP1 = (ConstFormalParameter)MFPS.FP;
				ConstFormalParameter FP2 = (ConstFormalParameter)MFPS2.FP;
				if (! eType1.equals(FP1.T))
					reporter.reportError ("wrong argument type for \"%\"",
							ast.MOP.spelling, ast.MOP.position);
				if (! eType2.equals(FP2.T))
					reporter.reportError ("wrong argument type for \"%\"",
							ast.MOP.spelling, ast.MOP.position);
				ast.type = bbinding.T;
			}
		}
		return ast.type;
	}

	public Object visitVnameExpression(VnameExpression ast, Object o) {
		ast.type = (TypeDenoter) ast.V.visit(this, null);
		return ast.type;
	}


	public Object visitStringExpression(StringExpression ast, Object o) {
		ast.type = StdEnvironment.stringType;
		if(ast.SL.spelling.length() == 0){
			reporter.reportError ("Empty string", "", ast.position);
			return StdEnvironment.errorType;
		}
		return ast.type;
	}

	// Declarations

	// Always returns null. Does not use the given object.
	public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object o) {
		return null;
	}

	public Object visitConstDeclaration(ConstDeclaration ast, Object o) {
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		idTable.enter(ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("identifier \"%\" already declared",
					ast.I.spelling, ast.position);
		return null;
	}

	public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter (ast.I.spelling, ast); // permits recursion
		if (ast.duplicated)
			reporter.reportError ("identifier \"%\" already declared",
					ast.I.spelling, ast.position);
		idTable.openScope();
		ast.FPS.visit(this, null);
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		idTable.closeScope();
		if (! ast.T.equals(eType))
			reporter.reportError ("body of function \"%\" has wrong type",
					ast.I.spelling, ast.E.position);
		return null;
	}

	public Object visitFuncOperDeclaration(FuncOperDeclaration ast,	Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter (ast.O.spelling, ast); // permits recursion
		if (ast.duplicated)
			reporter.reportError ("identifier \"%\" already declared",
					ast.O.spelling, ast.position);
		idTable.openScope();
		ast.FPS.visit(this, null);
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		idTable.closeScope();
		if (! ast.T.equals(eType))
			reporter.reportError ("body of function \"%\" has wrong type",
					ast.O.spelling, ast.E.position);
		return null;
	}

	public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
		idTable.enter (ast.I.spelling, ast); // permits recursion
		if (ast.duplicated)
			reporter.reportError ("identifier \"%\" already declared",
					ast.I.spelling, ast.position);
		idTable.openScope();
		ast.FPS.visit(this, null);
		ast.C.visit(this, null);
		idTable.closeScope();
		return null;
	}

	public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o) {
		ast.D1.visit(this, null);
		ast.D2.visit(this, null);
		return null;
	}

	public Object visitTypeDeclaration(TypeDeclaration ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter (ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("identifier \"%\" already declared",
					ast.I.spelling, ast.position);
		return null;
	}

	public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object o) {
		return null;
	}

	public Object visitVarDeclaration(VarDeclaration ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter (ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("identifier \"%\" already declared",
					ast.I.spelling, ast.position);

		return null;
	}

	public Object visitAssignDeclaration(AssignDeclaration ast,	Object o) {
		ast.E.visit(this, null);
		TypeDenoter TD = ast.E.type;
		if(TD instanceof CharTypeDenoter){
			ast.T = new CharTypeDenoter(ast.position);
			ast.T = (CharTypeDenoter) ast.T.visit(this, null);
		}
		else if(TD instanceof StringTypeDenoter){
			ast.T = (StringTypeDenoter) TD;
			ast.T = (StringTypeDenoter) ast.T.visit(this, null);
		}
		else if(TD instanceof BoolTypeDenoter){
			ast.T = new BoolTypeDenoter(ast.position);
			ast.T = (BoolTypeDenoter) ast.T.visit(this, null);
		}
		else if(TD instanceof IntTypeDenoter){
			ast.T = new IntTypeDenoter(ast.position);
			ast.T = (IntTypeDenoter) ast.T.visit(this, null);
		}
		else if(TD instanceof RecordTypeDenoter){
			RecordExpression RE = (RecordExpression) ast.E;
			ast.T = new RecordTypeDenoter(RE.RA.type, ast.position);
			ast.T = (RecordTypeDenoter) ast.T.visit(this, null);
		}
		else if(TD instanceof ArrayTypeDenoter){
			ArrayTypeDenoter atd = (ArrayTypeDenoter) TD;
			ast.T = new ArrayTypeDenoter(atd.IL, atd.T, ast.position);
			ast.T = (ArrayTypeDenoter) ast.T.visit(this, null);
		}
		else {reporter.reportError ("unkown expression type (how did you do this?)", "", ast.position); return null;}
		if(TD instanceof StringTypeDenoter){
			ast.V = new StringDeclaration(ast.I, new IntegerLiteral("" + ((StringExpression)ast.E).SL.spelling.length(), ast.position), ast.position);
			ast.V.visit(this, null);
		}
		else{
			ast.V = new VarDeclaration(ast.I, ast.T, ast.position);
			ast.V.visit(this, null);
		}
		ast.A.varDec = true;
		ast.A.visit(this, null);
		return null;
	}

	public Object visitStringDeclaration(StringDeclaration ast, Object o) {
		idTable.enter (ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("identifier \"%\" already declared",
					ast.I.spelling, ast.position);
		return null;
	}

	public Object visitConstStringDeclaration(ConstStringDeclaration ast, Object o) {
		ast.E.visit(this, null);
		idTable.enter (ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("identifier \"%\" already declared",
					ast.I.spelling, ast.position);
		return null;
	}

	// Array Aggregates

	// Returns the TypeDenoter for the Array Aggregate. Does not use the
	// given object.

	public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object o) {
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		TypeDenoter elemType = (TypeDenoter) ast.AA.visit(this, null);
		ast.elemCount = ast.AA.elemCount + 1;
		if (! eType.equals(elemType))
			reporter.reportError ("incompatible array-aggregate element", "", ast.E.position);
		return elemType;
	}

	public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o) {
		TypeDenoter elemType = (TypeDenoter) ast.E.visit(this, null);
		ast.elemCount = 1;
		return elemType;
	}

	// Record Aggregates

	// Returns the TypeDenoter for the Record Aggregate. Does not use the
	// given object.

	public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object o) {
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		FieldTypeDenoter rType = (FieldTypeDenoter) ast.RA.visit(this, null);
		TypeDenoter fType = checkFieldIdentifier(rType, ast.I);
		if (fType != StdEnvironment.errorType)
			reporter.reportError ("duplicate field \"%\" in record",
					ast.I.spelling, ast.I.position);
		ast.type = new MultipleFieldTypeDenoter(ast.I, eType, rType, ast.position);
		return ast.type;
	}

	public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object o) {
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		ast.type = new SingleFieldTypeDenoter(ast.I, eType, ast.position);
		return ast.type;
	}

	// Formal Parameters

	// Always returns null. Does not use the given object.

	public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter(ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("duplicated formal parameter \"%\"",
					ast.I.spelling, ast.position);
		return null;
	}

	public Object visitConstStringFormalParameter(ConstStringFormalParameter ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter(ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("duplicated formal parameter \"%\"",
					ast.I.spelling, ast.position);
		return null;
	}

	public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o) {
		idTable.openScope();
		ast.FPS.visit(this, null);
		idTable.closeScope();
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter (ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("duplicated formal parameter \"%\"",
					ast.I.spelling, ast.position);
		return null;
	}

	public Object visitFuncOperFormalParameter(FuncOperFormalParameter ast, Object o) {
		idTable.openScope();
		ast.FPS.visit(this, null);
		idTable.closeScope();
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter (ast.O.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("duplicated formal parameter \"%\"",
					ast.O.spelling, ast.position);
		return null;
	}

	public Object visitProcFormalParameter(ProcFormalParameter ast, Object o) {
		idTable.openScope();
		ast.FPS.visit(this, null);
		idTable.closeScope();
		idTable.enter (ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("duplicated formal parameter \"%\"",
					ast.I.spelling, ast.position);
		return null;
	}

	public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		idTable.enter (ast.I.spelling, ast);
		if (ast.duplicated)
			reporter.reportError ("duplicated formal parameter \"%\"",
					ast.I.spelling, ast.position);
		return null;
	}

	public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object o) {
		return null;
	}

	public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object o) {
		ast.FP.visit(this, null);
		ast.FPS.visit(this, null);
		return null;
	}

	public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object o) {
		ast.FP.visit(this, null);
		return null;
	}

	// Actual Parameters

	// Always returns null. Uses the given FormalParameter.

	public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
		FormalParameter fp = (FormalParameter) o;
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		if (fp instanceof ConstFormalParameter){
			if (! eType.equals(((ConstFormalParameter) fp).T))
				reporter.reportError ("wrong type for const actual parameter", "",
						ast.E.position);
		} else if (fp instanceof ConstStringFormalParameter){
			if (! eType.equals(((ConstStringFormalParameter) fp).T))
				reporter.reportError ("wrong type for const string parameter", "",
						ast.E.position);
		}
		else
			reporter.reportError ("const actual parameter not expected here", "", ast.position);
		return null;
	}

	public Object visitFuncActualParameter(FuncActualParameter ast, Object o) {
		FormalParameter fp = (FormalParameter) o;

		Declaration binding = (Declaration) ast.I.visit(this, null);
		if (binding == null)
			reportUndeclared (ast.I);
		else if (! (binding instanceof FuncDeclaration ||
				binding instanceof FuncFormalParameter))
			reporter.reportError ("\"%\" is not a function identifier",
					ast.I.spelling, ast.I.position);
		else if (! (fp instanceof FuncFormalParameter))
			reporter.reportError ("func actual parameter not expected here", "",
					ast.position);
		else {
			FormalParameterSequence FPS = null;
			TypeDenoter T = null;
			if (binding instanceof FuncDeclaration) {
				FPS = ((FuncDeclaration) binding).FPS;
				T = ((FuncDeclaration) binding).T;
			} else {
				FPS = ((FuncFormalParameter) binding).FPS;
				T = ((FuncFormalParameter) binding).T;
			}
			if (! FPS.equals(((FuncFormalParameter) fp).FPS))
				reporter.reportError ("wrong signature for function \"%\"",
						ast.I.spelling, ast.I.position);
			else if (! T.equals(((FuncFormalParameter) fp).T))
				reporter.reportError ("wrong type for function \"%\"",
						ast.I.spelling, ast.I.position);
		}
		return null;
	}

	public Object visitFuncOperActualParameter(FuncOperActualParameter ast, Object o) {
		FormalParameter fp = (FormalParameter) o;
		Declaration binding = (Declaration) ast.O.visit(this, null);
		if (binding == null)
			reportUndeclared (ast.O);
		else if (! (binding instanceof FuncDeclaration ||
				binding instanceof FuncFormalParameter || 
				binding instanceof FuncOperFormalParameter || 
				binding instanceof FuncOperDeclaration ||
				binding instanceof BinaryOperatorDeclaration))
			reporter.reportError ("\"%\" is not a function identifier",
					ast.O.spelling, ast.O.position);
		else if (! (fp instanceof FuncFormalParameter || fp instanceof FuncOperFormalParameter))
			reporter.reportError ("func actual parameter not expected here", "",
					ast.position);
		else {
			FormalParameterSequence FPS = null;
			TypeDenoter T = null;
			if (binding instanceof FuncDeclaration) {
				FPS = ((FuncDeclaration) binding).FPS;
				T = ((FuncDeclaration) binding).T;
			} else if (binding instanceof FuncOperDeclaration) {
				FPS = ((FuncOperDeclaration) binding).FPS;
				T = ((FuncOperDeclaration) binding).T;
			} else if (binding instanceof BinaryOperatorDeclaration) {
				BinaryOperatorDeclaration binaryBinding = (BinaryOperatorDeclaration) binding;
				if(binaryBinding.O.spelling.equals("+"))
					binaryBinding = StdEnvironment.addDecl;
				else if(binaryBinding.O.spelling.equals("-"))
					binaryBinding = StdEnvironment.subtractDecl;
				else if(binaryBinding.O.spelling.equals("*"))
					binaryBinding = StdEnvironment.multiplyDecl;
				else if(binaryBinding.O.spelling.equals("/"))
					binaryBinding = StdEnvironment.divideDecl;
				else if(binaryBinding.O.spelling.equals("//"))
					binaryBinding = StdEnvironment.moduloDecl;
				else if(binaryBinding.O.spelling.equals("<"))
					binaryBinding = StdEnvironment.lessDecl;
				else if(binaryBinding.O.spelling.equals(">"))
					binaryBinding = StdEnvironment.greaterDecl;
				else if(binaryBinding.O.spelling.equals("<="))
					binaryBinding = StdEnvironment.notgreaterDecl;
				else if(binaryBinding.O.spelling.equals(">="))
					binaryBinding = StdEnvironment.notlessDecl;
				else if(binaryBinding.O.spelling.equals("="))
					binaryBinding = StdEnvironment.equalDecl;
				else if(binaryBinding.O.spelling.equals("\\="))
					binaryBinding = StdEnvironment.unequalDecl;
				else if(binaryBinding.O.spelling.equals("\\="))
					binaryBinding = StdEnvironment.unequalDecl;
				else
					reporter.reportError("Standard binary operator expected here", 
							binaryBinding.O.spelling, ast.O.position);
				T = binaryBinding.RES;
			} 

			else if(binding instanceof FuncOperFormalParameter){
				FPS = ((FuncOperFormalParameter) binding).FPS;
				T = ((FuncOperFormalParameter) binding).T;
			}
			else {
				FPS = ((FuncFormalParameter) binding).FPS;
				T = ((FuncFormalParameter) binding).T;
			}
			if(fp instanceof FuncFormalParameter){
				if (! FPS.equals(((FuncFormalParameter) fp).FPS))
					reporter.reportError ("wrong signature for function \"%\"",
							ast.O.spelling, ast.O.position);
				else if (! T.equals(((FuncFormalParameter) fp).T))
					reporter.reportError ("wrong type for function \"%\"",
							ast.O.spelling, ast.O.position);
			} else if (binding instanceof BinaryOperatorDeclaration) {
				BinaryOperatorDeclaration binaryBinding = (BinaryOperatorDeclaration)binding;
				if(((MultipleFormalParameterSequence)((FuncOperFormalParameter)fp).FPS).FP instanceof ConstFormalParameter){
					ConstFormalParameter CFP = (ConstFormalParameter)((MultipleFormalParameterSequence)((FuncOperFormalParameter)fp).FPS).FP;
					SingleFormalParameterSequence SFPS = (SingleFormalParameterSequence)((MultipleFormalParameterSequence)((FuncOperFormalParameter)fp).FPS).FPS;
					ConstFormalParameter CFP2 = (ConstFormalParameter)SFPS.FP;
					if(!binaryBinding.RES.equals(((FuncOperFormalParameter)fp).T))
						reporter.reportError ("Imporper return type of function ", binaryBinding.O.spelling, binaryBinding.O.position);
					if(!(CFP.T instanceof IntTypeDenoter && CFP2.T instanceof IntTypeDenoter))
						reporter.reportError ("Imporper parameter type in function ", binaryBinding.O.spelling, binaryBinding.O.position);
				}
				if(((MultipleFormalParameterSequence)((FuncOperFormalParameter)fp).FPS).FP instanceof ConstStringFormalParameter){
					ConstStringFormalParameter CSFP = (ConstStringFormalParameter)((MultipleFormalParameterSequence)((FuncOperFormalParameter)fp).FPS).FP;
					SingleFormalParameterSequence SFPS = (SingleFormalParameterSequence)((MultipleFormalParameterSequence)((FuncOperFormalParameter)fp).FPS).FPS;
					ConstStringFormalParameter CSFP2 = (ConstStringFormalParameter)SFPS.FP;
					if(!binaryBinding.RES.equals(((FuncOperFormalParameter)fp).T))
						reporter.reportError ("Imporper return type of function ", binaryBinding.O.spelling, binaryBinding.O.position);
					if(!(CSFP.T instanceof StringTypeDenoter && CSFP2.T instanceof StringTypeDenoter))
						reporter.reportError ("Imporper parameter type in function ", binaryBinding.O.spelling, binaryBinding.O.position);
				}
			} else if(fp instanceof FuncOperFormalParameter){
				if (! FPS.equals(((FuncOperFormalParameter) fp).FPS))
					reporter.reportError ("wrong signature for function \"%\"",
							ast.O.spelling, ast.O.position);
				else if (! T.equals(((FuncOperFormalParameter) fp).T))
					reporter.reportError ("wrong type for function \"%\"",
							ast.O.spelling, ast.O.position);
			}
		}
		return null;
	}

	public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
		FormalParameter fp = (FormalParameter) o;

		Declaration binding = (Declaration) ast.I.visit(this, null);
		if (binding == null)
			reportUndeclared (ast.I);
		else if (! (binding instanceof ProcDeclaration ||
				binding instanceof ProcFormalParameter))
			reporter.reportError ("\"%\" is not a procedure identifier",
					ast.I.spelling, ast.I.position);
		else if (! (fp instanceof ProcFormalParameter))
			reporter.reportError ("proc actual parameter not expected here", "",
					ast.position);
		else {
			FormalParameterSequence FPS = null;
			if (binding instanceof ProcDeclaration)
				FPS = ((ProcDeclaration) binding).FPS;
			else
				FPS = ((ProcFormalParameter) binding).FPS;
			if (! FPS.equals(((ProcFormalParameter) fp).FPS))
				reporter.reportError ("wrong signature for procedure \"%\"",
						ast.I.spelling, ast.I.position);
		}
		return null;
	}

	public Object visitVarActualParameter(VarActualParameter ast, Object o) {
		FormalParameter fp = (FormalParameter) o;

		TypeDenoter vType = (TypeDenoter) ast.V.visit(this, null);
		if (! ast.V.variable)
			reporter.reportError ("actual parameter is not a variable", "",
					ast.V.position);
		else if (! (fp instanceof VarFormalParameter))
			reporter.reportError ("var actual parameter not expected here", "",
					ast.V.position);
		else if (! vType.equals(((VarFormalParameter) fp).T))
			reporter.reportError ("wrong type for var actual parameter", "",
					ast.V.position);
		return null;
	}

	public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object o) {
		FormalParameterSequence fps = (FormalParameterSequence) o;
		if (! (fps instanceof EmptyFormalParameterSequence))
			reporter.reportError ("too few actual parameters", "", ast.position);
		return null;
	}

	public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object o) {
		FormalParameterSequence fps = (FormalParameterSequence) o;
		if (! (fps instanceof MultipleFormalParameterSequence))
			reporter.reportError ("too many actual parameters", "", ast.position);
		else {
			ast.AP.visit(this, ((MultipleFormalParameterSequence) fps).FP);
			ast.APS.visit(this, ((MultipleFormalParameterSequence) fps).FPS);
		}
		return null;
	}

	public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object o) {
		FormalParameterSequence fps = (FormalParameterSequence) o;
		if (! (fps instanceof SingleFormalParameterSequence))
			reporter.reportError ("incorrect number of actual parameters", "", ast.position);
		else {
			ast.AP.visit(this, ((SingleFormalParameterSequence) fps).FP);
		}
		return null;
	}

	// Type Denoters

	// Returns the expanded version of the TypeDenoter. Does not
	// use the given object.

	public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o) {
		return StdEnvironment.anyType;
	}

	public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		if ((Integer.valueOf(ast.IL.spelling).intValue()) == 0)
			reporter.reportError ("arrays must not be empty", "", ast.IL.position);
		return ast;
	}

	public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o) {
		return StdEnvironment.booleanType;
	}

	public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o) {
		return StdEnvironment.charType;
	}

	public Object visitStringTypeDenoter(StringTypeDenoter ast,	Object o) {
		return StdEnvironment.stringType;
	}

	public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o) {
		return StdEnvironment.errorType;
	}

	public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast, Object o) {
		Declaration binding = (Declaration) ast.I.visit(this, null);
		if (binding == null) {
			reportUndeclared (ast.I);
			return StdEnvironment.errorType;
		} else if (! (binding instanceof TypeDeclaration)) {
			reporter.reportError ("\"%\" is not a type identifier",
					ast.I.spelling, ast.I.position);
			return StdEnvironment.errorType;
		}
		return ((TypeDeclaration) binding).T;
	}

	public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o) {
		return StdEnvironment.integerType;
	}

	public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o) {
		ast.FT = (FieldTypeDenoter) ast.FT.visit(this, null);
		return ast;
	}

	public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		ast.FT.visit(this, null);
		return ast;
	}

	public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Object o) {
		ast.T = (TypeDenoter) ast.T.visit(this, null);
		return ast;
	}

	// Literals, Identifiers and Operators
	public Object visitCharacterLiteral(CharacterLiteral CL, Object o) {
		return StdEnvironment.charType;
	}

	public Object visitIdentifier(Identifier I, Object o) {
		Declaration binding = idTable.retrieve(I.spelling);
		if (binding != null)
			I.decl = binding;
		return binding;
	}

	public Object visitIntegerLiteral(IntegerLiteral IL, Object o) {
		return StdEnvironment.integerType;
	}

	public Object visitStringLiteral(StringLiteral stringLiteral, Object o) {
		return StdEnvironment.stringType;
	}

	public Object visitOperator(Operator O, Object o) {
		Declaration binding = idTable.retrieve(O.spelling);
		if (binding != null)
			O.decl = binding;
		return binding;
	}

	public Object visitMultOperator(MultOperator MO, Object o) {
		Declaration binding = idTable.retrieve(MO.spelling);
		if (binding != null)
			MO.decl = binding;
		return binding;
	}

	// Value-or-variable names

	// Determines the address of a named object (constant or variable).
	// This consists of a base object, to which 0 or more field-selection
	// or array-indexing operations may be applied (if it is a record or
	// array).  As much as possible of the address computation is done at
	// compile-time. Code is generated only when necessary to evaluate
	// index expressions at run-time.
	// currentLevel is the routine level where the v-name occurs.
	// frameSize is the anticipated size of the local stack frame when
	// the object is addressed at run-time.
	// It returns the description of the base object.
	// offset is set to the total of any field offsets (plus any offsets
	// due to index expressions that happen to be literals).
	// indexed is set to true iff there are any index expressions (other
	// than literals). In that case code is generated to compute the
	// offset due to these indexing operations at run-time.

	// Returns the TypeDenoter of the Vname. Does not use the
	// given object.

	public Object visitDotVname(DotVname ast, Object o) {
		ast.type = null;
		TypeDenoter vType = (TypeDenoter) ast.V.visit(this, null);
		ast.variable = ast.V.variable;
		if (! (vType instanceof RecordTypeDenoter))
			reporter.reportError ("record expected here", "", ast.V.position);
		else {
			ast.type = checkFieldIdentifier(((RecordTypeDenoter) vType).FT, ast.I);
			if (ast.type == StdEnvironment.errorType)
				reporter.reportError ("no field \"%\" in this record type",
						ast.I.spelling, ast.I.position);
		}
		return ast.type;
	}

	public Object visitSimpleVname(SimpleVname ast, Object o) {
		ast.variable = false;
		ast.type = StdEnvironment.errorType;
		Declaration binding = (Declaration) ast.I.visit(this, null);
		if (binding == null)
			reportUndeclared(ast.I);
		else
			ast.declaration = binding;
			if (binding instanceof ConstStringDeclaration) {
				ast.type = StdEnvironment.stringType;
				ast.variable = false;
				ast.len = Integer.parseInt(((ConstStringDeclaration)binding).IL.spelling);
			}else if (binding instanceof StringDeclaration) {
				ast.type = StdEnvironment.stringType;
				ast.variable = true;
				ast.len = Integer.parseInt(((StringDeclaration)binding).IL.spelling);
			} else if (binding instanceof VarDeclaration) {
				ast.type = ((VarDeclaration) binding).T;
				ast.variable = true;
			}  else if (binding instanceof ConstDeclaration) {
				ast.type = ((ConstDeclaration) binding).E.type;
				ast.variable = false;
			} else if (binding instanceof ConstFormalParameter) {
				ast.type = ((ConstFormalParameter) binding).T;
				ast.variable = false;
			} else if (binding instanceof ConstStringFormalParameter) {
				ast.type = StdEnvironment.stringType;
				ast.variable = false;
				ast.len = Integer.parseInt(((ConstStringFormalParameter)binding).IL.spelling);
			} else if (binding instanceof VarFormalParameter) {
				ast.type = ((VarFormalParameter) binding).T;
				ast.variable = true;
			} else{
				reporter.reportError ("\"%\" is not a const or var identifier",
						ast.I.spelling, ast.I.position);
			}
		return ast.type;
	}

	public Object visitSubscriptVname(SubscriptVname ast, Object o) {
		TypeDenoter vType = (TypeDenoter) ast.V.visit(this, null);
		ast.variable = ast.V.variable;
		TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
		if (vType != StdEnvironment.errorType) {	
			if (! (vType instanceof ArrayTypeDenoter) && !(vType instanceof StringTypeDenoter))
				reporter.reportError ("array or string expected here", "", ast.V.position);
			else {
				if (! eType.equals(StdEnvironment.integerType))
					reporter.reportError ("Integer expression expected here", "",
							ast.E.position);
				if(vType instanceof ArrayTypeDenoter) ast.type = ((ArrayTypeDenoter) vType).T;
				else ast.type = StdEnvironment.charType;
			}
		}
		return ast.type;
	}

	// Programs

	public Object visitProgram(Program ast, Object o) {
		ast.C.visit(this, null);
		return null;
	}

	// Checks whether the source program, represented by its AST, satisfies the
	// language's scope rules and type rules.
	// Also decorates the AST as follows:
	//  (a) Each applied occurrence of an identifier or operator is linked to
	//      the corresponding declaration of that identifier or operator.
	//  (b) Each expression and value-or-variable-name is decorated by its type.
	//  (c) Each type identifier is replaced by the type it denotes.
	// Types are represented by small ASTs.

	public void check(Program ast) {
		ast.visit(this, null);
	}

	/////////////////////////////////////////////////////////////////////////////

	public Checker (ErrorReporter reporter) {
		this.reporter = reporter;
		this.idTable = new IdentificationTable();
		establishStdEnvironment();
	}

	private IdentificationTable idTable;
	private static SourcePosition dummyPos = new SourcePosition();
	private ErrorReporter reporter;

	// Reports that the identifier or operator used at a leaf of the AST
	// has not been declared.

	private void reportUndeclared (Terminal leaf) {
		reporter.reportError("\"%\" is not declared", leaf.spelling, leaf.position);
	}


	private static TypeDenoter checkFieldIdentifier(FieldTypeDenoter ast, Identifier I) {
		if (ast instanceof MultipleFieldTypeDenoter) {
			MultipleFieldTypeDenoter ft = (MultipleFieldTypeDenoter) ast;
			if (ft.I.spelling.compareTo(I.spelling) == 0) {
				I.decl = ast;
				return ft.T;
			} else {
				return checkFieldIdentifier (ft.FT, I);
			}
		} else if (ast instanceof SingleFieldTypeDenoter) {
			SingleFieldTypeDenoter ft = (SingleFieldTypeDenoter) ast;
			if (ft.I.spelling.compareTo(I.spelling) == 0) {
				I.decl = ast;
				return ft.T;
			}
		}
		return StdEnvironment.errorType;
	}


	// Creates a small AST to represent the "declaration" of a standard
	// type, and enters it in the identification table.

	private TypeDeclaration declareStdType (String id, TypeDenoter typedenoter) {

		TypeDeclaration binding;

		binding = new TypeDeclaration(new Identifier(id, dummyPos), typedenoter, dummyPos);
		idTable.enter(id, binding);
		return binding;
	}

	// Creates a small AST to represent the "declaration" of a standard
	// type, and enters it in the identification table.

	private ConstDeclaration declareStdConst (String id, TypeDenoter constType) {

		IntegerExpression constExpr;
		ConstDeclaration binding;

		// constExpr used only as a placeholder for constType
		constExpr = new IntegerExpression(null, dummyPos);
		constExpr.type = constType;
		binding = new ConstDeclaration(new Identifier(id, dummyPos), constExpr, dummyPos);
		idTable.enter(id, binding);
		return binding;
	}

	// Creates a small AST to represent the "declaration" of a standard
	// type, and enters it in the identification table.

	private ProcDeclaration declareStdProc (String id, FormalParameterSequence fps) {

		ProcDeclaration binding;

		binding = new ProcDeclaration(new Identifier(id, dummyPos), fps,
				new EmptyCommand(dummyPos), dummyPos);
		idTable.enter(id, binding);
		return binding;
	}

	// Creates a small AST to represent the "declaration" of a standard
	// type, and enters it in the identification table.

	private FuncDeclaration declareStdFunc (String id, FormalParameterSequence fps,
			TypeDenoter resultType) {

		FuncDeclaration binding;

		binding = new FuncDeclaration(new Identifier(id, dummyPos), fps, resultType,
				new EmptyExpression(dummyPos), dummyPos);
		idTable.enter(id, binding);
		return binding;
	}

	// Creates a small AST to represent the "declaration" of a
	// unary operator, and enters it in the identification table.
	// This "declaration" summarises the operator's type info.

	private UnaryOperatorDeclaration declareStdUnaryOp
	(String op, TypeDenoter argType, TypeDenoter resultType) {

		UnaryOperatorDeclaration binding;

		binding = new UnaryOperatorDeclaration (new Operator(op, dummyPos),
				argType, resultType, dummyPos);
		idTable.enter(op, binding);
		return binding;
	}

	// Creates a small AST to represent the "declaration" of a
	// binary operator, and enters it in the identification table.
	// This "declaration" summarises the operator's type info.

	private BinaryOperatorDeclaration declareStdBinaryOp
	(String op, TypeDenoter arg1Type, TypeDenoter arg2type, TypeDenoter resultType) {

		BinaryOperatorDeclaration binding;

		binding = new BinaryOperatorDeclaration (new Operator(op, dummyPos),
				arg1Type, arg2type, resultType, dummyPos);
		idTable.enter(op, binding);
		return binding;
	}

	// Creates small ASTs to represent the standard types.
	// Creates small ASTs to represent "declarations" of standard types,
	// constants, procedures, functions, and operators.
	// Enters these "declarations" in the identification table.

	private final static Identifier dummyI = new Identifier("", dummyPos);

	private void establishStdEnvironment () {

		// idTable.startIdentification();
		StdEnvironment.booleanType = new BoolTypeDenoter(dummyPos);
		StdEnvironment.integerType = new IntTypeDenoter(dummyPos);
		StdEnvironment.charType = new CharTypeDenoter(dummyPos);
		StdEnvironment.anyType = new AnyTypeDenoter(dummyPos);
		StdEnvironment.errorType = new ErrorTypeDenoter(dummyPos);
		StdEnvironment.stringType = new StringTypeDenoter(dummyPos);
		StdEnvironment.booleanDecl = declareStdType("Boolean", StdEnvironment.booleanType);
		StdEnvironment.falseDecl = declareStdConst("false", StdEnvironment.booleanType);
		StdEnvironment.trueDecl = declareStdConst("true", StdEnvironment.booleanType);
		StdEnvironment.notDecl = declareStdUnaryOp("\\", StdEnvironment.booleanType, StdEnvironment.booleanType);
		StdEnvironment.andDecl = declareStdBinaryOp("/\\", StdEnvironment.booleanType, StdEnvironment.booleanType, StdEnvironment.booleanType);
		StdEnvironment.orDecl = declareStdBinaryOp("\\/", StdEnvironment.booleanType, StdEnvironment.booleanType, StdEnvironment.booleanType);

		StdEnvironment.integerDecl = declareStdType("Integer", StdEnvironment.integerType);
		StdEnvironment.maxintDecl = declareStdConst("maxint", StdEnvironment.integerType);
		StdEnvironment.addDecl = declareStdBinaryOp("+", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
		StdEnvironment.subtractDecl = declareStdBinaryOp("-", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
		StdEnvironment.multiplyDecl = declareStdBinaryOp("*", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
		StdEnvironment.divideDecl = declareStdBinaryOp("/", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
		StdEnvironment.moduloDecl = declareStdBinaryOp("//", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
		StdEnvironment.lessDecl = declareStdBinaryOp("<", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.booleanType);
		StdEnvironment.notgreaterDecl = declareStdBinaryOp("<=", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.booleanType);
		StdEnvironment.greaterDecl = declareStdBinaryOp(">", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.booleanType);
		StdEnvironment.notlessDecl = declareStdBinaryOp(">=", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.booleanType);
		StdEnvironment.lexicoIncDecl = declareStdBinaryOp("<<", StdEnvironment.stringType, StdEnvironment.stringType, StdEnvironment.booleanType);
		StdEnvironment.lexicoDecDecl = declareStdBinaryOp(">>", StdEnvironment.stringType, StdEnvironment.stringType, StdEnvironment.booleanType);

		StdEnvironment.charDecl = declareStdType("Char", StdEnvironment.charType);
		StdEnvironment.stringDecl = declareStdType("String", StdEnvironment.stringType);
		StdEnvironment.chrDecl = declareStdFunc("chr", new SingleFormalParameterSequence(
				new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos), StdEnvironment.charType);
		StdEnvironment.ordDecl = declareStdFunc("ord", new SingleFormalParameterSequence(
				new ConstFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos), StdEnvironment.integerType);
		StdEnvironment.eofDecl = declareStdFunc("eof", new EmptyFormalParameterSequence(dummyPos), StdEnvironment.booleanType);
		StdEnvironment.eolDecl = declareStdFunc("eol", new EmptyFormalParameterSequence(dummyPos), StdEnvironment.booleanType);
		StdEnvironment.getDecl = declareStdProc("get", new SingleFormalParameterSequence(
				new VarFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos));
		StdEnvironment.putDecl = declareStdProc("put", new SingleFormalParameterSequence(
				new ConstFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos));
		StdEnvironment.putsDecl = declareStdProc("puts", new SingleFormalParameterSequence(
				new ConstFormalParameter(dummyI, StdEnvironment.stringType, dummyPos), dummyPos));
		StdEnvironment.getintDecl = declareStdProc("getint", new SingleFormalParameterSequence(
				new VarFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
		StdEnvironment.putintDecl = declareStdProc("putint", new SingleFormalParameterSequence(
				new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
		StdEnvironment.geteolDecl = declareStdProc("geteol", new EmptyFormalParameterSequence(dummyPos));
		StdEnvironment.puteolDecl = declareStdProc("puteol", new EmptyFormalParameterSequence(dummyPos));
		StdEnvironment.haltDecl = declareStdFunc("halt", new EmptyFormalParameterSequence(dummyPos), StdEnvironment.integerType);
		StdEnvironment.equalDecl = declareStdBinaryOp("=", StdEnvironment.anyType, StdEnvironment.anyType, StdEnvironment.booleanType);
		StdEnvironment.unequalDecl = declareStdBinaryOp("\\=", StdEnvironment.anyType, StdEnvironment.anyType, StdEnvironment.booleanType);

	}


}
