/*
 * @(#)Frame.java                        2.1 2003/10/07
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

public class Frame {

  public Frame() {
    this.level = 0;
    this.size = 0;
  }

  public Frame(int level, Integer size) {
    this.level = level;
    this.size = size.intValue();
  }

  public Frame(int level, int size) {
    this.level = level;
    this.size = size;
  }

  public Frame(Frame frame, int sizeIncrement) {
    this.level = frame.level;
    this.size = frame.size + sizeIncrement;
  }

  public Frame(Frame frame, Integer sizeIncrement) {
    this.level = frame.level;
    this.size = frame.size + sizeIncrement.intValue();
  }

  protected int level;
  protected int size;
}
