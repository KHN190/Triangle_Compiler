/*
 * @(#)LayoutVisitor.java                        2.1 2003/10/07
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

package Triangle.TreeDrawer;

import java.awt.FontMetrics;

import Triangle.AbstractSyntaxTrees.AST;
import Triangle.AbstractSyntaxTrees.AnyTypeDenoter;
import Triangle.AbstractSyntaxTrees.ArrayExpression;
import Triangle.AbstractSyntaxTrees.ArrayTypeDenoter;
import Triangle.AbstractSyntaxTrees.AssignCommand;
import Triangle.AbstractSyntaxTrees.AssignDeclaration;
import Triangle.AbstractSyntaxTrees.BinaryExpression;
import Triangle.AbstractSyntaxTrees.BinaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.BoolTypeDenoter;
import Triangle.AbstractSyntaxTrees.CallCommand;
import Triangle.AbstractSyntaxTrees.CallExpression;
import Triangle.AbstractSyntaxTrees.CaseCommand;
import Triangle.AbstractSyntaxTrees.CharTypeDenoter;
import Triangle.AbstractSyntaxTrees.CharacterExpression;
import Triangle.AbstractSyntaxTrees.CharacterLiteral;
import Triangle.AbstractSyntaxTrees.ConstActualParameter;
import Triangle.AbstractSyntaxTrees.ConstDeclaration;
import Triangle.AbstractSyntaxTrees.ConstFormalParameter;
import Triangle.AbstractSyntaxTrees.ConstStringDeclaration;
import Triangle.AbstractSyntaxTrees.ConstStringFormalParameter;
import Triangle.AbstractSyntaxTrees.DotVname;
import Triangle.AbstractSyntaxTrees.EmptyActualParameterSequence;
import Triangle.AbstractSyntaxTrees.EmptyCommand;
import Triangle.AbstractSyntaxTrees.EmptyExpression;
import Triangle.AbstractSyntaxTrees.EmptyFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.ErrorTypeDenoter;
import Triangle.AbstractSyntaxTrees.ForCommand;
import Triangle.AbstractSyntaxTrees.FuncActualParameter;
import Triangle.AbstractSyntaxTrees.FuncDeclaration;
import Triangle.AbstractSyntaxTrees.FuncFormalParameter;
import Triangle.AbstractSyntaxTrees.FuncOperActualParameter;
import Triangle.AbstractSyntaxTrees.FuncOperDeclaration;
import Triangle.AbstractSyntaxTrees.FuncOperFormalParameter;
import Triangle.AbstractSyntaxTrees.Identifier;
import Triangle.AbstractSyntaxTrees.IfCommand;
import Triangle.AbstractSyntaxTrees.IfExpression;
import Triangle.AbstractSyntaxTrees.IntTypeDenoter;
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
import Triangle.AbstractSyntaxTrees.UnaryExpression;
import Triangle.AbstractSyntaxTrees.UnaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.VarActualParameter;
import Triangle.AbstractSyntaxTrees.VarDeclaration;
import Triangle.AbstractSyntaxTrees.VarFormalParameter;
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;

public class LayoutVisitor implements Visitor {

  private final int BORDER = 5;
  private final int PARENT_SEP = 30;

  private FontMetrics fontMetrics;

  public LayoutVisitor (FontMetrics fontMetrics) {
    this.fontMetrics = fontMetrics;
  }

  // Commands
  public Object visitAssignCommand(AssignCommand ast, Object obj) {
    return layoutBinary("AssignCom.", ast.V, ast.E);
  }

  public Object visitCallCommand(CallCommand ast, Object obj) {
    return layoutBinary("CallCom.", ast.I, ast.APS);
   }

  public Object visitEmptyCommand(EmptyCommand ast, Object obj) {
    return layoutNullary("EmptyCom.");
  }

  public Object visitIfCommand(IfCommand ast, Object obj) {
    return layoutTernary("IfCom.", ast.E, ast.C1, ast.C2);
  }

  public Object visitLetCommand(LetCommand ast, Object obj) {
    return layoutBinary("LetCom.", ast.D, ast.C);
  }

  public Object visitSequentialCommand(SequentialCommand ast, Object obj) {
    return layoutBinary("Seq.Com.", ast.C1, ast.C2);
  }

  public Object visitWhileCommand(WhileCommand ast, Object obj) {
    return layoutBinary("WhileCom.", ast.E, ast.C);
  }


  // Expressions
  public Object visitArrayExpression(ArrayExpression ast, Object obj) {
    return layoutUnary("ArrayExpr.", ast.AA);
  }

  public Object visitBinaryExpression(BinaryExpression ast, Object obj) {
    return layoutTernary("Bin.Expr.", ast.E1, ast.O, ast.E2);
  }

  public Object visitCallExpression(CallExpression ast, Object obj) {
    return layoutBinary("CallExpr.", ast.I, ast.APS);
  }

  public Object visitCharacterExpression(CharacterExpression ast, Object obj) {
    return layoutUnary("Char.Expr.", ast.CL);
  }

  public Object visitEmptyExpression(EmptyExpression ast, Object obj) {
    return layoutNullary("EmptyExpr.");
  }

  public Object visitIfExpression(IfExpression ast, Object obj) {
    return layoutTernary("IfExpr.", ast.E1, ast.E2, ast.E3);
  }

  public Object visitIntegerExpression(IntegerExpression ast, Object obj) {
    return layoutUnary("Int.Expr.", ast.IL);
  }

  public Object visitLetExpression(LetExpression ast, Object obj) {
    return layoutBinary("LetExpr.", ast.D, ast.E);
  }

  public Object visitRecordExpression(RecordExpression ast, Object obj) {
    return layoutUnary("Rec.Expr.", ast.RA);
  }

  public Object visitUnaryExpression(UnaryExpression ast, Object obj) {
    return layoutBinary("UnaryExpr.", ast.O, ast.E);
  }

  public Object visitVnameExpression(VnameExpression ast, Object obj) {
    return layoutUnary("VnameExpr.", ast.V);
  }


  // Declarations
  public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object obj) {
    return layoutQuaternary("Bin.Op.Decl.", ast.O, ast.ARG1, ast.ARG2, ast.RES);
  }

  public Object visitConstDeclaration(ConstDeclaration ast, Object obj) {
    return layoutBinary("ConstDecl.", ast.I, ast.E);
  }

  public Object visitFuncDeclaration(FuncDeclaration ast, Object obj) {
    return layoutQuaternary("FuncDecl.", ast.I, ast.FPS, ast.T, ast.E);
  }

  public Object visitProcDeclaration(ProcDeclaration ast, Object obj) {
    return layoutTernary("ProcDecl.", ast.I, ast.FPS, ast.C);
  }

  public Object visitSequentialDeclaration(SequentialDeclaration ast, Object obj) {
    return layoutBinary("Seq.Decl.", ast.D1, ast.D2);
  }

  public Object visitTypeDeclaration(TypeDeclaration ast, Object obj) {
    return layoutBinary("TypeDecl.", ast.I, ast.T);
  }

  public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object obj) {
    return layoutTernary("UnaryOp.Decl.", ast.O, ast.ARG, ast.RES);
  }

  public Object visitVarDeclaration(VarDeclaration ast, Object obj) {
    return layoutBinary("VarDecl.", ast.I, ast.T);
  }


  // Array Aggregates
  public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object obj) {
    return layoutBinary("Mult.ArrayAgg.", ast.E, ast.AA);
  }

  public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object obj) {
    return layoutUnary("Sing.ArrayAgg.", ast.E);
  }


  // Record Aggregates
  public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object obj) {
    return layoutTernary("Mult.Rec.Agg.", ast.I, ast.E, ast.RA);
  }

  public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object obj) {
    return layoutBinary("Sing.Rec.Agg.", ast.I, ast.E);
  }


  // Formal Parameters
  public Object visitConstFormalParameter(ConstFormalParameter ast, Object obj) {
    return layoutBinary("ConstF.P.", ast.I, ast.T);
  }

  public Object visitFuncFormalParameter(FuncFormalParameter ast, Object obj) {
    return layoutTernary("FuncF.P.", ast.I, ast.FPS, ast.T);
  }

  public Object visitProcFormalParameter(ProcFormalParameter ast, Object obj) {
    return layoutBinary("ProcF.P.", ast.I, ast.FPS);
  }

  public Object visitVarFormalParameter(VarFormalParameter ast, Object obj) {
    return layoutBinary("VarF.P.", ast.I, ast.T);
  }


  public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object obj) {
    return layoutNullary("EmptyF.P.S.");
  }

  public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object obj) {
    return layoutBinary("Mult.F.P.S.", ast.FP, ast.FPS);
  }

  public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object obj) {
    return layoutUnary("Sing.F.P.S.", ast.FP);
  }


  // Actual Parameters
  public Object visitConstActualParameter(ConstActualParameter ast, Object obj) {
    return layoutUnary("ConstA.P.", ast.E);
  }

  public Object visitFuncActualParameter(FuncActualParameter ast, Object obj) {
    return layoutUnary("FuncA.P.", ast.I);
  }

  public Object visitProcActualParameter(ProcActualParameter ast, Object obj) {
    return layoutUnary("ProcA.P.", ast.I);
  }

  public Object visitVarActualParameter(VarActualParameter ast, Object obj) {
    return layoutUnary("VarA.P.", ast.V);
  }


  public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object obj) {
    return layoutNullary("EmptyA.P.S.");
  }

  public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object obj) {
    return layoutBinary("Mult.A.P.S.", ast.AP, ast.APS);
  }

  public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object obj) {
    return layoutUnary("Sing.A.P.S.", ast.AP);
  }


  // Type Denoters
  public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object obj) {
    return layoutNullary("any");
  }

  public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object obj) {
    return layoutBinary("ArrayTypeD.", ast.IL, ast.T);
  }

  public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object obj) {
    return layoutNullary("bool");
  }

  public Object visitCharTypeDenoter(CharTypeDenoter ast, Object obj) {
    return layoutNullary("char");
  }

  public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object obj) {
    return layoutNullary("error");
  }

  public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast, Object obj) {
    return layoutUnary("Sim.TypeD.", ast.I);
  }

  public Object visitIntTypeDenoter(IntTypeDenoter ast, Object obj) {
    return layoutNullary("int");
  }

  public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object obj) {
    return layoutUnary("Rec.TypeD.", ast.FT);
  }


  public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Object obj) {
    return layoutTernary("Mult.F.TypeD.", ast.I, ast.T, ast.FT);
  }

  public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Object obj) {
    return layoutBinary("Sing.F.TypeD.", ast.I, ast.T);
  }


  // Literals, Identifiers and Operators
  public Object visitCharacterLiteral(CharacterLiteral ast, Object obj) {
    return layoutNullary(ast.spelling);
  }

  public Object visitIdentifier(Identifier ast, Object obj) {
    return layoutNullary(ast.spelling);
 }

  public Object visitIntegerLiteral(IntegerLiteral ast, Object obj) {
    return layoutNullary(ast.spelling);
  }

  public Object visitOperator(Operator ast, Object obj) {
    return layoutNullary(ast.spelling);
  }


  // Value-or-variable names
  public Object visitDotVname(DotVname ast, Object obj) {
    return layoutBinary("DotVname", ast.I, ast.V);
  }

  public Object visitSimpleVname(SimpleVname ast, Object obj) {
    return layoutUnary("Sim.Vname", ast.I);
  }

  public Object visitSubscriptVname(SubscriptVname ast, Object obj) {
    return layoutBinary("Sub.Vname",
        ast.V, ast.E);
  }


  // Programs
  public Object visitProgram(Program ast, Object obj) {
    return layoutUnary("Program", ast.C);
  }

  private DrawingTree layoutCaption (String name) {
    int w = fontMetrics.stringWidth(name) + 4;
    int h = fontMetrics.getHeight() + 4;
    return new DrawingTree(name, w, h);
  }

  private DrawingTree layoutNullary (String name) {
    DrawingTree dt = layoutCaption(name);
    dt.contour.upper_tail = new Polyline(0, dt.height + 2 * BORDER, null);
    dt.contour.upper_head = dt.contour.upper_tail;
    dt.contour.lower_tail = new Polyline(-dt.width - 2 * BORDER, 0, null);
    dt.contour.lower_head = new Polyline(0, dt.height + 2 * BORDER, dt.contour.lower_tail);
    return dt;
  }

  private DrawingTree layoutUnary (String name, AST child1) {
    DrawingTree dt = layoutCaption(name);
    DrawingTree d1 = (DrawingTree) child1.visit(this, null);
    dt.setChildren(new DrawingTree[] {d1});
    attachParent(dt, join(dt));
    return dt;
  }

  private DrawingTree layoutBinary (String name, AST child1, AST child2) {
    DrawingTree dt = layoutCaption(name);
    DrawingTree d1 = (DrawingTree) child1.visit(this, null);
    DrawingTree d2 = (DrawingTree) child2.visit(this, null);
    dt.setChildren(new DrawingTree[] {d1, d2});
    attachParent(dt, join(dt));
    return dt;
  }

  private DrawingTree layoutTernary (String name, AST child1, AST child2,
                                     AST child3) {
    DrawingTree dt = layoutCaption(name);
    DrawingTree d1 = (DrawingTree) child1.visit(this, null);
    DrawingTree d2 = (DrawingTree) child2.visit(this, null);
    DrawingTree d3 = (DrawingTree) child3.visit(this, null);
    dt.setChildren(new DrawingTree[] {d1, d2, d3});
    attachParent(dt, join(dt));
    return dt;
  }

  private DrawingTree layoutQuaternary (String name, AST child1, AST child2,
                                        AST child3, AST child4) {
    DrawingTree dt = layoutCaption(name);
    DrawingTree d1 = (DrawingTree) child1.visit(this, null);
    DrawingTree d2 = (DrawingTree) child2.visit(this, null);
    DrawingTree d3 = (DrawingTree) child3.visit(this, null);
    DrawingTree d4 = (DrawingTree) child4.visit(this, null);
    dt.setChildren(new DrawingTree[] {d1, d2, d3, d4});
    attachParent(dt, join(dt));
    return dt;
  }

  private void attachParent(DrawingTree dt, int w) {
    int y = PARENT_SEP;
    int x2 = (w - dt.width) / 2 - BORDER;
    int x1 = x2 + dt.width + 2 * BORDER - w;

    dt.children[0].offset.y = y + dt.height;
    dt.children[0].offset.x = x1;
    dt.contour.upper_head = new Polyline(0, dt.height,
                                new Polyline(x1, y, dt.contour.upper_head));
    dt.contour.lower_head = new Polyline(0, dt.height,
                                new Polyline(x2, y, dt.contour.lower_head));
  }

  private int join (DrawingTree dt) {
    int w, sum;

    dt.contour = dt.children[0].contour;
    sum = w = dt.children[0].width + 2 * BORDER;

    for (int i = 1; i < dt.children.length; i++) {
      int d = merge(dt.contour, dt.children[i].contour);
      dt.children[i].offset.x = d + w;
      dt.children[i].offset.y = 0;
      w = dt.children[i].width + 2 * BORDER;
      sum += d + w;
    }
    return sum;
  }

  private int merge(Polygon c1, Polygon c2) {
    int x, y, total, d;
    Polyline lower, upper, b;

    x = y = total = 0;
    upper = c1.lower_head;
    lower = c2.upper_head;

    while (lower != null && upper != null) {
        d = offset(x, y, lower.dx, lower.dy, upper.dx, upper.dy);
	x += d;
	total += d;

	if (y + lower.dy <= upper.dy) {
	  x += lower.dx;
	  y += lower.dy;
	  lower = lower.link;
	} else {
	  x -= upper.dx;
	  y -= upper.dy;
	  upper = upper.link;
	}
      }

      if (lower != null) {
        b = bridge(c1.upper_tail, 0, 0, lower, x, y);
        c1.upper_tail = (b.link != null) ? c2.upper_tail : b;
        c1.lower_tail = c2.lower_tail;
      } else {
        b = bridge(c2.lower_tail, x, y, upper, 0, 0);
        if (b.link == null) {
          c1.lower_tail = b;
        }
      }

      c1.lower_head = c2.lower_head;

      return total;
    }

  private int offset (int p1, int p2, int a1, int a2, int b1, int b2) {
    int d, s, t;

    if (b2 <= p2 || p2 + a2 <= 0) {
      return 0;
    }

    t = b2 * a1 - a2 * b1;
    if (t > 0) {
      if (p2 < 0) {
        s = p2 * a1;
        d = s / a2 - p1;
      } else if (p2 > 0) {
        s = p2 * b1;
        d = s / b2 - p1;
      } else {
        d = -p1;
      }
    } else if (b2 < p2 + a2) {
      s = (b2 - p2) * a1;
      d = b1 - (p1 + s / a2);
    } else if (b2 > p2 + a2) {
      s = (a2 + p2) * b1;
      d = s / b2 - (p1 + a1);
    } else {
      d = b1 - (p1 + a1);
    }

    if (d > 0) {
      return d;
    } else {
      return 0;
    }
  }

  private Polyline bridge (Polyline line1, int x1, int y1,
                           Polyline line2, int x2, int y2) {
    int dy, dx, s;
    Polyline r;

    dy = y2 + line2.dy - y1;
    if (line2.dy == 0) {
      dx = line2.dx;
    } else {
      s = dy * line2.dx;
      dx = s / line2.dy;
    }

    r = new Polyline(dx, dy, line2.link);
    line1.link = new Polyline(x2 + line2.dx - dx - x1, 0, r);

    return r;
  }

public Object visitStringDeclaration(StringDeclaration stringDeclaration,
		Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitStringTypeDenoter(StringTypeDenoter stringTypeDenoter,
		Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitRepeatCommand(RepeatCommand repeatCommand, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitStringExpression(StringExpression ast, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitStringLiteral(StringLiteral stringLiteral, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitForCommand(ForCommand forCommand, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitCaseCommand(CaseCommand caseCommand, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitAssignDeclaration(AssignDeclaration assignDeclaration,
		Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitMultExpression(MultExpression multExpression, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitFuncOperDeclaration(FuncOperDeclaration funcOperDeclaration,
		Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitConstStringFormalParameter(
		ConstStringFormalParameter constStringFormalParameter, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitFuncOperActualParameter(
		FuncOperActualParameter funcOperActualParameter, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitFuncOperFormalParameter(
		FuncOperFormalParameter funcOperFormalParameter, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitMultOperator(MultOperator operator, Object o) {
	// TODO Auto-generated method stub
	return null;
}

public Object visitConstStringDeclaration(
		ConstStringDeclaration constStringDeclaration, Object o) {
	// TODO Auto-generated method stub
	return null;
}

}