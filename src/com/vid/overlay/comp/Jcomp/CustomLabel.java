package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.vid.overlay.comp.master.JComponentType;
import com.vid.overlay.comp.master.SHAPE_TYPE;

public class CustomLabel extends CustomJComponent {

	private static final long serialVersionUID = -2906663949003876551L;

	private String displayString;

	private Color displayStringColor;

	private Font font;

	public CustomLabel() {
		super();
	}

	public CustomLabel(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, Font font, String hoverString) {

		super(startX, startY, width, height, hoverString);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setFont(font);
		defineParameter();
	}

	@Override
	protected void defineParameter() {
		super.defineParameter();
		setjComponentType(JComponentType.CUSTOM_LABEL);
		setShpe(SHAPE_TYPE.ROUNDED_RECTANGLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * 
	 * 
	 * For opacity set BGColor = new (r,g,b,a) a-->visibility
	 * 
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g2.setPaint(getBgColor());
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

		g2.setPaint(getDisplayStringColor());
		g2.setFont(getFont());
		// TODO edit the starting position so that always center of the labelbox
		g2.drawString(getDisplayString(), 10, 16);
	}

	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public Color getDisplayStringColor() {
		return displayStringColor;
	}

	public void setDisplayStringColor(Color displayStringColor) {
		this.displayStringColor = displayStringColor;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		if (font != null)
			this.font = font;
		else
			super.getFont();
	}

}
