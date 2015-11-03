package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;

import com.vid.overlay.comp.master.JComponentType;

public class SpeechBubble extends CustomJComponent implements JCompoWithTextPane {

	private static final long serialVersionUID = -1057656871481790644L;

	private Color displayStringColor;

	private String displayString;

	private Image componentImage;

	private SpeechBubbleImageType imageType;

	public SpeechBubble() {
		super();
	}

	public SpeechBubble(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, Font font, SpeechBubbleImageType imageType, String hoverString) {

		super(startX, startY, width, height, hoverString);
		setImageType(imageType);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setFont(font);
	}

	@Override
	protected void defineParameter() {
		super.defineParameter();
		setjComponentType(JComponentType.SPEECH_BUBLE);

		if (getImageType() == SpeechBubbleImageType.BLACK)
			setComponentImage(new ImageIcon(getClass().getResource("/icons/SpeechBubble_black.png")).getImage());
		else
			setComponentImage(new ImageIcon(getClass().getResource("/icons/SpeechBubble_white.png")).getImage());

		setChildTextPane();

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		//System.out.println(this.getClass().getName() + " " + CustomJComponent.class.getName() + ": paint called ");
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g2.setPaint(getBgColor());
		g2.fillRect(getX()+20, getY()+0, getWidth() - 1, getHeight() - 1);
		if (getComponentImage() != null)
			g2.drawImage(getComponentImage(), getX(), getY(), getWidth(), getHeight(), null);
		else
			logger.trace("No component found");

		//System.out.println(this.getClass().getName() + " " + SpeechBubble.class.getName() + ": paint called ");

	}

	protected void paintComponentOld(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		System.out.println(this.getClass().getName() + " " + CustomJComponent.class.getName() + ": paint called ");
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g2.setPaint(getBgColor());
		g2.fillRect(20, 0, getWidth() - 1, getHeight() - 1);
		if (getComponentImage() != null)
			g2.drawImage(getComponentImage(), 0, 0, getWidth(), getHeight(), null);
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

	public void setComponentImage(Image componentImage) {
		this.componentImage = componentImage;
	}

	public enum SpeechBubbleImageType {
		BLACK, WHITE;

		public static SpeechBubbleImageType getFromName(String name) {
			SpeechBubbleImageType[] values = values();
			for (SpeechBubbleImageType speechBubbleImageType : values) {
				if (speechBubbleImageType.name().equalsIgnoreCase(name))
					return speechBubbleImageType;
			}
			return null;
		}
	}

	public void setChildTextPane() {
		super.setTextPane();
		setTextPaneBounds(getStartX() + 20, getStartY(), getWidth() - 20, getHeight());
	}

	public SpeechBubbleImageType getImageType() {
		return imageType;
	}

	public void setImageType(SpeechBubbleImageType imageType) {
		this.imageType = imageType;
	}

}
