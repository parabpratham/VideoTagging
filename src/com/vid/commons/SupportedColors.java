package com.vid.commons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.color.ColorSpace;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class SupportedColors extends Color {

	private static final long serialVersionUID = -9153251567085020794L;

	public final static SupportedColors DEFAULT_BGCOLOR = new SupportedColors(Color.black, 80);

	public final static SupportedColors DEFAULT_FONTCOLOR = new SupportedColors(Color.white, 80);

	public SupportedColors(ColorSpace cspace, float[] components, float alpha) {
		super(cspace, components, alpha);
	}

	public SupportedColors(Color color, int alpha) {
		super(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	public SupportedColors(float r, float g, float b, float a) {
		super(r, g, b, a);
	}

	public SupportedColors(float r, float g, float b) {
		super(r, g, b);
	}

	public SupportedColors(int rgba, boolean hasalpha) {
		super(rgba, hasalpha);
	}

	public SupportedColors(int r, int g, int b) {
		super(r, g, b);
	}

	public SupportedColors(int rgb) {
		super(rgb);
	}

	public SupportedColors(int r, int g, int b, int a) {
		super(r, g, b, a);
	}

	public static JComboBox getColorToolBar() {
		String[] test = { " ", " ", " ", " ", " ", " " };
		JComboBox colorBox = new JComboBox(test);
		colorBox.setMaximumRowCount(5);
		colorBox.setPreferredSize(new Dimension(50, 20));
		colorBox.setRenderer(new ColorListRenderer());
		return colorBox;
	}

}
