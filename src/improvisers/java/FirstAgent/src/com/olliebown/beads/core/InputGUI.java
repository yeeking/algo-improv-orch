package com.olliebown.beads.core;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class InputGUI extends Panel implements KeyListener {
	
	public static class DefaultSongPart extends SongPart {

		public DefaultSongPart(AudioContext context, String name) {
			super(context, 0, name);
		}

		@Override
		public void calculateBuffer() {
		}

	}
	
	public class Repainter extends UGen {

		public Repainter(AudioContext context) {
			super(context);
			context.getRoot().addDependent(this);
		}

		@Override
		public void calculateBuffer() {
			repaint();
		}
	}
	
	private SongPart root;
	private SongPart focus;
	
	public InputGUI(SongPart root) {
		this.root = root;
		focus = root;
		focus.setFocus(true);
	}
	
	public void runInWindow(AudioContext ac) {
		linkToContext(ac);
		Frame frame = new Frame();
//		ScrollPane sp = new ScrollPane();
//		sp.add(this);
//		frame.add(sp);
		frame.add(this);
		frame.addKeyListener(this);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
	}
	
	public void linkToContext(AudioContext ac) {
		Repainter r = new Repainter(ac);
	}
	
	public void paint(Graphics g) {
		int finalHeight = root.paint(g, 5, 5, 900, 900) + 1;
		g.setColor(Color.WHITE);
		g.fillRect(5, finalHeight, 900 - 5, 900 - finalHeight);
	}
	

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			switchFocusToSibling(false);
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			switchFocusToSibling(true);
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			switchFocusToParent();
		} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			switchFocusToChild();
		} else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			deleteFocusAndSwitchToSibling();
		} else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			focus.setActive(true);
		} else focus.keyPressed(e);
		repaint();
	}
	
	private void deleteFocusAndSwitchToSibling() {
		SongPart toBeDeleted = focus;
		SongPart parent = focus.parent;
		if(focus.parent != null) {
			switchFocusToSibling(true);
			if(parent.children.size() != 0) parent.removeChild(toBeDeleted);
		}
	}
	
	private void switchFocusToParent() {
		if(focus.parent != null) {
//			focus.parent.lastChildIndex = focus.parent.children.indexOf(focus);
			focus.setFocus(false);
			focus = focus.parent;
			focus.setFocus(true);
		}
	}
	
	private void switchFocusToChild() {
		SongPart newFocus = focus.getChildFocus();
		if(newFocus != null) {
			focus.setFocus(false);
			focus = newFocus;
			focus.setFocus(true);
		}
	}
	
	private void switchFocusToSibling(boolean down) {
		if(focus.parent != null) {
			int childIndex = focus.parent.children.indexOf(focus);
//			focus.parent.lastChildIndex = childIndex;
			if(down) childIndex = (childIndex + 1) % focus.parent.children.size();
			else childIndex = (focus.parent.children.size() + childIndex - 1) % focus.parent.children.size();
			SongPart newFocus = focus.parent.children.get(childIndex);
			focus.setFocus(false);
			focus = newFocus;
			focus.setFocus(true);
		}
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			focus.setActive(false);
		}
	}

	public void keyTyped(KeyEvent e) {
	}
	

	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512);
		SongPart p1 = new DefaultSongPart(ac, "p1");
		SongPart p2 = new DefaultSongPart(ac, "p2");
		SongPart p3 = new DefaultSongPart(ac, "p3");
		SongPart p4 = new DefaultSongPart(ac, "p4");
		p1.addChild(p2);
		p2.addChild(p3);
		p1.addChild(p4);
		InputGUI ig = new InputGUI(p1);
		ig.runInWindow(ac);
	}
	
}
