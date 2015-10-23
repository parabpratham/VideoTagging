package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;

import com.vid.commons.SupportedColors;

public class SpeechBubble extends CustomJComponent {

	private static final long serialVersionUID = -1057656871481790644L;

	private Color displayStringColor;

	private String displayString;

	private Image componentImage;

	private JTextPane textPane;

	public SpeechBubble(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, SpeechBubbleImageType imageType, String hoverString) {

		super(startX, startY, width, height, hoverString);
		if (imageType == SpeechBubbleImageType.BLACK)
			setComponentImage(new ImageIcon(getClass().getResource("/icons/SpeechBubble_black.png")).getImage());
		else
			setComponentImage(new ImageIcon(getClass().getResource("/icons/SpeechBubble_white.png")).getImage());
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setTextPane();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g2.setPaint(getBgColor());
		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.fillRect(0, 0, getWidth(), getHeight());
		if (getComponentImage() != null)
			g2.drawImage(getComponentImage(), 0, 0, getWidth() - 1, getHeight() - 1, null);
		else
			logger.trace("No component found");

	}

	public Color getDisplayStringColor() {
		return displayStringColor;
	}

	public void setDisplayStringColor(Color displayStringColor) {
		this.displayStringColor = displayStringColor;
	}

	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public Image getComponentImage() {
		return componentImage;
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public void setTextPane() {
		textPane = new JTextPane();
		textPane.setBackground(getBgColor());
		// textPane.setCaretColor(getDisplayStringColor());
		textPane.setText(getDisplayString());
		textPane.setBounds(getStartX() + 20, getStartY(), getWidth(), getHeight());
	}

	public void setComponentImage(Image componentImage) {
		this.componentImage = componentImage;
	}

	public enum SpeechBubbleImageType {
		BLACK, WHITE;
	}

}
