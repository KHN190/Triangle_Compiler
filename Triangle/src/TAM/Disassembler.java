/*
 * @(#)Disassembler.java                        2.1 2003/10/07
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

package TAM;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Disassembles the TAM code in the given file, and displays the
 * instructions on standard output.
 *
 * For example:
 * <pre>
 *   java TAM.Disassembler obj.tam
 * </pre>
 *
 * <p>
 * Copyright 1991 David A. Watt, University of Glasgow<br>
 * Copyright 1998 Deryck F. Brown, The Robert Gordon University<br>
 * </p>
 *
 */

public class Disassembler {

  static String objectName;

  static int CT;

  /**
   * Writes the r-field of an instruction in the form "l<I>reg</I>r", where
   * l and r are the bracket characters to use.
   * @param leftbracket		the character to print before the register.
   * @param r			the number of the register.
   * @param rightbracket 	the character to print after the register.
   */
  private static void writeR (char leftbracket, int r, char rightbracket) {

    System.out.print(leftbracket);
    switch (r) {
      case Machine.CBr:
        System.out.print ("CB");
        break;
      case Machine.CTr:
	System.out.print ("CT");
	break;
      case Machine.PBr:
	System.out.print ("PB");
	break;
      case Machine.PTr:
	System.out.print ("PT");
	break;
      case Machine.SBr:
	System.out.print ("SB");
	break;
      case Machine.STr:
	System.out.print ("ST");
	break;
      case Machine.HBr:
	System.out.print ("HB");
	break;
      case Machine.HTr:
	System.out.print ("HT");
	break;
      case Machine.LBr:
	System.out.print ("LB");
	break;
      case Machine.L1r:
	System.out.print ("L1");
	break;
      case Machine.L2r:
	System.out.print ("L2");
	break;
      case Machine.L3r:
	System.out.print ("L3");
	break;
      case Machine.L4r:
	System.out.print ("L4");
	break;
      case Machine.L5r:
	System.out.print ("L5");
	break;
      case Machine.L6r:
	System.out.print ("L6");
	break;
      case Machine.CPr:
	System.out.print ("CP");
	break;
    }
    System.out.print (rightbracket);
  }

  /**
   * Writes a void n-field of an instruction.
   */
  private static void blankN() {
    System.out.print ("      ");
  }

  // Writes the n-field of an instruction.
  /**
   * Writes the n-field of an instruction in the form "(n)".
   * @param n	the integer to write.
   */
  private static void writeN (int n) {
    System.out.print ("(" + n + ") ");
    if (n < 10)
      System.out.print ("  ");
    else if (n < 100)
      System.out.print (" ");
  }

  /**
   * Writes the d-field of an instruction.
   * @param d	the integer to write.
   */
  private static void writeD (int d) {
    System.out.print (d);
  }

  /**
   * Writes the name of primitive routine with relative address d.
   * @param d	the displacment of the primitive routine.
   */
  private static void writePrimitive (int d) {
    switch (d) {
      case Machine.idDisplacement:
        System.out.print ("id      ");
	break;
      case Machine.notDisplacement:
	System.out.print ("not     ");
	break;
      case Machine.andDisplacement:
	System.out.print ("and     ");
	break;
      case Machine.orDisplacement:
	System.out.print ("or      ");
	break;
      case Machine.succDisplacement:
	System.out.print ("succ    ");
	break;
      case Machine.predDisplacement:
	System.out.print ("pred    ");
	break;
      case Machine.negDisplacement:
	System.out.print ("neg     ");
	break;
      case Machine.addDisplacement:
	System.out.print ("add     ");
	break;
      case Machine.subDisplacement:
	System.out.print ("sub     ");
	break;
      case Machine.multDisplacement:
	System.out.print ("mult    ");
	break;
      case Machine.divDisplacement:
	System.out.print ("div     ");
	break;
      case Machine.modDisplacement:
	System.out.print ("mod     ");
	break;
      case Machine.ltDisplacement:
	System.out.print ("lt      ");
	break;
      case Machine.leDisplacement:
	System.out.print ("le      ");
	break;
      case Machine.geDisplacement:
	System.out.print ("ge      ");
	break;
      case Machine.gtDisplacement:
	System.out.print ("gt      ");
	break;
      case Machine.eqDisplacement:
	System.out.print ("eq      ");
	break;
      case Machine.neDisplacement:
	System.out.print ("ne      ");
	break;
      case Machine.eolDisplacement:
	System.out.print ("eol     ");
	break;
      case Machine.eofDisplacement:
	System.out.print ("eof     ");
	break;
      case Machine.getDisplacement:
	System.out.print ("get     ");
	break;
      case Machine.putDisplacement:
	System.out.print ("put     ");
	break;
      case Machine.geteolDisplacement:
	System.out.print ("geteol  ");
	break;
      case Machine.puteolDisplacement:
	System.out.print ("puteol  ");
	break;
      case Machine.getintDisplacement:
	System.out.print ("getint  ");
	break;
      case Machine.putintDisplacement:
	System.out.print ("putint  ");
	break;
      case Machine.newDisplacement:
	System.out.print ("new     ");
	break;
      case Machine.disposeDisplacement:
	System.out.print ("dispose ");
	break;
    }
  }

  /**
   * Writes the given instruction in assembly-code format.
   * @param instr	the instruction to display.
   */
  private static void writeInstruction (Instruction instr) {

    switch (instr.op) {
      case Machine.LOADop:
	System.out.print ("LOAD  ");
	writeN(instr.n);
	writeD(instr.d);
	writeR('[', instr.r, ']');
	break;

      case Machine.LOADAop:
        System.out.print ("LOADA ");
        blankN();
        writeD(instr.d);
        writeR('[', instr.r, ']');
        break;

      case Machine.LOADIop:
        System.out.print ("LOADI ");
        writeN(instr.n);
        break;

      case Machine.LOADLop:
        System.out.print ("LOADL ");
        blankN();
        writeD(instr.d);
        break;

      case Machine.STOREop:
        System.out.print ("STORE ");
        writeN(instr.n);
        writeD(instr.d);
        writeR('[', instr.r, ']');
        break;

      case Machine.STOREIop:
        System.out.print ("STOREI");
        writeN(instr.n);
        break;

      case Machine.CALLop:
        System.out.print ("CALL  ");
        if (instr.r == Machine.PBr) {
          blankN();
          writePrimitive(instr.d);
        } else {
          writeR('(', instr.n, ')');
          System.out.print ("  ");
          writeD(instr.d);
          writeR('[', instr.r, ']');
        }
        break;

      case Machine.CALLIop:
        System.out.print ("CALLI ");
	break;

      case Machine.RETURNop:
        System.out.print ("RETURN");
        writeN(instr.n);
        writeD(instr.d);
        break;

      case Machine.PUSHop:
        System.out.print ("PUSH  ");
        blankN();
        writeD(instr.d);
        break;

      case Machine.POPop:
        System.out.print ("POP   ");
        writeN(instr.n);
        writeD(instr.d);
        break;

      case Machine.JUMPop:
        System.out.print ("JUMP  ");
        blankN();
        writeD(instr.d);
        writeR('[', instr.r, ']');
        break;

      case Machine.JUMPIop:
        System.out.print ("JUMPI ");
        break;

      case Machine.JUMPIFop:
        System.out.print ("JUMPIF");
        writeN(instr.n);
        writeD(instr.d);
        writeR('[', instr.r, ']');
        break;

      case Machine.HALTop:
        System.out.print ("HALT  ");
    }
  }

  /**
   * Writes all instructions of the program in code store.
   */
  private static void disassembleProgram() {
    for (int addr = Machine.CB; addr < CT; addr++) {
      System.out.print (addr + ":  ");
      writeInstruction(Machine.code[addr]);
      System.out.println();
    }
  }


// LOADING

  /**
   * Loads the TAM object program into code store from the named file.
   * @param objectName	the name of the file containing the program.
   */
  static void loadObjectProgram (String objectName) {

    FileInputStream objectFile = null;
    DataInputStream objectStream = null;

    int addr;
    boolean finished = false;

    try {
      objectFile = new FileInputStream (objectName);
      objectStream = new DataInputStream (objectFile);

      addr = Machine.CB;
      while (!finished) {
        Machine.code[addr] = Instruction.read(objectStream);
        if (Machine.code[addr] == null)
          finished = true;
        else
          addr = addr + 1;
      }
      CT = addr;
      objectFile.close();
    } catch (FileNotFoundException s) {
      CT = Machine.CB;
      System.err.println ("Error opening object file: " + s);
    } catch (IOException s) {
      CT = Machine.CB;
      System.err.println ("Error reading object file: " + s);
    }
  }


// DISASSEMBLE

  public static void main(String[] args) {
    System.out.println ("********** TAM Disassembler (Sun Version 2.1) **********");

    if (args.length == 1)
      objectName = args[0];
    else
      objectName = "obj.tam";

    loadObjectProgram(objectName);
    disassembleProgram();
  }
}
