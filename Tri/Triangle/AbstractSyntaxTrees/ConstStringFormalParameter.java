/*
 * @(#)ConstFormalParameter.java                        2.1 2003/10/07
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

public class ConstStringFormalParameter extends FormalParameter {


public ConstStringFormalParameter (Identifier iAST, TypeDenoter tAST, IntegerLiteral ilAST,
                        SourcePosition thePosition) {
    super (thePosition);
    I = iAST;
    T = tAST;
    IL = ilAST;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitConstStringFormalParameter(this, o);
  }

  public void display(int indent){
      super.display(indent);
      I.display(indent+1);
      T.display(indent+1);
      IL.display(indent+1);
  }
  
  public Identifier I;
  public TypeDenoter T;
  public IntegerLiteral IL;


  public boolean equals (Object fpAST) {
  	if (fpAST instanceof ConstStringFormalParameter) {
  	  ConstStringFormalParameter cfpAST = (ConstStringFormalParameter) fpAST;
  	  return (T.equals(cfpAST.T) && IL.equals(cfpAST.IL));
  	} else
  	  return false;
  }
}
