/*
 * @(#)Encoder.java                        2.1 2003/10/07
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

package Triangle.CodeGenerator;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.TableView.TableRow;

import TAM.Instruction;
import TAM.Machine;
import TAM.ObjectFileHeader;
import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.*;

public final class Encoder implements Visitor {



	// Commands
	public Object visitAssignCommand(AssignCommand ast, Object o) {
		Frame frame = (Frame) o;
		Integer valSize = (Integer) ast.E.visit(this, frame);
		encodeStore(ast.V, new Frame (frame, valSize.intValue()),
				valSize.intValue());
		return null;
	}

	public Object visitCallCommand(CallCommand ast, Object o) {
		Frame frame = (Frame) o;
		Integer argsSize = (Integer) ast.APS.visit(this, frame);
		if(ast.I.spelling.equals("puts")){
			SingleActualParameterSequence saps = ((SingleActualParameterSequence) ast.APS);
			ConstActualParameter cap = ((ConstActualParameter) saps.AP);
			if(cap.E instanceof StringExpression){
				StringExpression se = ((StringExpression) cap.E);
				int i = 0;
				while(i < se.SL.spelling.length()){
					emit(Machine.LOADLop, 1, 0, (int) se.SL.spelling.charAt(i++), ast.getPosition().start);
				}
				ast.I.size = se.SL.spelling.length();
			}
			else if (cap.E instanceof VnameExpression){
				VnameExpression se = ((VnameExpression) cap.E);
				//encodeFetch(se.V, frame, se.V.len);
				ast.I.size = se.V.len;
			}
		}
		ast.I.visit(this, new Frame(frame.level, argsSize));
		return null;
	}


	public Object visitCaseCommand(CaseCommand ast, Object o) {
		Frame frame = (Frame) o;
		// space for boolean of did we use a case.	
		emit(Machine.PUSHop, 0, 0, 1, ast.getPosition().start);
		for(IntegerLiteral IL : ast.MAP.keySet()){
			ast.E.visit(this, frame);
			emit(Machine.LOADLop, 0, 0, Integer.parseInt(IL.spelling), ast.getPosition().start);
			emit(Machine.LOADLop, 0,0,1, ast.getPosition().start);
			emit(Machine.CALLop, Machine.LBr, Machine.PBr, Machine.eqDisplacement, ast.getPosition().start);
			int jumpAddr = nextInstrAddr;
			emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, jumpAddr, ast.getPosition().start);
			ast.MAP.get(IL).visit(this, frame);
			emit(Machine.LOADLop, 0, 0, 1, ast.getPosition().start);
			emit(Machine.STOREop, 1, Machine.STr, -2, ast.getPosition().start);
			patch(jumpAddr, nextInstrAddr);
		}
		emit(Machine.LOADop, 1, Machine.STr, -1, ast.getPosition().start);
		int jumpEndAddr = nextInstrAddr;
		emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, jumpEndAddr, ast.getPosition().start);
		ast.C.visit(this, frame);
		patch(jumpEndAddr, nextInstrAddr);
		emit(Machine.POPop, 0, 0, 1, ast.getPosition().start);
		return null;
	}

	public Object visitEmptyCommand(EmptyCommand ast, Object o) {
		return null;
	}

	public Object visitIfCommand(IfCommand ast, Object o) {
		Frame frame = (Frame) o;
		int jumpifAddr, jumpAddr;
		Integer valSize = (Integer) ast.E.visit(this, frame);
		jumpifAddr = nextInstrAddr;
		emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0, ast.getPosition().start);
		ast.C1.visit(this, frame);
		jumpAddr = nextInstrAddr;
		emit(Machine.JUMPop, 0, Machine.CBr, 0, ast.getPosition().start);
		patch(jumpifAddr, nextInstrAddr);
		ast.C2.visit(this, frame);
		patch(jumpAddr, nextInstrAddr);
		return null;
	}

	public Object visitLetCommand(LetCommand ast, Object o) {
		Frame frame = (Frame) o;
		int extraSize = ((Integer) ast.D.visit(this, frame)).intValue();
		ast.C.visit(this, new Frame(frame, extraSize));
		if (extraSize > 0){
			emit(Machine.POPop, 0, 0, extraSize, ast.getPosition().start);
		}
		return null;
	}

	public Object visitSequentialCommand(SequentialCommand ast, Object o) {
		ast.C1.visit(this, o);
		ast.C2.visit(this, o);
		return null;
	}

	public Object visitWhileCommand(WhileCommand ast, Object o) {
		Frame frame = (Frame) o;
		int jumpAddr, loopAddr;
		jumpAddr = nextInstrAddr;
		emit(Machine.JUMPop, 0, Machine.CBr, 0, ast.getPosition().start);
		loopAddr = nextInstrAddr;
		ast.C.visit(this, frame);
		patch(jumpAddr, nextInstrAddr);
		ast.E.visit(this, frame);
		emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, loopAddr, ast.getPosition().start);
		return null;
	}

	public Object visitRepeatCommand(RepeatCommand ast, Object o) {
		Frame frame = (Frame) o;
		int loopAddr;

		loopAddr = nextInstrAddr;
		ast.C.visit(this, frame);
		ast.E.visit(this, frame);
		emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, loopAddr, ast.getPosition().start);
		return null;
	}

	public Object visitForCommand(ForCommand ast, Object o) {
		Frame frame = (Frame) o;
		int jumpAddr, loopAddr;
		ast.E1.visit(this, frame);
		ast.E2.visit(this, frame);
		jumpAddr = nextInstrAddr;
		jumpAddr = nextInstrAddr;
		emit(Machine.JUMPop, 0, Machine.CBr, jumpAddr, ast.getPosition().start);
		loopAddr = nextInstrAddr;
		emit(Machine.LOADop, 1, Machine.STr, -2, ast.getPosition().start);
		ast.C.visit(this, frame);
		emit(Machine.LOADop, 1, Machine.STr, -2, ast.getPosition().start);
		emit(Machine.CALLop, Machine.LBr, Machine.PBr, Machine.succDisplacement, ast.getPosition().start);
		emit(Machine.STOREop, 1, Machine.STr, -3, ast.getPosition().start);
		patch(jumpAddr, nextInstrAddr);
		emit(Machine.LOADop, 1, Machine.STr, -2, ast.getPosition().start);
		emit(Machine.LOADop, 1, Machine.STr, -2, ast.getPosition().start);
		emit(Machine.CALLop, Machine.LBr, Machine.PBr, Machine.gtDisplacement, ast.getPosition().start);
		emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, loopAddr, ast.getPosition().start);

		return null;
	}

	// Expressions
	public Object visitArrayExpression(ArrayExpression ast, Object o) {
		ast.type.visit(this, null);
		return ast.AA.visit(this, o);
	}

	public Object visitBinaryExpression(BinaryExpression ast, Object o) {
		Frame frame = (Frame) o;
		Integer valSize = (Integer) ast.type.visit(this, null);
		int valSize1 = ((Integer) ast.E1.visit(this, frame)).intValue();
		Frame frame1 = new Frame(frame, valSize1);
		int valSize2 = ((Integer) ast.E2.visit(this, frame1)).intValue();
		Frame frame2 = new Frame(frame.level, valSize1 + valSize2);	
		ast.O.size1 = valSize1;
		if(ast.E2 instanceof StringExpression){
			StringExpression se = ((StringExpression) ast.E2);
			ast.O.size2 = se.SL.spelling.length();
		}
		else if (ast.E2 instanceof VnameExpression){
			VnameExpression se = ((VnameExpression) ast.E2);
			if(se.type instanceof StringTypeDenoter)
				ast.O.size2 = se.V.len;
		}
		if(ast.E1 instanceof StringExpression){
			StringExpression se = ((StringExpression) ast.E1);
			ast.O.size1 = se.SL.spelling.length();
		}
		else if (ast.E1 instanceof VnameExpression){
			VnameExpression se = ((VnameExpression) ast.E1);
			if(se.type instanceof StringTypeDenoter)
				ast.O.size1 = se.V.len;
		}
		ast.O.visit(this, frame2);
		return valSize;
	}

	public Object visitMultExpression(MultExpression ast, Object o) {
		Frame frame = (Frame) o;
		Integer valSize = (Integer) ast.type.visit(this, null);
		int valSize1 = ((Integer) ast.E1.visit(this, frame)).intValue();
		Frame frame1 = new Frame(frame, valSize1);
		int valSize2 = ((Integer) ast.E2.visit(this, frame1)).intValue();
		Frame frame2 = new Frame(frame.level, valSize1 + valSize2);
		ast.MOP.visit(this, frame2);
		return valSize;
	}

	public Object visitCallExpression(CallExpression ast, Object o) {
		Frame frame = (Frame) o;
		Integer valSize = (Integer) ast.type.visit(this, null);
		Integer argsSize = (Integer) ast.APS.visit(this, frame);
		ast.I.visit(this, new Frame(frame.level, argsSize));
		return valSize;
	}

	public Object visitCharacterExpression(CharacterExpression ast,
			Object o) {
		Frame frame = (Frame) o;
		Integer valSize = (Integer) ast.type.visit(this, null);
		emit(Machine.LOADLop, 0, 0, ast.CL.spelling.charAt(1), ast.getPosition().start);
		return valSize;
	}

	public Object visitStringExpression(StringExpression ast, Object o) {
		Integer valSize = (Integer) ast.SL.spelling.length();
		for(int i = 0; i < valSize; i ++){
			emit(Machine.LOADLop, 0, 0, ast.SL.spelling.charAt(i), ast.getPosition().start);
		}
		return valSize;
	}

	public Object visitEmptyExpression(EmptyExpression ast, Object o) {
		return new Integer(0);
	}

	public Object visitIfExpression(IfExpression ast, Object o) {
		Frame frame = (Frame) o;
		Integer valSize;
		int jumpifAddr, jumpAddr;

		ast.type.visit(this, null);
		ast.E1.visit(this, frame);
		jumpifAddr = nextInstrAddr;
		emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0, ast.getPosition().start);
		valSize = (Integer) ast.E2.visit(this, frame);
		jumpAddr = nextInstrAddr;
		emit(Machine.JUMPop, 0, Machine.CBr, 0, ast.getPosition().start);
		patch(jumpifAddr, nextInstrAddr);
		valSize = (Integer) ast.E3.visit(this, frame);
		patch(jumpAddr, nextInstrAddr);
		return valSize;
	}

	public Object visitIntegerExpression(IntegerExpression ast, Object o) {
		Frame frame = (Frame) o;
		Integer valSize = (Integer) ast.type.visit(this, null);
		emit(Machine.LOADLop, 0, 0, Integer.parseInt(ast.IL.spelling), ast.getPosition().start);
		return valSize;
	}

	public Object visitLetExpression(LetExpression ast, Object o) {
		Frame frame = (Frame) o;
		ast.type.visit(this, null);
		int extraSize = ((Integer) ast.D.visit(this, frame)).intValue();
		Frame frame1 = new Frame(frame, extraSize);
		Integer valSize = (Integer) ast.E.visit(this, frame1);
		if (extraSize > 0){
			emit(Machine.POPop, valSize.intValue(), 0, extraSize, ast.getPosition().start);
		}
		return valSize;
	}

	public Object visitRecordExpression(RecordExpression ast, Object o){
		ast.type.visit(this, null);
		return ast.RA.visit(this, o);
	}

	public Object visitUnaryExpression(UnaryExpression ast, Object o) {
		Frame frame = (Frame) o;
		Integer valSize = (Integer) ast.type.visit(this, null);
		ast.E.visit(this, frame);
		ast.O.visit(this, new Frame(frame.level, valSize.intValue()));
		return valSize;
	}

	public Object visitVnameExpression(VnameExpression ast, Object o) {
		Frame frame = (Frame) o;
		Integer valSize = (Integer) ast.type.visit(this, null);
		if(ast.type instanceof StringTypeDenoter)
			valSize = (Integer) ast.V.len;
		encodeFetch(ast.V, frame, valSize.intValue());
		return valSize;
	}


	// Declarations
	public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast,
			Object o){
		return new Integer(0);
	}

	public Object visitAssignDeclaration(AssignDeclaration ast,	Object o) {
		Frame frame = (Frame) o;
		int extraSize;
		if(ast.T instanceof StringTypeDenoter){
			extraSize = ((StringExpression)ast.E).SL.spelling.length();
		}
		else extraSize = ((Integer) ast.T.visit(this, null)).intValue();
		emit(Machine.PUSHop, 0, 0, extraSize, ast.getPosition().start);
		ast.V.entity = new KnownAddress(Machine.addressSize, frame.level, frame.size);
		Integer valSize = (Integer) ast.E.visit(this, frame);
		encodeStore(ast.Vn, new Frame (frame, valSize.intValue()), valSize.intValue());
		return new Integer(extraSize);
	}

	public Object visitConstDeclaration(ConstDeclaration ast, Object o) {
		Frame frame = (Frame) o;
		int extraSize = 0;

		if (ast.E instanceof CharacterExpression) {
			CharacterLiteral CL = ((CharacterExpression) ast.E).CL;
			ast.entity = new KnownValue(Machine.characterSize,
					characterValuation(CL.spelling));
		} else if (ast.E instanceof IntegerExpression) {
			IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
			ast.entity = new KnownValue(Machine.integerSize,
					Integer.parseInt(IL.spelling));
		} else {
			int valSize = ((Integer) ast.E.visit(this, frame)).intValue();
			ast.entity = new UnknownValue(valSize, frame.level, frame.size);
			extraSize = valSize;
		}
		return new Integer(extraSize);
	}

	public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
		Frame frame = (Frame) o;
		int jumpAddr = nextInstrAddr;
		int argsSize = 0, valSize = 0;
		String[] details = new String[3];
		details[0] = ast.I.spelling;
		details[1] = "" + (nextInstrAddr + 1);
		emit(Machine.JUMPop, 0, Machine.CBr, 0, ast.getPosition().start);
		ast.entity = new KnownRoutine(Machine.closureSize, frame.level, nextInstrAddr);
		if (frame.level == Machine.maxRoutineLevel)
			reporter.reportRestriction("can't nest routines more than 7 deep");
		else {
			Frame frame1 = new Frame(frame.level + 1, 0);
			argsSize = ((Integer) ast.FPS.visit(this, frame1)).intValue();
			Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
			valSize = ((Integer) ast.E.visit(this, frame2)).intValue();
		}
		emit(Machine.RETURNop, valSize, 0, argsSize, ast.getPosition().start);
		patch(jumpAddr, nextInstrAddr);
		details[2] = "" + (nextInstrAddr - 1);
		writeTableDetails(details);
		return new Integer(0);
	}

	public Object visitFuncOperDeclaration(FuncOperDeclaration ast, Object o) {
		Frame frame = (Frame) o;
		String[] details = new String[3];
		details[0] = ast.O.spelling;
		details[1] = "" + (nextInstrAddr + 1);
		int jumpAddr = nextInstrAddr;
		int argsSize = 0, valSize = 0;
		emit(Machine.JUMPop, 0, Machine.CBr, 0, ast.getPosition().start);
		ast.entity = new KnownRoutine(Machine.closureSize, frame.level, nextInstrAddr);
		if (frame.level == Machine.maxRoutineLevel)
			reporter.reportRestriction("can't nest routines more than 7 deep");
		else {
			Frame frame1 = new Frame(frame.level + 1, 0);
			argsSize = ((Integer) ast.FPS.visit(this, frame1)).intValue();
			Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
			valSize = ((Integer) ast.E.visit(this, frame2)).intValue();
		}
		emit(Machine.RETURNop, valSize, 0, argsSize, ast.getPosition().start);
		patch(jumpAddr, nextInstrAddr);
		details[2] = "" + (nextInstrAddr - 1);
		writeTableDetails(details);
		return new Integer(0);
	}

	public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
		Frame frame = (Frame) o;
		int jumpAddr = nextInstrAddr;
		String[] details = new String[3];
		details[0] = ast.I.spelling;
		details[1] = "" + (nextInstrAddr + 1);
		int argsSize = 0;
		emit(Machine.JUMPop, 0, Machine.CBr, 0, ast.getPosition().start);
		ast.entity = new KnownRoutine (Machine.closureSize, frame.level,
				nextInstrAddr);
		if (frame.level == Machine.maxRoutineLevel)
			reporter.reportRestriction("can't nest routines so deeply");
		else {
			Frame frame1 = new Frame(frame.level + 1, 0);
			argsSize = ((Integer) ast.FPS.visit(this, frame1)).intValue();
			Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
			ast.C.visit(this, frame2);
		}
		emit(Machine.RETURNop, 0, 0, argsSize, ast.getPosition().start);
		patch(jumpAddr, nextInstrAddr);
		details[2] = "" + (nextInstrAddr - 1);
		writeTableDetails(details);
		return new Integer(0);
	}

	public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o) {
		Frame frame = (Frame) o;
		int extraSize1, extraSize2;

		extraSize1 = ((Integer) ast.D1.visit(this, frame)).intValue();
		Frame frame1 = new Frame (frame, extraSize1);
		extraSize2 = ((Integer) ast.D2.visit(this, frame1)).intValue();
		return new Integer(extraSize1 + extraSize2);
	}

	public Object visitStringDeclaration(StringDeclaration ast, Object o) {
		Frame frame = (Frame) o;
		int extraSize = Integer.parseInt(ast.IL.spelling);
		for(int i = 0; i < extraSize; i++)
		{
			emit(Machine.LOADLop, 1, 0, 0, ast.getPosition().start);
		}
		//		emit(Machine.PUSHop, 0, 0, extraSize);
		ast.entity = new KnownAddress(Machine.addressSize, frame.level, frame.size);
		return new Integer(extraSize);
	}


	public Object visitConstStringDeclaration(ConstStringDeclaration ast, Object o) {
		Frame frame = (Frame) o;
		int extraSize = Integer.parseInt(ast.IL.spelling);
		ast.entity = new KnownAddress(Machine.addressSize, frame.level, frame.size);
		ast.E.visit(this, null);
		return new Integer(extraSize);
	}

	public Object visitTypeDeclaration(TypeDeclaration ast, Object o) {
		// just to ensure the type's representation is decided
		ast.T.visit(this, null);
		return new Integer(0);
	}

	public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast,
			Object o) {
		return new Integer(0);
	}

	public Object visitVarDeclaration(VarDeclaration ast, Object o) {
		Frame frame = (Frame) o;
		int extraSize;

		extraSize = ((Integer) ast.T.visit(this, null)).intValue();
		for(int i = 0; i < extraSize; i++){
			emit(Machine.LOADLop, 1, 0, 0, ast.getPosition().start);
		}
		ast.entity = new KnownAddress(Machine.addressSize, frame.level, frame.size);
		return new Integer(extraSize);
	}


	// Array Aggregates
	public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast,
			Object o) {
		Frame frame = (Frame) o;
		int elemSize = ((Integer) ast.E.visit(this, frame)).intValue();
		Frame frame1 = new Frame(frame, elemSize);
		int arraySize = ((Integer) ast.AA.visit(this, frame1)).intValue();
		return new Integer(elemSize + arraySize);
	}

	public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o) {
		return ast.E.visit(this, o);
	}


	// Record Aggregates
	public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast,
			Object o) {
		Frame frame = (Frame) o;
		int fieldSize = ((Integer) ast.E.visit(this, frame)).intValue();
		Frame frame1 = new Frame (frame, fieldSize);
		int recordSize = ((Integer) ast.RA.visit(this, frame1)).intValue();
		return new Integer(fieldSize + recordSize);
	}

	public Object visitSingleRecordAggregate(SingleRecordAggregate ast,
			Object o) {
		return ast.E.visit(this, o);
	}


	// Formal Parameters
	public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
		Frame frame = (Frame) o;
		int valSize = ((Integer) ast.T.visit(this, null)).intValue();
		ast.entity = new UnknownValue (valSize, frame.level, -frame.size - valSize);
		return new Integer(valSize);
	}

	public Object visitConstStringFormalParameter(ConstStringFormalParameter ast, Object o) {
		Frame frame = (Frame) o;
		int valSize = Integer.parseInt(ast.IL.spelling);
		ast.entity = new UnknownValue (valSize, frame.level, -frame.size - valSize);
		return new Integer(valSize);
	}

	public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o) {
		Frame frame = (Frame) o;
		int argsSize = Machine.closureSize;
		ast.entity = new UnknownRoutine (Machine.closureSize, frame.level,
				-frame.size - argsSize);
		return new Integer(argsSize);
	}

	public Object visitFuncOperFormalParameter(FuncOperFormalParameter ast, Object o) {
		Frame frame = (Frame) o;
		int argsSize = Machine.closureSize;
		ast.entity = new UnknownRoutine (Machine.closureSize, frame.level,
				-frame.size - argsSize);

		return new Integer(argsSize);
	}

	public Object visitProcFormalParameter(ProcFormalParameter ast, Object o) {
		Frame frame = (Frame) o;
		int argsSize = Machine.closureSize;
		ast.entity = new UnknownRoutine (Machine.closureSize, frame.level,
				-frame.size - argsSize);
		return new Integer(argsSize);
	}

	public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
		Frame frame = (Frame) o;
		ast.T.visit(this, null);
		ast.entity = new UnknownAddress (Machine.addressSize, frame.level,
				-frame.size - Machine.addressSize);
		return new Integer(Machine.addressSize);
	}


	public Object visitEmptyFormalParameterSequence(
			EmptyFormalParameterSequence ast, Object o) {
		return new Integer(0);
	}

	public Object visitMultipleFormalParameterSequence(
			MultipleFormalParameterSequence ast, Object o) {
		Frame frame = (Frame) o;
		int argsSize1 = ((Integer) ast.FPS.visit(this, frame)).intValue();
		Frame frame1 = new Frame(frame, argsSize1);
		int argsSize2 = ((Integer) ast.FP.visit(this, frame1)).intValue();
		return new Integer(argsSize1 + argsSize2);
	}

	public Object visitSingleFormalParameterSequence(
			SingleFormalParameterSequence ast, Object o) {
		return ast.FP.visit (this, o);
	}


	// Actual Parameters
	public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
		return ast.E.visit (this, o);
	}

	public Object visitFuncActualParameter(FuncActualParameter ast, Object o) {
		Frame frame = (Frame) o;
		if (ast.I.decl.entity instanceof KnownRoutine) {
			ObjectAddress address = ((KnownRoutine) ast.I.decl.entity).address;
			// static link, code address
			emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0, ast.getPosition().start);
			emit(Machine.LOADAop, 0, Machine.CBr, address.displacement, ast.getPosition().start);
		} else if (ast.I.decl.entity instanceof UnknownRoutine) {
			ObjectAddress address = ((UnknownRoutine) ast.I.decl.entity).address;
			emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
					address.level), address.displacement, ast.getPosition().start);
		} else if (ast.I.decl.entity instanceof PrimitiveRoutine) {
			int displacement = ((PrimitiveRoutine) ast.I.decl.entity).displacement;
			// static link, code address
			emit(Machine.LOADAop, 0, Machine.SBr, 0, ast.getPosition().start);
			emit(Machine.LOADAop, 0, Machine.PBr, displacement, ast.getPosition().start);
		}
		return new Integer(Machine.closureSize);
	}

	public Object visitFuncOperActualParameter(FuncOperActualParameter ast, Object o) {
		Frame frame = (Frame) o;
		if (ast.O.decl.entity instanceof KnownRoutine) {
			ObjectAddress address = ((KnownRoutine) ast.O.decl.entity).address;
			// static link, code address
			emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0, ast.getPosition().start);
			emit(Machine.LOADAop, 0, Machine.CBr, address.displacement, ast.getPosition().start);

		} else if (ast.O.decl.entity instanceof UnknownRoutine) {
			ObjectAddress address = ((UnknownRoutine) ast.O.decl.entity).address;
			emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
					address.level), address.displacement, ast.getPosition().start);
		} else if (ast.O.decl.entity instanceof PrimitiveRoutine) {
			int displacement = ((PrimitiveRoutine) ast.O.decl.entity).displacement;
			// static link, code address
			emit(Machine.LOADAop, 0, Machine.SBr, 0, ast.getPosition().start);
			emit(Machine.LOADAop, 0, Machine.PBr, displacement, ast.getPosition().start);
		}
		else if (ast.O.decl.entity instanceof EqualityRoutine) {
			int displacement = ((EqualityRoutine) ast.O.decl.entity).displacement;
			emit(Machine.LOADAop, 0, Machine.SBr, 0, ast.getPosition().start);
			emit(Machine.LOADAop, 0, Machine.PBr, displacement, ast.getPosition().start);
		}
		return new Integer(Machine.closureSize);
	}


	public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
		Frame frame = (Frame) o;
		if (ast.I.decl.entity instanceof KnownRoutine) {
			ObjectAddress address = ((KnownRoutine) ast.I.decl.entity).address;
			// static link, code address
			emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0, ast.getPosition().start);
			emit(Machine.LOADAop, 0, Machine.CBr, address.displacement, ast.getPosition().start);
		} else if (ast.I.decl.entity instanceof UnknownRoutine) {
			ObjectAddress address = ((UnknownRoutine) ast.I.decl.entity).address;
			emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
					address.level), address.displacement, ast.getPosition().start);
		} else if (ast.I.decl.entity instanceof PrimitiveRoutine) {
			int displacement = ((PrimitiveRoutine) ast.I.decl.entity).displacement;
			// static link, code address
			emit(Machine.LOADAop, 0, Machine.SBr, 0, ast.getPosition().start);
			emit(Machine.LOADAop, 0, Machine.PBr, displacement, ast.getPosition().start);
		}
		return new Integer(Machine.closureSize);
	}

	public Object visitVarActualParameter(VarActualParameter ast, Object o) {
		encodeFetchAddress(ast.V, (Frame) o);
		return new Integer(Machine.addressSize);
	}


	public Object visitEmptyActualParameterSequence(
			EmptyActualParameterSequence ast, Object o) {
		return new Integer(0);
	}

	public Object visitMultipleActualParameterSequence(
			MultipleActualParameterSequence ast, Object o) {
		Frame frame = (Frame) o;
		int argsSize1 = ((Integer) ast.AP.visit(this, frame)).intValue();
		Frame frame1 = new Frame (frame, argsSize1);
		int argsSize2 = ((Integer) ast.APS.visit(this, frame1)).intValue();
		return new Integer(argsSize1 + argsSize2);
	}

	public Object visitSingleActualParameterSequence(
			SingleActualParameterSequence ast, Object o) {
		return ast.AP.visit (this, o);
	}


	// Type Denoters
	public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o) {
		return new Integer(0);
	}

	public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o) {
		int typeSize;
		if (ast.entity == null) {
			int elemSize = ((Integer) ast.T.visit(this, null)).intValue();
			typeSize = Integer.parseInt(ast.IL.spelling) * elemSize;
			ast.entity = new TypeRepresentation(typeSize);
		} else
			typeSize = ast.entity.size;
		return new Integer(typeSize);
	}

	public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o) {
		if (ast.entity == null) {
			ast.entity = new TypeRepresentation(Machine.booleanSize);
		}
		return new Integer(Machine.booleanSize);
	}

	public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o) {
		if (ast.entity == null) {
			ast.entity = new TypeRepresentation(Machine.characterSize);
		}
		return new Integer(Machine.characterSize);
	}

	public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o) {
		return new Integer(0);
	}

	public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast,
			Object o) {
		return new Integer(0);
	}

	public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o) {
		if (ast.entity == null) {
			ast.entity = new TypeRepresentation(Machine.integerSize);
		}
		return new Integer(Machine.integerSize);
	}

	public Object visitStringTypeDenoter(StringTypeDenoter ast,	Object o) {
		if (ast.entity == null) {
			ast.entity = new TypeRepresentation(Integer.parseInt(ast.IL.spelling));
		}
		return new Integer(Integer.parseInt(ast.IL.spelling));
	}

	public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o) {
		int typeSize;
		if (ast.entity == null) {
			typeSize = ((Integer) ast.FT.visit(this, new Integer(0))).intValue();
			ast.entity = new TypeRepresentation(typeSize);
		} else
			typeSize = ast.entity.size;
		return new Integer(typeSize);
	}


	public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast,
			Object o) {
		int offset = ((Integer) o).intValue();
		int fieldSize;

		if (ast.entity == null) {
			fieldSize = ((Integer) ast.T.visit(this, null)).intValue();
			ast.entity = new Field (fieldSize, offset);
		} else
			fieldSize = ast.entity.size;

		Integer offset1 = new Integer(offset + fieldSize);
		int recSize = ((Integer) ast.FT.visit(this, offset1)).intValue();
		return new Integer(fieldSize + recSize);
	}

	public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast,
			Object o) {
		int offset = ((Integer) o).intValue();
		int fieldSize;

		if (ast.entity == null) {
			fieldSize = ((Integer) ast.T.visit(this, null)).intValue();
			ast.entity = new Field (fieldSize, offset);
		} else
			fieldSize = ast.entity.size;

		return new Integer(fieldSize);
	}


	// Literals, Identifiers and Operators
	public Object visitCharacterLiteral(CharacterLiteral ast, Object o) {
		return null;
	}

	public Object visitStringLiteral(StringLiteral stringLiteral, Object o) {
		return null;
	}

	public Object visitIdentifier(Identifier ast, Object o) {
		Frame frame = (Frame) o;
		if(ast.spelling.equals("halt")){
			emit(Machine.CALLop, 0, Machine.PBr, Machine.haltDisplacement, ast.getPosition().start);
		}
		else if (ast.decl.entity instanceof KnownRoutine) {
			ObjectAddress address = ((KnownRoutine) ast.decl.entity).address;
			emit(Machine.CALLop, displayRegister(frame.level, address.level),
					Machine.CBr, address.displacement, ast.getPosition().start);
		} else if (ast.decl.entity instanceof UnknownRoutine) {
			ObjectAddress address = ((UnknownRoutine) ast.decl.entity).address;
			emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
					address.level), address.displacement, ast.getPosition().start);
			emit(Machine.CALLIop, 0, 0, 0, ast.getPosition().start);
		} else if (ast.decl.entity instanceof PrimitiveRoutine) {
			int displacement = ((PrimitiveRoutine) ast.decl.entity).displacement;
			if (displacement != Machine.idDisplacement){
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement, ast.getPosition().start);
			}
		} else if (ast.decl.entity instanceof EqualityRoutine) { // "=" or "\="
			int displacement = ((EqualityRoutine) ast.decl.entity).displacement;
			if(ast.spelling.equals("puts")){
				emit(Machine.LOADLop, 0, 0, ast.size, ast.getPosition().start);
			}
			else {
				emit(Machine.LOADLop, 0, 0, frame.size / 2, ast.getPosition().start);
			}
			emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement, ast.getPosition().start);
		}
		return null;
	}

	public Object visitIntegerLiteral(IntegerLiteral ast, Object o) {
		return null;
	}

	public Object visitOperator(Operator ast, Object o) {
		Frame frame = (Frame) o;
		if (ast.decl.entity instanceof KnownRoutine) {
			ObjectAddress address = ((KnownRoutine) ast.decl.entity).address;
			emit(Machine.CALLop, displayRegister (frame.level, address.level),
					Machine.CBr, address.displacement, ast.getPosition().start);
		} else if (ast.decl.entity instanceof UnknownRoutine) {
			ObjectAddress address = ((UnknownRoutine) ast.decl.entity).address;
			emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
					address.level), address.displacement, ast.getPosition().start);
			emit(Machine.LOADLop, 1, 0, 1041, ast.getPosition().start);
			emit(Machine.LOADLop, 1, 0, 1, ast.getPosition().start);
			emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.eqDisplacement, ast.getPosition().start);
			int jumpAddr, jumpIfAddr, jumpAddr2, jumpIfAddr2;
			jumpIfAddr = nextInstrAddr;
			emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, jumpIfAddr, ast.getPosition().start);
			emit(Machine.LOADLop, 1, 0, ast.size1, ast.getPosition().start);
			emit(Machine.POPop, 1, 0, 1, ast.getPosition().start);
			jumpAddr = nextInstrAddr;
			emit(Machine.JUMPop, 0, Machine.CBr, jumpAddr, ast.getPosition().start);
			patch(jumpIfAddr, nextInstrAddr);
			emit(Machine.POPop, 0, 0, 1, ast.getPosition().start);
			patch(jumpAddr, nextInstrAddr);
			emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
					address.level), address.displacement, ast.getPosition().start);
			emit(Machine.LOADLop, 1, 0, 1042, ast.getPosition().start);
			emit(Machine.LOADLop, 1, 0, 1, ast.getPosition().start);
			emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.eqDisplacement, ast.getPosition().start);
			jumpIfAddr2 = nextInstrAddr;
			emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, jumpIfAddr2, ast.getPosition().start);
			emit(Machine.LOADLop, 1, 0, ast.size1, ast.getPosition().start);
			emit(Machine.POPop, 1, 0, 1, ast.getPosition().start);
			jumpAddr2 = nextInstrAddr;
			emit(Machine.JUMPop, 0, Machine.CBr, jumpAddr2, ast.getPosition().start);
			patch(jumpIfAddr2, nextInstrAddr);
			emit(Machine.POPop, 0, 0, 1, ast.getPosition().start);
			patch(jumpAddr2, nextInstrAddr);
			emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
					address.level), address.displacement, ast.getPosition().start);
			emit(Machine.CALLIop, 0, 0, 0, ast.getPosition().start);			
		} else if (ast.decl.entity instanceof PrimitiveRoutine) {
			int displacement = ((PrimitiveRoutine) ast.decl.entity).displacement;
			if (displacement != Machine.idDisplacement){
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement, ast.getPosition().start);
			}
		} else if (ast.decl.entity instanceof EqualityRoutine) { // "=" or "\="
			int displacement = ((EqualityRoutine) ast.decl.entity).displacement;
			emit(Machine.LOADLop, 0, 0, ast.size1, ast.getPosition().start);
			emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement, ast.getPosition().start);
		} else if (ast.decl.entity instanceof DoubleStringRoutine) { // "<<" or ">>"
			int displacement = ((DoubleStringRoutine) ast.decl.entity).displacement;
			emit(Machine.LOADLop, 0, 0, ast.size2, ast.getPosition().start);
			emit(Machine.LOADLop, 0, 0, ast.size1, ast.getPosition().start);
			emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement, ast.getPosition().start);
		}
		return null;
	}

	public Object visitMultOperator(MultOperator ast, Object o) {
		Frame frame = (Frame) o;
		if (ast.decl.entity instanceof KnownRoutine) {
			ObjectAddress address = ((KnownRoutine) ast.decl.entity).address;
			emit(Machine.CALLop, displayRegister (frame.level, address.level),
					Machine.CBr, address.displacement, ast.getPosition().start);
		} else if (ast.decl.entity instanceof UnknownRoutine) {
			ObjectAddress address = ((UnknownRoutine) ast.decl.entity).address;
			emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
					address.level), address.displacement, ast.getPosition().start);
			emit(Machine.CALLIop, 0, 0, 0, ast.getPosition().start);
		} else if (ast.decl.entity instanceof PrimitiveRoutine) {
			int displacement = ((PrimitiveRoutine) ast.decl.entity).displacement;
			if (displacement != Machine.idDisplacement){
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement, ast.getPosition().start);
			}
		} else if (ast.decl.entity instanceof EqualityRoutine) { // "=" or "\="
			int displacement = ((EqualityRoutine) ast.decl.entity).displacement;
			emit(Machine.LOADLop, 0, 0, frame.size / 2, ast.getPosition().start);
			emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement, ast.getPosition().start);
		}
		return null;
	}


	// Value-or-variable names
	public Object visitDotVname(DotVname ast, Object o) {
		Frame frame = (Frame) o;
		RuntimeEntity baseObject = (RuntimeEntity) ast.V.visit(this, frame);
		ast.offset = ast.V.offset + ((Field) ast.I.decl.entity).fieldOffset;
		// I.decl points to the appropriate record field
		ast.indexed = ast.V.indexed;
		return baseObject;
	}

	public Object visitSimpleVname(SimpleVname ast, Object o) {
		ast.offset = 0;
		ast.indexed = false;
		return ast.I.decl.entity;
	}

	public Object visitSubscriptVname(SubscriptVname ast, Object o) {
		Frame frame = (Frame) o;
		RuntimeEntity baseObject;
		int elemSize, indexSize;

		baseObject = (RuntimeEntity) ast.V.visit(this, frame);
		ast.offset = ast.V.offset;
		ast.indexed = ast.V.indexed;
		elemSize = ((Integer) ast.type.visit(this, null)).intValue();
		if (ast.E instanceof IntegerExpression) {
			IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
			ast.offset = ast.offset + Integer.parseInt(IL.spelling) * elemSize;
		} else {
			if(ast.V.type instanceof StringTypeDenoter){
				indexSize = ast.V.len;
			}
			else{
				ArrayTypeDenoter ATD = (ArrayTypeDenoter)ast.V.type;
				indexSize = Integer.parseInt(ATD.IL.spelling);
			}
			// v-name is indexed by a proper expression, not a literal
			if (ast.indexed)
				frame.size = frame.size + Machine.integerSize;
			int indexSize2 = ((Integer) ast.E.visit(this, frame)).intValue();
			emit(Machine.LOADLop, 0, 0, indexSize, ast.getPosition().start);
			emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.arrayCheckDisplacement, ast.getPosition().start);
			if (elemSize != 1) {
				emit(Machine.LOADLop, 0, 0, elemSize, ast.getPosition().start);
				emit(Machine.CALLop, Machine.SBr, Machine.PBr,
						Machine.multDisplacement, ast.getPosition().start);
			}
			if (ast.indexed)
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, ast.getPosition().start);
			else
				ast.indexed = true;
		}
		return baseObject;
	}


	// Programs
	public Object visitProgram(Program ast, Object o) {
		Object ret = ast.C.visit(this, o);
		return ret;
	}

	public Encoder (ErrorReporter reporter) {
		this.reporter = reporter;
		nextInstrAddr = Machine.CB;
		elaborateStdEnvironment();
	}

	private ErrorReporter reporter;

	// Generates code to run a program.
	// showingTable is true iff entity description details
	// are to be displayed.
	public final void encodeRun (Program theAST, boolean showingTable) {
		lineMap.ensureCapacity(1);
		tableDetailsReqd = showingTable;
		//startCodeGeneration();
		theAST.visit(this, new Frame (0, 0));
		emit(Machine.HALTop, 0, 0, 0, theAST.getPosition().start);
	}

	// Decides run-time representation of a standard constant.
	private final void elaborateStdConst (Declaration constDeclaration,
			int value) {

		if (constDeclaration instanceof ConstDeclaration) {
			ConstDeclaration decl = (ConstDeclaration) constDeclaration;
			int typeSize = ((Integer) decl.E.type.visit(this, null)).intValue();
			decl.entity = new KnownValue(typeSize, value);
		}
	}

	// Decides run-time representation of a standard routine.

	private void elaborateStdStrRoutine(BinaryOperatorDeclaration decl, int disp) {
		decl.entity = new DoubleStringRoutine (Machine.closureSize, disp);
	}

	private final void elaborateStdPrimRoutine (Declaration routineDeclaration,
			int routineOffset) {
		routineDeclaration.entity = new PrimitiveRoutine (Machine.closureSize, routineOffset);
	}

	private final void elaborateStdEqRoutine (Declaration routineDeclaration,
			int routineOffset) {
		routineDeclaration.entity = new EqualityRoutine (Machine.closureSize, routineOffset);
	}

	private final void elaborateStdRoutine (Declaration routineDeclaration,
			int routineOffset) {
		routineDeclaration.entity = new KnownRoutine (Machine.closureSize, 0, routineOffset);
	}

	private final void elaborateStdEnvironment() {
		tableDetailsReqd = false;
		elaborateStdConst(StdEnvironment.falseDecl, Machine.falseRep);
		elaborateStdConst(StdEnvironment.trueDecl, Machine.trueRep);
		elaborateStdPrimRoutine(StdEnvironment.haltDecl, Machine.haltDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.notDecl, Machine.notDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.andDecl, Machine.andDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.orDecl, Machine.orDisplacement);
		elaborateStdConst(StdEnvironment.maxintDecl, Machine.maxintRep);
		elaborateStdPrimRoutine(StdEnvironment.addDecl, Machine.addDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.subtractDecl, Machine.subDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.multiplyDecl, Machine.multDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.divideDecl, Machine.divDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.moduloDecl, Machine.modDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.lessDecl, Machine.ltDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.notgreaterDecl, Machine.leDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.greaterDecl, Machine.gtDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.notlessDecl, Machine.geDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.chrDecl, Machine.idDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.ordDecl, Machine.idDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.eolDecl, Machine.eolDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.eofDecl, Machine.eofDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.getDecl, Machine.getDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.putDecl, Machine.putDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.getintDecl, Machine.getintDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.putintDecl, Machine.putintDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.geteolDecl, Machine.geteolDisplacement);
		elaborateStdPrimRoutine(StdEnvironment.puteolDecl, Machine.puteolDisplacement);
		elaborateStdEqRoutine(StdEnvironment.equalDecl, Machine.eqDisplacement);
		elaborateStdEqRoutine(StdEnvironment.unequalDecl, Machine.neDisplacement);
		elaborateStdEqRoutine(StdEnvironment.putsDecl, Machine.putsDisplacement);
		elaborateStdStrRoutine(StdEnvironment.lexicoIncDecl, Machine.lexicoIncDisplacement);
		elaborateStdStrRoutine(StdEnvironment.lexicoDecDecl, Machine.lexicoDecDisplacement);

	}

	// Saves the object program in the named file.

	public void saveObjectProgram(String objectName, String fileName) {
		FileOutputStream objectFile = null;
		DataOutputStream objectStream = null;
		ObjectFileHeader header = new ObjectFileHeader(fileName, nextInstrAddr - Machine.CB, tableRecord);

		int addr;

		try {
			objectFile = new FileOutputStream (objectName);
			objectStream = new DataOutputStream (objectFile);
			header.write(objectStream);
			addr = Machine.CB;
			for (addr = Machine.CB; addr < nextInstrAddr; addr++)
				Machine.code[addr].write(objectStream);
			for(int integer : lineMap){
				objectStream.writeInt(integer);
			}
			for(String[] stringArray : tableDetails){
				for(String string : stringArray){
					objectStream.writeInt(string.length());
					objectStream.writeChars(string);
				}
			}
			objectFile.close();
		} catch (FileNotFoundException s) {
			System.err.println ("Error opening object file: " + s);
		} catch (IOException s) {
			System.err.println ("Error writing object file: " + s);
		}
	}

	boolean tableDetailsReqd;
	static ArrayList<String[]> tableDetails = new ArrayList<String[]>();
	static ArrayList<Integer> lineMap = new ArrayList<Integer>();
	static int tableRecord;
	static int lineRecord;

	public static void writeTableDetails(String[] details) {
		tableDetails.add(details);
		tableRecord++;
		//= details;
	}

	public static void writeTableDetails(int instructionNumber, int lineNumber) {
		lineMap.add(instructionNumber, lineNumber);
	}

	// OBJECT CODE

	// Implementation notes:
	// Object code is generated directly into the TAM Code Store, starting at CB.
	// The address of the next instruction is held in nextInstrAddr.

	private int nextInstrAddr;

	// Appends an instruction, with the given fields, to the object code.
	private void emit (int op, int n, int r, int d, int lineNumber) {
		Instruction nextInstr = new Instruction();
		if (n > 255) {
			reporter.reportRestriction("length of operand can't exceed 255 words");
			n = 255; // to allow code generation to continue
		}
		nextInstr.op = op;
		nextInstr.n = n;
		nextInstr.r = r;
		nextInstr.d = d;
		if (nextInstrAddr == Machine.PB)
			reporter.reportRestriction("too many instructions for code segment");
		else {
			writeTableDetails(nextInstrAddr, lineNumber);
			Machine.code[nextInstrAddr] = nextInstr;
			nextInstrAddr = nextInstrAddr + 1;
		}
	}

	// Patches the d-field of the instruction at address addr.
	private void patch (int addr, int d) {
		Machine.code[addr].d = d;
	}

	// DATA REPRESENTATION

	public int characterValuation (String spelling) {
		// Returns the machine representation of the given character literal.
		return spelling.charAt(1);
		// since the character literal is of the form 'x'}
	}

	// REGISTERS

	// Returns the register number appropriate for object code at currentLevel
	// to address a data object at objectLevel.
	private int displayRegister (int currentLevel, int objectLevel) {
		if (objectLevel == 0)
			return Machine.SBr;
		else if (currentLevel - objectLevel <= 6)
			return Machine.LBr + currentLevel - objectLevel; // LBr|L1r|...|L6r
		else {
			reporter.reportRestriction("can't access data more than 6 levels out");
			return Machine.L6r;  // to allow code generation to continue
		}
	}

	// Generates code to fetch the value of a named constant or variable
	// and push it on to the stack.
	// currentLevel is the routine level where the vname occurs.
	// frameSize is the anticipated size of the local stack frame when
	// the constant or variable is fetched at run-time.
	// valSize is the size of the constant or variable's value.

	private void encodeStore(Vname V, Frame frame, int valSize) {

		RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
		// If indexed = true, code will have been generated to load an index value.
		if (valSize > 255) {
			reporter.reportRestriction("can't store values larger than 255 words");
			valSize = 255; // to allow code generation to continue
		}
		if (baseObject instanceof KnownAddress) {
			ObjectAddress address = ((KnownAddress) baseObject).address;
			if (V.indexed) {
				emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
						address.displacement + V.offset, V.getPosition().start);
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
				emit(Machine.STOREIop, valSize, 0, 0, V.getPosition().start);
			} else {
				emit(Machine.STOREop, valSize, displayRegister(frame.level,
						address.level), address.displacement + V.offset, V.getPosition().start);
			}
		} else if (baseObject instanceof UnknownAddress) {
			ObjectAddress address = ((UnknownAddress) baseObject).address;
			emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level,
					address.level), address.displacement, V.getPosition().start);
			if (V.indexed)
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
			if (V.offset != 0) {
				emit(Machine.LOADLop, 0, 0, V.offset, V.getPosition().start);
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
			}
			emit(Machine.STOREIop, valSize, 0, 0, V.getPosition().start);
		}
	}

	// Generates code to fetch the value of a named constant or variable
	// and push it on to the stack.
	// currentLevel is the routine level where the vname occurs.
	// frameSize is the anticipated size of the local stack frame when
	// the constant or variable is fetched at run-time.
	// valSize is the size of the constant or variable's value.

	private void encodeFetch(Vname V, Frame frame, int valSize) {
		RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
		// If indexed = true, code will have been generated to load an index value.
		if (valSize > 255) {
			reporter.reportRestriction("can't load values larger than 255 words");
			valSize = 255; // to allow code generation to continue
		}
		if (baseObject instanceof KnownValue) {
			// presumably offset = 0 and indexed = false
			int value = ((KnownValue) baseObject).value;
			emit(Machine.LOADLop, 0, 0, value, V.getPosition().start);
		} else if ((baseObject instanceof UnknownValue) ||
				(baseObject instanceof KnownAddress)) {
			ObjectAddress address = (baseObject instanceof UnknownValue) ?
					((UnknownValue) baseObject).address :
						((KnownAddress) baseObject).address;
					if (V.indexed) {
						emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
								address.displacement + V.offset, V.getPosition().start);
						emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
						emit(Machine.LOADIop, valSize, 0, 0, V.getPosition().start);
					} else{
						emit(Machine.LOADop, valSize, displayRegister(frame.level,
								address.level), address.displacement + V.offset, V.getPosition().start);
					}
		} else if (baseObject instanceof UnknownAddress) {
			ObjectAddress address = ((UnknownAddress) baseObject).address;
			emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level,
					address.level), address.displacement, V.getPosition().start);
			if (V.indexed)
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
			if (V.offset != 0) {
				emit(Machine.LOADLop, 0, 0, V.offset, V.getPosition().start);
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
			}
			emit(Machine.LOADIop, valSize, 0, 0, V.getPosition().start);
		}
	}

	// Generates code to compute and push the address of a named variable.
	// vname is the program phrase that names this variable.
	// currentLevel is the routine level where the vname occurs.
	// frameSize is the anticipated size of the local stack frame when
	// the variable is addressed at run-time.

	private void encodeFetchAddress (Vname V, Frame frame) {

		RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
		// If indexed = true, code will have been generated to load an index value.
		if (baseObject instanceof KnownAddress) {
			ObjectAddress address = ((KnownAddress) baseObject).address;
			emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
					address.displacement + V.offset, V.getPosition().start);
			if (V.indexed)
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
		} else if (baseObject instanceof UnknownAddress) {
			ObjectAddress address = ((UnknownAddress) baseObject).address;
			emit(Machine.LOADop, Machine.addressSize,displayRegister(frame.level,
					address.level), address.displacement, V.getPosition().start);
			if (V.indexed)
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
			if (V.offset != 0) {
				emit(Machine.LOADLop, 0, 0, V.offset, V.getPosition().start);
				emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement, V.getPosition().start);
			}
		}
	}
}
