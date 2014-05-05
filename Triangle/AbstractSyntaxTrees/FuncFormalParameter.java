/*
 * @(#)FuncFormalParameter.java                2.1 2003/10/07
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

package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class FuncFormalParameter extends FormalParameter {

  public FuncFormalParameter (Identifier iAST, FormalParameterSequence fpsAST,
                       TypeDenoter tAST, SourcePosition thePosition) {
    super (thePosition);
    I = iAST;
    FPS = fpsAST;
    T = tAST;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitFuncFormalParameter(this, o);
  }

  public boolean equals (Object fpAST) {
  	if (fpAST instanceof FuncFormalParameter) {
  	  FuncFormalParameter ffpAST = (FuncFormalParameter) fpAST;
  	  return FPS.equals(ffpAST.FPS) && T.equals(ffpAST.T);
  	} else
  	  return false;
  }

  public Identifier I;
  public FormalParameterSequence FPS;
  public TypeDenoter T;
}
