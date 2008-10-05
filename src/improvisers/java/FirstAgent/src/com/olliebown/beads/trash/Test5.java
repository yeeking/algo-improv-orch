
package com.olliebown.beads.trash;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

public class Test5 implements KeyListener {

    public static void main(String[] args) {
        JFrame frame = new JFrame("test5");
        frame.setSize(new Dimension(500,500));
        frame.setVisible(true);
        frame.addKeyListener(new Test5());
    }

    public void keyTyped(KeyEvent arg0) {
        System.out.println("x");
    }

    public void keyPressed(KeyEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void keyReleased(KeyEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
