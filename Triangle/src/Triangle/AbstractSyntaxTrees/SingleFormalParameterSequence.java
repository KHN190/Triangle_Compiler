/*
 * @(#)SingleFormalParameterSequence.java                2.1 2003/10/07
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

public class SingleFormalParameterSequence extends FormalParameterSequence {

  public SingleFormalParameterSequence (FormalParameter fpAST,
                                 SourcePosition thePosition) {
    super (thePosition);
    FP = fpAST;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitSingleFormalParameterSequence(this, o);
  }

  public boolean equals(Object fpsAST) {
    if (fpsAST instanceof SingleFormalParameterSequence) {
      SingleFormalParameterSequence sfpsAST =
          (SingleFormalParameterSequence) fpsAST;
      return FP.equals(sfpsAST.FP);
    } else
      return false;
  }

  public FormalParameter FP;
}
