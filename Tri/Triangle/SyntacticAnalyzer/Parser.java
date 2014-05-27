/*
 * @(#)Parser.java                        2.1 2003/10/07
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

package Triangle.SyntacticAnalyzer;

import java.util.LinkedHashMap;

import Triangle.ErrorReporter;
import Triangle.AbstractSyntaxTrees.ActualParameter;
import Triangle.AbstractSyntaxTrees.ActualParameterSequence;
import Triangle.AbstractSyntaxTrees.AddOperator;
import Triangle.AbstractSyntaxTrees.ArrayAggregate;
import Triangle.AbstractSyntaxTrees.ArrayExpression;
import Triangle.AbstractSyntaxTrees.ArrayTypeDenoter;
import Triangle.AbstractSyntaxTrees.AssignCommand;
import Triangle.AbstractSyntaxTrees.AssignDeclaration;
import Triangle.AbstractSyntaxTrees.BinaryExpression;
import Triangle.AbstractSyntaxTrees.CallCommand;
import Triangle.AbstractSyntaxTrees.CallExpression;
import Triangle.AbstractSyntaxTrees.CaseCommand;
import Triangle.AbstractSyntaxTrees.CharacterExpression;
import Triangle.AbstractSyntaxTrees.CharacterLiteral;
import Triangle.AbstractSyntaxTrees.Command;
import Triangle.AbstractSyntaxTrees.ConstActualParameter;
import Triangle.AbstractSyntaxTrees.ConstDeclaration;
import Triangle.AbstractSyntaxTrees.ConstFormalParameter;
import Triangle.AbstractSyntaxTrees.ConstStringDeclaration;
import Triangle.AbstractSyntaxTrees.ConstStringFormalParameter;
import Triangle.AbstractSyntaxTrees.Declaration;
import Triangle.AbstractSyntaxTrees.DotVname;
import Triangle.AbstractSyntaxTrees.EmptyActualParameterSequence;
import Triangle.AbstractSyntaxTrees.EmptyCommand;
import Triangle.AbstractSyntaxTrees.EmptyFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.Expression;
import Triangle.AbstractSyntaxTrees.FieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.ForCommand;
import Triangle.AbstractSyntaxTrees.FormalParameter;
import Triangle.AbstractSyntaxTrees.FormalParameterSequence;
import Triangle.AbstractSyntaxTrees.FuncActualParameter;
import Triangle.AbstractSyntaxTrees.FuncDeclaration;
import Triangle.AbstractSyntaxTrees.FuncFormalParameter;
import Triangle.AbstractSyntaxTrees.FuncOperActualParameter;
import Triangle.AbstractSyntaxTrees.FuncOperDeclaration;
import Triangle.AbstractSyntaxTrees.FuncOperFormalParameter;
import Triangle.AbstractSyntaxTrees.Identifier;
import Triangle.AbstractSyntaxTrees.IfCommand;
import Triangle.AbstractSyntaxTrees.IfExpression;
import Triangle.AbstractSyntaxTrees.IntegerExpression;
import Triangle.AbstractSyntaxTrees.IntegerLiteral;
import Triangle.AbstractSyntaxTrees.LetCommand;
import Triangle.AbstractSyntaxTrees.LetExpression;
import Triangle.AbstractSyntaxTrees.MultExpression;
import Triangle.AbstractSyntaxTrees.MultOperator;
import Triangle.AbstractSyntaxTrees.MultipleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleArrayAggregate;
import Triangle.AbstractSyntaxTrees.MultipleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.MultipleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleRecordAggregate;
import Triangle.AbstractSyntaxTrees.Operator;
import Triangle.AbstractSyntaxTrees.ProcActualParameter;
import Triangle.AbstractSyntaxTrees.ProcDeclaration;
import Triangle.AbstractSyntaxTrees.ProcFormalParameter;
import Triangle.AbstractSyntaxTrees.Program;
import Triangle.AbstractSyntaxTrees.RecordAggregate;
import Triangle.AbstractSyntaxTrees.RecordExpression;
import Triangle.AbstractSyntaxTrees.RecordTypeDenoter;
import Triangle.AbstractSyntaxTrees.RepeatCommand;
import Triangle.AbstractSyntaxTrees.SequentialCommand;
import Triangle.AbstractSyntaxTrees.SequentialDeclaration;
import Triangle.AbstractSyntaxTrees.SimpleTypeDenoter;
import Triangle.AbstractSyntaxTrees.SimpleVname;
import Triangle.AbstractSyntaxTrees.SingleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleArrayAggregate;
import Triangle.AbstractSyntaxTrees.SingleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.SingleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleRecordAggregate;
import Triangle.AbstractSyntaxTrees.StringDeclaration;
import Triangle.AbstractSyntaxTrees.StringExpression;
import Triangle.AbstractSyntaxTrees.StringLiteral;
import Triangle.AbstractSyntaxTrees.StringTypeDenoter;
import Triangle.AbstractSyntaxTrees.SubscriptVname;
import Triangle.AbstractSyntaxTrees.TypeDeclaration;
import Triangle.AbstractSyntaxTrees.TypeDenoter;
import Triangle.AbstractSyntaxTrees.UnaryExpression;
import Triangle.AbstractSyntaxTrees.VarActualParameter;
import Triangle.AbstractSyntaxTrees.VarDeclaration;
import Triangle.AbstractSyntaxTrees.VarFormalParameter;
import Triangle.AbstractSyntaxTrees.Vname;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;

public class Parser {

	private Scanner lexicalAnalyser;
	private ErrorReporter errorReporter;
	private Token currentToken;
	private SourcePosition previousTokenPosition;

	public Parser(Scanner lexer, ErrorReporter reporter) {
		lexicalAnalyser = lexer;
		errorReporter = reporter;
		previousTokenPosition = new SourcePosition();
	}

	// accept checks whether the current token matches tokenExpected.
	// If so, fetches the next token.
	// If not, reports a syntactic error.

	void accept (int tokenExpected) throws SyntaxError {
		if (currentToken.kind == tokenExpected) {
			previousTokenPosition = currentToken.position;
			currentToken = lexicalAnalyser.scan();
		} else {
			syntacticError("\"%\" expected here", Token.spell(tokenExpected));
		}
	}

	void acceptIt() {
		previousTokenPosition = currentToken.position;
		currentToken = lexicalAnalyser.scan();
	}

	// start records the position of the start of a phrase.
	// This is defined to be the position of the first
	// character of the first token of the phrase.

	void start(SourcePosition position) {
		position.start = currentToken.position.start;
	}

	// finish records the position of the end of a phrase.
	// This is defined to be the position of the last
	// character of the last token of the phrase.

	void finish(SourcePosition position) {
		position.finish = previousTokenPosition.finish;
	}

	void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
		SourcePosition pos = currentToken.position;
		errorReporter.reportError(messageTemplate, tokenQuoted, pos);
		throw(new SyntaxError());
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// PROGRAMS
	//
	///////////////////////////////////////////////////////////////////////////////

	public Program parseProgram() {

		Program programAST = null;

		previousTokenPosition.start = 0;
		previousTokenPosition.finish = 0;
		currentToken = lexicalAnalyser.scan();

		try {
			Command cAST = parseCommand();
			programAST = new Program(cAST, previousTokenPosition);
			if (currentToken.kind != Token.EOT) {
				syntacticError("\"%\" not expected after end of program",
						currentToken.spelling);
			}
		}
		catch (SyntaxError s) { return null; }
		return programAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// LITERALS
	//
	///////////////////////////////////////////////////////////////////////////////

	// parseIntegerLiteral parses an integer-literal, and constructs
	// a leaf AST to represent it.

	IntegerLiteral parseIntegerLiteral() throws SyntaxError {
		IntegerLiteral IL = null;

		if (currentToken.kind == Token.INTLITERAL) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			IL = new IntegerLiteral(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			IL = null;
			syntacticError("integer literal expected here", "");
		}
		return IL;
	}

	// parseCharacterLiteral parses a character-literal, and constructs a leaf
	// AST to represent it.

	CharacterLiteral parseCharacterLiteral() throws SyntaxError {
		CharacterLiteral CL = null;

		if (currentToken.kind == Token.CHARLITERAL) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			CL = new CharacterLiteral(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			CL = null;
			syntacticError("character literal expected here", "");
		}
		return CL;
	}

	// parseStringLiteral parses a string-literal, and constructs a leaf
	// AST to represent it.
	StringLiteral parseStringLiteral() throws SyntaxError {
		StringLiteral SL = null;
		if(currentToken.kind == Token.STRINGLITERAL){
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			spelling = spelling.substring(1, spelling.length() - 1);
			SL = new StringLiteral(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		}
		else{
			SL = null;
			syntacticError("string literal expected here","");
		}
		return SL;
	}

	// parseIdentifier parses an identifier, and constructs a leaf AST to
	// represent it.

	Identifier parseIdentifier() throws SyntaxError {
		Identifier I = null;

		if (currentToken.kind == Token.IDENTIFIER) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			I = new Identifier(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			I = null;
			syntacticError("identifier expected here", "");
		}
		return I;
	}

	// parseOperator parses an operator, and constructs a leaf AST to
	// represent it.

	Operator parseOperator() throws SyntaxError {
		Operator O = null;

		if (currentToken.kind == Token.OPERATOR) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			O = new Operator(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			O = null;
			syntacticError("operator expected here", "");
		}
		return O;
	}
	
	AddOperator parseAddOperator() throws SyntaxError {
		AddOperator O = null;

		if (currentToken.kind == Token.ADDOPERATOR) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			O = new AddOperator(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			O = null;
			syntacticError("operator expected here", "");
		}
		return O;
	}

	MultOperator parseMultOperator() throws SyntaxError {
		MultOperator O = null;

		if (currentToken.kind == Token.MULTOPERATOR) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			O = new MultOperator(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			O = null;
			syntacticError("mult operator expected here", "");
		}
		return O;
	}
	

	///////////////////////////////////////////////////////////////////////////////
	//
	// COMMANDS
	//
	///////////////////////////////////////////////////////////////////////////////

	// parseCommand parses the command, and constructs an AST
	// to represent its phrase structure.

	Command parseCommand() throws SyntaxError {
		Command commandAST = null; // in case there's a syntactic error

		SourcePosition commandPos = new SourcePosition();

		start(commandPos);
		commandAST = parseSingleCommand();
		while (currentToken.kind == Token.SEMICOLON) {
			acceptIt();
			Command c2AST = parseSingleCommand();
			finish(commandPos);
			commandAST = new SequentialCommand(commandAST, c2AST, commandPos);
		}
		return commandAST;
	}

	Command parseSingleCommand() throws SyntaxError {
		Command commandAST = null; // in case there's a syntactic error

		SourcePosition commandPos = new SourcePosition();
		start(commandPos);

		switch (currentToken.kind) {

		case Token.IDENTIFIER:
		{
			Identifier iAST = parseIdentifier();
			if (currentToken.kind == Token.LPAREN) {
				acceptIt();
				ActualParameterSequence apsAST = parseActualParameterSequence();
				accept(Token.RPAREN);
				finish(commandPos);
				commandAST = new CallCommand(iAST, apsAST, commandPos);

			} else {
				Vname vAST = parseRestOfVname(iAST);
				accept(Token.BECOMES);
				Expression eAST = parseExpression();
				finish(commandPos);
				commandAST = new AssignCommand(vAST, eAST, commandPos);
			}
		}
		break;

		case Token.BEGIN:
			acceptIt();
			commandAST = parseCommand();
			accept(Token.END);
			break;

		case Token.LET:
		{
			acceptIt();
			Declaration dAST = parseDeclaration();
			accept(Token.IN);
			Command cAST = parseSingleCommand();
			finish(commandPos);
			commandAST = new LetCommand(dAST, cAST, commandPos);
		}
		break;

		case Token.CASE:{
			LinkedHashMap <IntegerLiteral, Command> map = new LinkedHashMap<IntegerLiteral, Command>();
			acceptIt();
			Expression eAST = parseExpression();
			accept(Token.OF);
			IntegerLiteral iLAST = parseIntegerLiteral();
			accept(Token.COLON);
			Command cAST = parseSingleCommand();
			accept(Token.SEMICOLON);
			map.put(iLAST, cAST);
			while(currentToken.kind != Token.ELSE){
				iLAST = parseIntegerLiteral();
				accept(Token.COLON);
				cAST = parseSingleCommand();
				accept(Token.SEMICOLON);
				map.put(iLAST, cAST);
			}
			accept(Token.ELSE);
			accept(Token.COLON);
			cAST = parseSingleCommand();
			finish(commandPos);
			commandAST = new CaseCommand(eAST, map, cAST, commandPos);
		}
		break;

		case Token.IF:
		{
			acceptIt();
			Expression eAST = parseExpression();
			accept(Token.THEN);
			Command c1AST = parseSingleCommand();
			accept(Token.ELSE);
			Command c2AST = parseSingleCommand();
			finish(commandPos);
			commandAST = new IfCommand(eAST, c1AST, c2AST, commandPos);
		}
		break;

		case Token.FOR:{
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.FROM);
			Expression e1AST = parseExpression();
			accept(Token.TO);
			Expression e2AST = parseExpression();
			accept(Token.DO);
			Command cAST = parseSingleCommand();
			finish(commandPos);
			commandAST = new ForCommand(iAST, e1AST, e2AST, cAST, commandPos);
		}
		break;

		case Token.REPEAT:
		{
			Command cAST;
			acceptIt();
			if (currentToken.kind == Token.UNTIL) {
				cAST = new EmptyCommand(commandPos);
			}
			else{
				cAST = parseSingleCommand();
			}

			accept(Token.UNTIL);
			Expression eAST = parseExpression();
			finish(commandPos);
			commandAST = new RepeatCommand(cAST, eAST, commandPos);
		}
		break;

		case Token.WHILE:
		{
			acceptIt();
			Expression eAST = parseExpression();
			accept(Token.DO);
			Command cAST = parseSingleCommand();
			finish(commandPos);
			commandAST = new WhileCommand(eAST, cAST, commandPos);
		}
		break;

		case Token.SEMICOLON:
		case Token.END:
		case Token.ELSE:
		case Token.IN:
		case Token.EOT:

			finish(commandPos);
			commandAST = new EmptyCommand(commandPos);
			break;

		default:
			syntacticError("\"%\" cannot start a command",
					currentToken.spelling);
		break;

		}

		return commandAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// EXPRESSIONS
	//
	///////////////////////////////////////////////////////////////////////////////

	Expression parseExpression() throws SyntaxError {
		Expression expressionAST = null; // in case there's a syntactic error

		SourcePosition expressionPos = new SourcePosition();

		start (expressionPos);

		switch (currentToken.kind) {

		case Token.LET:
		{
			acceptIt();
			Declaration dAST = parseDeclaration();
			accept(Token.IN);
			Expression eAST = parseExpression();
			finish(expressionPos);
			expressionAST = new LetExpression(dAST, eAST, expressionPos);
		}
		break;

		case Token.IF:
		{
			acceptIt();
			Expression e1AST = parseExpression();
			accept(Token.THEN);
			Expression e2AST = parseExpression();
			accept(Token.ELSE);
			Expression e3AST = parseExpression();
			finish(expressionPos);
			expressionAST = new IfExpression(e1AST, e2AST, e3AST, expressionPos);
		}
		break;

		default:
			expressionAST = parseQuatrenaryExpression();
		break;
		}
		return expressionAST;
	}
	
	Expression parseQuatrenaryExpression() throws SyntaxError {
		Expression expressionAST = null; // in case there's a syntactic error

		SourcePosition expressionPos = new SourcePosition();
		start(expressionPos);

		expressionAST = parseTertiaryExpression();
		while (currentToken.kind == Token.OPERATOR) {
			Operator mopAST = parseOperator();
			Expression e2AST = parseTertiaryExpression();
			expressionAST = new BinaryExpression (expressionAST, mopAST, e2AST,
					expressionPos);
		}
		return expressionAST;
	}

	Expression parseTertiaryExpression() throws SyntaxError {
		Expression expressionAST = null; // in case there's a syntactic error

		SourcePosition expressionPos = new SourcePosition();
		start(expressionPos);

		expressionAST = parseSecondaryExpression();
		while (currentToken.kind == Token.ADDOPERATOR) {
			AddOperator mopAST = parseAddOperator();
			Expression e2AST = parseSecondaryExpression();
			expressionAST = new BinaryExpression (expressionAST, mopAST, e2AST,
					expressionPos);
		}
		return expressionAST;
	}

	Expression parseSecondaryExpression() throws SyntaxError {
		Expression expressionAST = null; // in case there's a syntactic error

		SourcePosition expressionPos = new SourcePosition();
		start(expressionPos);

		expressionAST = parsePrimaryExpression();
		while (currentToken.kind == Token.MULTOPERATOR) {
			MultOperator opAST = parseMultOperator();
			Expression e2AST = parsePrimaryExpression();
			expressionAST = new MultExpression (expressionAST, opAST, e2AST,
					expressionPos);
		}
		return expressionAST;
	}

	Expression parsePrimaryExpression() throws SyntaxError {
		Expression expressionAST = null; // in case there's a syntactic error

		SourcePosition expressionPos = new SourcePosition();
		start(expressionPos);

		switch (currentToken.kind) {

		case Token.INTLITERAL:
		{
			IntegerLiteral ilAST = parseIntegerLiteral();
			finish(expressionPos);
			expressionAST = new IntegerExpression(ilAST, expressionPos);
		}
		break;

		case Token.CHARLITERAL:
		{
			CharacterLiteral clAST= parseCharacterLiteral();
			finish(expressionPos);
			expressionAST = new CharacterExpression(clAST, expressionPos);
		}
		break;

		case Token.STRINGLITERAL:
		{
			StringLiteral slAST= parseStringLiteral();
			finish(expressionPos);
			expressionAST = new StringExpression(slAST, expressionPos);
		}
		break;

		case Token.LBRACKET:
		{
			acceptIt();
			ArrayAggregate aaAST = parseArrayAggregate();
			accept(Token.RBRACKET);
			finish(expressionPos);
			expressionAST = new ArrayExpression(aaAST, expressionPos);
		}
		break;

		case Token.LCURLY:
		{
			acceptIt();
			RecordAggregate raAST = parseRecordAggregate();
			accept(Token.RCURLY);
			finish(expressionPos);
			expressionAST = new RecordExpression(raAST, expressionPos);
		}
		break;

		case Token.IDENTIFIER:
		{
			Identifier iAST= parseIdentifier();
			if (currentToken.kind == Token.LPAREN) {
				acceptIt();
				ActualParameterSequence apsAST = parseActualParameterSequence();
				accept(Token.RPAREN);
				finish(expressionPos);
				expressionAST = new CallExpression(iAST, apsAST, expressionPos);

			} else {
				Vname vAST = parseRestOfVname(iAST);
				finish(expressionPos);
				expressionAST = new VnameExpression(vAST, expressionPos);
			}
		}
		break;

		case Token.OPERATOR:
		{
			Operator opAST = parseOperator();
			Expression eAST = parsePrimaryExpression();
			finish(expressionPos);
			expressionAST = new UnaryExpression(opAST, eAST, expressionPos);
		}
		break;

		case Token.LPAREN:
		{
			acceptIt();
			expressionAST = parseExpression();
			accept(Token.RPAREN); 
			finish(expressionPos);
		}
		break;

		default:
			syntacticError("\"%\" cannot start an expression",
					currentToken.spelling);
		break;

		}
		return expressionAST;
	}


	RecordAggregate parseRecordAggregate() throws SyntaxError {
		RecordAggregate aggregateAST = null; // in case there's a syntactic error

		SourcePosition aggregatePos = new SourcePosition();
		start(aggregatePos);

		Identifier iAST = parseIdentifier();
		accept(Token.IS);
		Expression eAST = parseExpression();

		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			RecordAggregate aAST = parseRecordAggregate();
			finish(aggregatePos);
			aggregateAST = new MultipleRecordAggregate(iAST, eAST, aAST, aggregatePos);
		} else {
			finish(aggregatePos);
			aggregateAST = new SingleRecordAggregate(iAST, eAST, aggregatePos);
		}
		return aggregateAST;
	}

	ArrayAggregate parseArrayAggregate() throws SyntaxError {
		ArrayAggregate aggregateAST = null; // in case there's a syntactic error

		SourcePosition aggregatePos = new SourcePosition();
		start(aggregatePos);

		Expression eAST = parseExpression();
		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			ArrayAggregate aAST = parseArrayAggregate();
			finish(aggregatePos);
			aggregateAST = new MultipleArrayAggregate(eAST, aAST, aggregatePos);
		} else {
			finish(aggregatePos);
			aggregateAST = new SingleArrayAggregate(eAST, aggregatePos);
		}
		return aggregateAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// VALUE-OR-VARIABLE NAMES
	//
	///////////////////////////////////////////////////////////////////////////////

	Vname parseVname () throws SyntaxError {
		Vname vnameAST = null; // in case there's a syntactic error
		Identifier iAST = parseIdentifier();
		vnameAST = parseRestOfVname(iAST);
		return vnameAST;
	}

	Vname parseRestOfVname(Identifier identifierAST) throws SyntaxError {
		SourcePosition vnamePos = new SourcePosition();
		vnamePos = identifierAST.position;
		Vname vAST = new SimpleVname(identifierAST, vnamePos);

		while (currentToken.kind == Token.DOT ||
				currentToken.kind == Token.LBRACKET) {

			if (currentToken.kind == Token.DOT) {
				acceptIt();
				Identifier iAST = parseIdentifier();
				vAST = new DotVname(vAST, iAST, vnamePos);
			} else {
				acceptIt();
				Expression eAST = parseExpression();
				accept(Token.RBRACKET);
				finish(vnamePos);
				vAST = new SubscriptVname(vAST, eAST, vnamePos);
			}
		}
		return vAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// DECLARATIONS
	//
	///////////////////////////////////////////////////////////////////////////////

	Declaration parseDeclaration() throws SyntaxError {
		Declaration declarationAST = null; // in case there's a syntactic error

		SourcePosition declarationPos = new SourcePosition();
		start(declarationPos);
		declarationAST = parseSingleDeclaration();
		while (currentToken.kind == Token.SEMICOLON) {
			acceptIt();
			Declaration d2AST = parseSingleDeclaration();
			finish(declarationPos);
			declarationAST = new SequentialDeclaration(declarationAST, d2AST,
					declarationPos);
		}
		return declarationAST;
	}

	Declaration parseSingleDeclaration() throws SyntaxError {
		Declaration declarationAST = null; // in case there's a syntactic error

		SourcePosition declarationPos = new SourcePosition();
		start(declarationPos);

		switch (currentToken.kind) {

		case Token.CONST:
		{
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.IS);
			Expression eAST = parseExpression();
			if(eAST instanceof StringExpression){
				StringExpression se = (StringExpression)eAST;
				declarationAST = new ConstStringDeclaration(iAST, eAST, new IntegerLiteral("" + se.SL.spelling.length(), declarationPos), declarationPos);
			}
			else{
				finish(declarationPos);
				declarationAST = new ConstDeclaration(iAST, eAST, declarationPos);
			}
		}
		break;

		case Token.VAR:
		{
			acceptIt();
			Identifier iAST = parseIdentifier();
			if(currentToken.kind == Token.BECOMES){
				accept(Token.BECOMES);
				Expression eAST = parseExpression();
				finish(declarationPos);
				declarationAST = new AssignDeclaration(iAST, eAST, declarationPos);
			}
			else{
				accept(Token.COLON);
				TypeDenoter tAST = parseTypeDenoter();
				if (tAST instanceof StringTypeDenoter) {
					acceptIt();
					IntegerLiteral ilAST = parseIntegerLiteral();
					finish(declarationPos);
					declarationAST = new StringDeclaration(iAST, ilAST, declarationPos);
				}
				else
				{
					finish(declarationPos);
					declarationAST = new VarDeclaration(iAST, tAST, declarationPos);
				}
			}
		}
		break;

		case Token.PROC:
		{
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.LPAREN);
			FormalParameterSequence fpsAST = parseFormalParameterSequence();
			accept(Token.RPAREN);
			accept(Token.IS);
			Command cAST = parseSingleCommand();
			finish(declarationPos);
			declarationAST = new ProcDeclaration(iAST, fpsAST, cAST, declarationPos);
		}
		break;

		case Token.FUNC:
		{
			acceptIt();
			if(currentToken.kind == Token.IDENTIFIER){			
				Identifier iAST = parseIdentifier();
				accept(Token.LPAREN);
				FormalParameterSequence fpsAST = parseFormalParameterSequence();
				accept(Token.RPAREN);
				accept(Token.COLON);
				TypeDenoter tAST = parseTypeDenoter();
				accept(Token.IS);
				Expression eAST = parseExpression();
				finish(declarationPos);
				declarationAST = new FuncDeclaration(iAST, fpsAST, tAST, eAST,
						declarationPos);
			}
			else if(currentToken.kind == Token.OPERATOR || 
					currentToken.kind == Token.ADDOPERATOR ||
					currentToken.kind == Token.MULTOPERATOR){
				Operator oAST;
				if(currentToken.kind == Token.OPERATOR)
					oAST = parseOperator();
				else if(currentToken.kind == Token.MULTOPERATOR)
					oAST = parseMultOperator();
				else
					oAST = parseAddOperator();
				accept(Token.LPAREN);
				FormalParameterSequence fpsAST = parseFormalParameterSequence();
				accept(Token.RPAREN);
				accept(Token.COLON);
				TypeDenoter tAST = parseTypeDenoter();
				accept(Token.IS);
				Expression eAST = parseExpression();
				finish(declarationPos);
				declarationAST = new FuncOperDeclaration(oAST, fpsAST, tAST, eAST,
						declarationPos);				
			}
		}
		break;

		case Token.TYPE:
		{
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.IS);
			TypeDenoter tAST = parseTypeDenoter();
			finish(declarationPos);
			declarationAST = new TypeDeclaration(iAST, tAST, declarationPos);
		}
		break;

		default:
			syntacticError("\"%\" cannot start a declaration",
					currentToken.spelling);
		break;

		}
		return declarationAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// PARAMETERS
	//
	///////////////////////////////////////////////////////////////////////////////

	FormalParameterSequence parseFormalParameterSequence() throws SyntaxError {
		FormalParameterSequence formalsAST;

		SourcePosition formalsPos = new SourcePosition();

		start(formalsPos);
		if (currentToken.kind == Token.RPAREN) {
			finish(formalsPos);
			formalsAST = new EmptyFormalParameterSequence(formalsPos);

		} else {
			formalsAST = parseProperFormalParameterSequence();
		}
		return formalsAST;
	}

	FormalParameterSequence parseProperFormalParameterSequence() throws SyntaxError {
		FormalParameterSequence formalsAST = null; // in case there's a syntactic error;

		SourcePosition formalsPos = new SourcePosition();
		start(formalsPos);
		FormalParameter fpAST = parseFormalParameter();
		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			FormalParameterSequence fpsAST = parseProperFormalParameterSequence();
			finish(formalsPos);
			formalsAST = new MultipleFormalParameterSequence(fpAST, fpsAST,
					formalsPos);

		} else {
			finish(formalsPos);
			formalsAST = new SingleFormalParameterSequence(fpAST, formalsPos);
		}
		return formalsAST;
	}

	FormalParameter parseFormalParameter() throws SyntaxError {
		FormalParameter formalAST = null; // in case there's a syntactic error;

		SourcePosition formalPos = new SourcePosition();
		start(formalPos);

		switch (currentToken.kind) {

		case Token.IDENTIFIER:
		{
			Identifier iAST = parseIdentifier();
			accept(Token.COLON);
			TypeDenoter tAST = parseTypeDenoter();
			if(tAST instanceof StringTypeDenoter){
				acceptIt();
				IntegerLiteral ilAST = parseIntegerLiteral();
				StringTypeDenoter stAST = (StringTypeDenoter) tAST;
				stAST.IL = ilAST;
				finish(formalPos);
				formalAST = new ConstStringFormalParameter(iAST, stAST, ilAST, formalPos);
			}
			else{
				finish(formalPos);
				formalAST = new ConstFormalParameter(iAST, tAST, formalPos);
			}
		}
		break;

		case Token.VAR:
		{
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.COLON);
			TypeDenoter tAST = parseTypeDenoter();
			finish(formalPos);
			formalAST = new VarFormalParameter(iAST, tAST, formalPos);
		}
		break;

		case Token.PROC:
		{
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.LPAREN);
			FormalParameterSequence fpsAST = parseFormalParameterSequence();
			accept(Token.RPAREN);
			finish(formalPos);
			formalAST = new ProcFormalParameter(iAST, fpsAST, formalPos);
		}
		break;

		case Token.FUNC:
		{
			acceptIt();
			if(currentToken.kind == Token.IDENTIFIER){
				Identifier iAST = parseIdentifier();
				accept(Token.LPAREN);
				FormalParameterSequence fpsAST = parseFormalParameterSequence();
				accept(Token.RPAREN);
				accept(Token.COLON);
				TypeDenoter tAST = parseTypeDenoter();
				finish(formalPos);
				formalAST = new FuncFormalParameter(iAST, fpsAST, tAST, formalPos);
			}
			else if(currentToken.kind == Token.OPERATOR){
				Operator oAST = parseOperator();
				accept(Token.LPAREN);
				FormalParameterSequence fpsAST = parseFormalParameterSequence();
				accept(Token.RPAREN);
				accept(Token.COLON);
				TypeDenoter tAST = parseTypeDenoter();
				finish(formalPos);
				formalAST = new FuncOperFormalParameter(oAST, fpsAST, tAST, formalPos);				
			}
		}
		break;

		default:
			syntacticError("\"%\" cannot start a formal parameter",
					currentToken.spelling);
		break;

		}
		return formalAST;
	}


	ActualParameterSequence parseActualParameterSequence() throws SyntaxError {
		ActualParameterSequence actualsAST;

		SourcePosition actualsPos = new SourcePosition();

		start(actualsPos);
		if (currentToken.kind == Token.RPAREN) {
			finish(actualsPos);
			actualsAST = new EmptyActualParameterSequence(actualsPos);

		} else {
			actualsAST = parseProperActualParameterSequence();
		}
		return actualsAST;
	}

	ActualParameterSequence parseProperActualParameterSequence() throws SyntaxError {
		ActualParameterSequence actualsAST = null; // in case there's a syntactic error

		SourcePosition actualsPos = new SourcePosition();

		start(actualsPos);
		ActualParameter apAST = parseActualParameter();
		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			ActualParameterSequence apsAST = parseProperActualParameterSequence();
			finish(actualsPos);
			actualsAST = new MultipleActualParameterSequence(apAST, apsAST,
					actualsPos);
		} else {
			finish(actualsPos);
			actualsAST = new SingleActualParameterSequence(apAST, actualsPos);
		}
		return actualsAST;
	}

	ActualParameter parseActualParameter() throws SyntaxError {
		ActualParameter actualAST = null; // in case there's a syntactic error

		SourcePosition actualPos = new SourcePosition();

		start(actualPos);

		switch (currentToken.kind) {

		case Token.IDENTIFIER:
		case Token.INTLITERAL:
		case Token.CHARLITERAL:
		case Token.OPERATOR:
		case Token.LET:
		case Token.IF:
		case Token.LPAREN:
		case Token.LBRACKET:
		case Token.LCURLY:
		{
			Expression eAST = parseExpression();
			finish(actualPos);
			actualAST = new ConstActualParameter(eAST, actualPos);
		}
		break;
		
		case Token.STRINGLITERAL:
		{
			StringExpression eAST = (StringExpression) parseExpression();
			finish(actualPos);
			actualAST = new ConstActualParameter(eAST, actualPos);
		}
		break;

		case Token.VAR:
		{
			acceptIt();
			Vname vAST = parseVname();
			finish(actualPos);
			actualAST = new VarActualParameter(vAST, actualPos);
		}
		break;

		case Token.PROC:
		{
			acceptIt();
			Identifier iAST = parseIdentifier();
			finish(actualPos);
			actualAST = new ProcActualParameter(iAST, actualPos);
		}
		break;

		case Token.FUNC:
		{
			acceptIt();
			if(currentToken.kind == Token.IDENTIFIER){
				Identifier iAST = parseIdentifier();
				finish(actualPos);
				actualAST = new FuncActualParameter(iAST, actualPos);
			}
			else if(currentToken.kind == Token.OPERATOR){
				Operator oAST = parseOperator();
				finish(actualPos);
				actualAST = new FuncOperActualParameter(oAST, actualPos);
			}
			else if(currentToken.kind == Token.ADDOPERATOR){
				AddOperator oAST = parseAddOperator();
				finish(actualPos);
				actualAST = new FuncOperActualParameter(oAST, actualPos);
			}
			else if(currentToken.kind == Token.MULTOPERATOR){
				MultOperator oAST = parseMultOperator();
				finish(actualPos);
				actualAST = new FuncOperActualParameter(oAST, actualPos);
			}
		}
		break;

		default:
			syntacticError("\"%\" cannot start an actual parameter",
					currentToken.spelling);
		break;

		}
		return actualAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// TYPE-DENOTERS
	//
	///////////////////////////////////////////////////////////////////////////////

	TypeDenoter parseTypeDenoter() throws SyntaxError {
		TypeDenoter typeAST = null; // in case there's a syntactic error
		SourcePosition typePos = new SourcePosition();

		start(typePos);

		switch (currentToken.kind) {

		case Token.IDENTIFIER:
		{
			Identifier iAST = parseIdentifier();
			finish(typePos);
			typeAST = new SimpleTypeDenoter(iAST, typePos);
		}
		break;

		case Token.ARRAY:
		{
			acceptIt();
			IntegerLiteral ilAST = parseIntegerLiteral();
			accept(Token.OF);
			TypeDenoter tAST = parseTypeDenoter();
			finish(typePos);
			typeAST = new ArrayTypeDenoter(ilAST, tAST, typePos);
		}
		break;

		case Token.STRING:
		{
			finish(typePos);
			typeAST = new StringTypeDenoter(typePos);
			((StringTypeDenoter)typeAST).IL = new IntegerLiteral("" + currentToken.spelling.length(), typePos);
		}
		break;

		case Token.RECORD:
		{
			acceptIt();
			FieldTypeDenoter fAST = parseFieldTypeDenoter();
			accept(Token.END);
			finish(typePos);
			typeAST = new RecordTypeDenoter(fAST, typePos);
		}
		break;

		default:
			syntacticError("\"%\" cannot start a type denoter",
					currentToken.spelling);
		break;

		}
		return typeAST;
	}

	FieldTypeDenoter parseFieldTypeDenoter() throws SyntaxError {
		FieldTypeDenoter fieldAST = null; // in case there's a syntactic error

		SourcePosition fieldPos = new SourcePosition();

		start(fieldPos);
		Identifier iAST = parseIdentifier();
		accept(Token.COLON);
		TypeDenoter tAST = parseTypeDenoter();
		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			FieldTypeDenoter fAST = parseFieldTypeDenoter();
			finish(fieldPos);
			fieldAST = new MultipleFieldTypeDenoter(iAST, tAST, fAST, fieldPos);
		} else {
			finish(fieldPos);
			fieldAST = new SingleFieldTypeDenoter(iAST, tAST, fieldPos);
		}
		return fieldAST;
	}
}
