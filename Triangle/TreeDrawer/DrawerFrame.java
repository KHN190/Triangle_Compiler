/*
 * @(#)DrawerFrame.java                        2.1 2003/10/07
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

class DrawerFrame extends JFrame {
  public DrawerFrame (JPanel panel) {
    setSize(300, 200);
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension d = tk.getScreenSize();
    int screenHeight = d.height;
    int screenWidth = d.width;
    setTitle("Triangle Compiler Abstract Syntax Tree");
    setSize(screenWidth / 2, screenHeight / 2);
    setLocation(screenWidth / 4, screenHeight / 4);
    // Image img = tk.getImage("icon.gif");
    // setIconImage(img);

    addWindowListener(
      new WindowAdapter() {
        public void windowClosing (WindowEvent e) {
      	  System.exit(0);
        }
      }
    );
    Container contentPane = getContentPane();
    contentPane.add(new JScrollPane(panel));
  }
}