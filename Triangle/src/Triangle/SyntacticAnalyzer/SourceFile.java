/*
 * @(#)SourceFile.java                        2.1 2003/10/07
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

public class SourceFile {

  public static final char EOL = '\n';
  public static final char EOT = '\u0000';

  java.io.File sourceFile;
  java.io.FileInputStream source;
  int currentLine;

  public SourceFile(String filename) {
    try {
      sourceFile = new java.io.File(filename);
      source = new java.io.FileInputStream(sourceFile);
      currentLine = 1;
    }
    catch (java.io.IOException s) {
      sourceFile = null;
      source = null;
      currentLine = 0;
    }
  }

  char getSource() {
    try {
      int c = source.read();

      if (c == -1) {
        c = EOT;
      } else if (c == EOL) {
          currentLine++;
      }
      return (char) c;
    }
    catch (java.io.IOException s) {
      return EOT;
    }
  }

  int getCurrentLine() {
    return currentLine;
  }
}
