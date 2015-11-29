package com.vid.commons;

import com.vid.overlay.comp.master.JComponentType;

public class ComponentMarker {
	int x, y;
	JComponentType componentType;

	public ComponentMarker(int x, int y, JComponentType componentType) {
		this.x = x;
		this.y = y;
		this.componentType = componentType;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public JComponentType getComponentType() {
		return componentType;
	}

	public void setComponentType(JComponentType componentType) {
		this.componentType = componentType;
	}
}
