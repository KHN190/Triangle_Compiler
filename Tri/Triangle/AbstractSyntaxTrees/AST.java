/*
 * @(#)AST.java                        2.1 2003/10/07
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

import Triangle.CodeGenerator.RuntimeEntity;
import Triangle.SyntacticAnalyzer.SourcePosition;

public abstract class AST implements Cloneable {
  // want to print "Program", not "Triangle.AbstractSyntaxTrees.Program"

  protected final static String packageName = "Triangle.AbstractSyntaxTrees";
  protected static int className = packageName.length()+1;

  public AST (SourcePosition thePosition) {
    position = thePosition;
    entity = null;
  }

  public SourcePosition getPosition() {
    return position;
  }

  public abstract Object visit(Visitor v, Object o);

  public void display() {
      display(0);
      System.out.println("\n");
  }
    
  protected void display(int indent){
      System.out.println();
      for (int i=0; i<indent; i++)
	  System.out.print("  ");
      System.out.print(getClass().getName().substring(className));
  }

  public Object clone() {
	  try {
		  return super.clone();
	  } catch (CloneNotSupportedException ex) {
		  return null;
	  }
  }

  public SourcePosition	position;

  public RuntimeEntity  entity;

}

