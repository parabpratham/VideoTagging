package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.play.CustomVideoPlayer;

public class CustomEntireVideoComment extends CustomJComponent {

	private static final long serialVersionUID = 8321521457139629266L;

	private String displayString;

	private Color displayStringColor;

	public CustomEntireVideoComment(int height, Color bgColor, String displayString, Color displayStringColor,
			Font font, String hoverString) {

		super(0, 0, CustomVideoPlayer.getVideosurface().getWidth() - 20, height, hoverString);
		setShpe(SHAPE_TYPE.ROUNDED_RECTANGLE);
		setBounds(0, 0, getWidth(), height);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setFont(font);
	}

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
