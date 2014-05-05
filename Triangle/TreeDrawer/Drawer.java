/*
 * @(#)Drawer.java                        2.1 2003/10/07
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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import Triangle.AbstractSyntaxTrees.Program;

public class Drawer {

  private DrawerFrame frame;
  private DrawerPanel panel;

  private Program theAST;
  private DrawingTree theDrawing;

  // Draw the AST representing a complete program.

  public void draw(Program ast) {
    theAST = ast;
    panel = new DrawerPanel(this);
    frame = new DrawerFrame(panel);

    Font font = new Font("SansSerif", Font.PLAIN, 12);
    frame.setFont(font);

    FontMetrics fontMetrics = frame.getFontMetrics(font);

    LayoutVisitor layout = new LayoutVisitor(fontMetrics);
    theDrawing = (DrawingTree) theAST.visit(layout, null);
    theDrawing.position(new Point(2048, 10));

    frame.setVisible(true);
  }

  public void paintAST (Graphics g) {
    g.setColor(panel.getBackground());
    Dimension d = panel.getSize();
    g.fillRect(0, 0, d.width, d.height);

    if (theDrawing != null) {
      theDrawing.paint(g);
    }
  }
}
