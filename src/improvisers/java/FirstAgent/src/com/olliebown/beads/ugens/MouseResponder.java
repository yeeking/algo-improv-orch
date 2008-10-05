package com.olliebown.beads.ugens;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.UGen;


public class MouseResponder extends UGen {

	private Point point;
	public float x;
	public float y;
	private int width;
	private int height;
	private Bead listener = null;
	
	public MouseResponder(AudioContext context) {
		super(context);
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Bead getListener() {
		return listener;
	}

	public void setListener(Bead listener) {
		this.listener = listener;
	}

	@Override
	public void calculateBuffer() {
		point = MouseInfo.getPointerInfo().getLocation();
		x = (float)point.x / (float)width;
		y = (float)point.y / (float)height;
		if(listener != null) listener.message(this);
	}

}
