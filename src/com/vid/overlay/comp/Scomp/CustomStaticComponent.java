package com.vid.overlay.comp.Scomp;

import org.jdom2.Element;

import com.vid.overlay.comp.master.SHAPE_TYPE;

public class CustomStaticComponent {

	private SHAPE_TYPE shape_TYPE;
	private Element parameter;
	private String id;
	
	public CustomStaticComponent() {
	}

	public SHAPE_TYPE getShape_TYPE() {
		return shape_TYPE;
	}

	public void setShape_TYPE(SHAPE_TYPE shape_TYPE) {
		this.shape_TYPE = shape_TYPE;
	}

	public Element getParameter() {
		return parameter;
	}

	public void setParameter(Element parameter) {
		this.parameter = parameter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
