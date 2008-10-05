package com.olliebown.beads.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import com.olliebown.beads.ugens.Gain;


public abstract class SongPart extends Gain {

	public final static int DEFAULT_INDENT = 20;
	public final static int DEFAULT_WIDTH = 200;
	public final static int DEFAULT_HEIGHT = 20;
	
	protected SongPart parent;
	protected ArrayList<SongPart> children;
	protected boolean focus;
	protected boolean active;
	protected int lastChildIndex;
	protected String name;
	protected int depth;
	protected int boxWidth;
	protected int boxHeight;
	
	public SongPart(AudioContext context, int inouts, String name) {
		super(context, inouts);
		this.name = name;
		children = new ArrayList<SongPart>();
		focus = false;
		active = false;
		parent = null;
		lastChildIndex = 0;
		boxWidth = DEFAULT_WIDTH;
		boxHeight = DEFAULT_HEIGHT;
	}
	
	public void addChild(SongPart sp) {
		children.add(sp);
		sp.parent = this;
//		addInput(sp);			//we don't enforce this
	}
	
	public void removeChild(SongPart sp) {
		children.remove(sp);
		sp.stopGracefully();
	}
	
	public void setFocus(boolean focus) {
		this.focus = focus;
		for(SongPart sp : children) {	//could be recursive in subclass if required
			sp.setFocus(focus);
		}
		if(!focus) setActive(false);
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public int paint(Graphics g, int width, int height, int maxWidth, int maxHeight) {
		g.setColor(focus ? Color.LIGHT_GRAY : Color.WHITE);
		if(active) g.setColor(Color.DARK_GRAY);
		g.fillRect(width, height, maxWidth - width, maxHeight - height);
		g.setColor(Color.BLACK);
		g.drawString(name, width + 5, height + 15);
		int returnHeight = height + this.boxHeight;
		for(SongPart sp : children) {
			returnHeight = sp.paint(g, width + DEFAULT_INDENT, returnHeight, maxWidth, maxHeight);
		}
		g.setColor(Color.BLACK);
		g.drawRect(width, height, maxWidth - width, returnHeight - height);
		g.drawLine(width + 2, height, width + 2, returnHeight);
		System.out.println(name + " " + focus);
		return returnHeight;
	}
	
	public int getBoxWidth() {
		return boxWidth;
	}
	

	public int getBoxHeight() {
		return boxHeight;
	}
	
	public static class DefaultSongPart extends SongPart {

		public DefaultSongPart(AudioContext context, String name) {
			super(context, 0, name);
		}

		@Override
		public void calculateBuffer() {
		}

	}

	public SongPart getChildFocus() {
		if(children.size() == 0) return null;
		return children.get(lastChildIndex);
	}

	public void keyPressed(KeyEvent e) {
	}
	
}


