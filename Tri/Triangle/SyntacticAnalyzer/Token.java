/*
 * @(#)Token.java                        2.1 2003/10/07
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


final class Token extends Object {

  protected int kind;
  protected String spelling;
  protected SourcePosition position;

  public Token(int kind, String spelling, SourcePosition position) {

    if (kind == Token.IDENTIFIER) {
      int currentKind = firstReservedWord;
      boolean searching = true;

      while (searching) {
        int comparison = tokenTable[currentKind].compareTo(spelling);
        if (comparison == 0) {
          this.kind = currentKind;
          searching = false;
        } else if (comparison > 0 || currentKind == lastReservedWord) {
          this.kind = Token.IDENTIFIER;
          searching = false;
        } else {
          currentKind ++;
        }
      }
    } else
      this.kind = kind;

    this.spelling = spelling;
    this.position = position;

  }

  public static String spell (int kind) {
    return tokenTable[kind];
  }

  public String toString() {
    return "Kind=" + kind + ", spelling=" + spelling +
      ", position=" + position;
  }

  // Token classes...

  public static final int

    // literals, identifiers, operators...
    INTLITERAL	= 0,
    CHARLITERAL	= 1,
    STRINGLITERAL = 2,
    IDENTIFIER	= 3,
    OPERATOR	= 4,
  	ADDOPERATOR	= 5,
  	MULTOPERATOR	= 6,

    // reserved words - must be in alphabetical order...
    ARRAY		= 7,
    BEGIN		= 8,
    CASE		= 9,
    CONST		= 10,
    DO			= 11,
    ELSE		= 12,
    END			= 13,
    FOR			= 14,
    FROM		= 15,
    FUNC		= 16,
    IF			= 17,
    IN			= 18,
    LET			= 19,
    OF			= 20,
    PROC		= 21,
    RECORD		= 22,
    REPEAT		= 23,
    STRING		= 24,
    THEN		= 25,
    TO			= 26,
    TYPE		= 27,
    UNTIL		= 28,
    VAR			= 29,
    WHILE		= 30,

    // punctuation...
    DOT			= 31,
    COLON		= 32,
    SEMICOLON	= 33,
    COMMA		= 34,
    BECOMES		= 35,
    IS			= 36,

    // brackets...
    LPAREN		= 37,
    RPAREN		= 38,
    LBRACKET	= 39,
    RBRACKET	= 40,
    LCURLY		= 41,
    RCURLY		= 42,
    
    // special tokens...
    EOT			= 43,
    ERROR		= 44;

  private static String[] tokenTable = new String[] {
    "<int>",
    "<char>",
    "<stringliteral>",
    "<identifier>",
    "<operator>",
    "<addoperator>",
    "<multopertor>",
    "array",
    "begin",
    "case",
    "const",
    "do",
    "else",
    "end",
    "for",
    "from",
    "func",
    "if",
    "in",
    "let",
    "of",
    "proc",
    "record",
    "repeat",
    "string",
    "then",
    "to",
    "type",
    "until",
    "var",
    "while",
    ".",
    ":",
    ";",
    ",",
    ":=",
    "~",
    "(",
    ")",
    "[",
    "]",
    "{",
    "}",
    "",
    "<error>"
  };

  private final static int	firstReservedWord = Token.ARRAY,
  				lastReservedWord  = Token.WHILE;
}
