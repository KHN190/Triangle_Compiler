/*
 * @(#)MultipleFormalParameterSequence.java        2.1 2003/10/07
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

public class MultipleFormalParameterSequence extends FormalParameterSequence {

  public MultipleFormalParameterSequence (FormalParameter fpAST, FormalParameterSequence fpsAST,
                                   SourcePosition thePosition) {
    super (thePosition);
    FP = fpAST;
    FPS = fpsAST;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitMultipleFormalParameterSequence(this, o);
  }

  public boolean equals(Object fpsAST) {
    if (fpsAST instanceof MultipleFormalParameterSequence) {
      MultipleFormalParameterSequence mfpsAST =
          (MultipleFormalParameterSequence) fpsAST;
      return FP.equals(mfpsAST.FP) && FPS.equals(mfpsAST.FPS);
    } else
      return false;
  }

  public FormalParameter FP;
  public FormalParameterSequence FPS;
}
