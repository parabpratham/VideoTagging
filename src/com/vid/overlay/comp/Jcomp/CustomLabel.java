package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.vid.overlay.comp.master.SHAPE_TYPE;

public class CustomLabel extends CustomComponent {

	private static final long serialVersionUID = -2906663949003876551L;

	private String displayString;

	private Color displayStringColor;

	public CustomLabel() {
		super();
		setOpaque(false);
	}

	/**
	 * @param startX
	 * @param startY
	 * @param width
	 * @param height
	 * @param bgColor
	 * @param displayString
	 * @param displayStringColor
	 * @param hoverString
	 */
	public CustomLabel(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, String hoverString) {

		super(startX, startY, width, height, hoverString);

		setShpe(SHAPE_TYPE.RECTANGLE);
		setBounds(startX, startY, width, height);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
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

		g2.setPaint(Color.black);
		g2.setFont(getFont());
		// TODO edit the starting position so that always center of the label
		// box
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

}
