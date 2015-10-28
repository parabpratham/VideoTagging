package com.vid.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.vid.commons.Fonts;
import com.vid.commons.SupportedColors;
import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.overlay.comp.Jcomp.CustomEntireVideoComment;
import com.vid.overlay.comp.Jcomp.CustomJComponent;
import com.vid.overlay.comp.Jcomp.CustomLabel;
import com.vid.overlay.comp.Jcomp.SpeechBubble;
import com.vid.overlay.comp.Jcomp.SpeechBubble.SpeechBubbleImageType;
import com.vid.overlay.comp.Jcomp.SpotLight;
import com.vid.overlay.comp.Jcomp.SpotLight.Link_type;
import com.vid.overlay.comp.Scomp.PaintStaticComponent;
import com.vid.overlay.comp.Scomp.PaintStaticComponent.ArrowDirection;

public class CustomOverlayTest extends JWindow {

	private static final long serialVersionUID = -730047965056443654L;

	private int startTime;
	private int endTime;

	private int i;

	private static final OverlayLog logger = AppLogger.getOverlayLog();

	public CustomOverlayTest(Window owner, int startTime, int endTime, int i) {

		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		AWTUtilities.setWindowOpaque(this, false);
		setLayout(null);

		this.setStartTime(startTime);
		this.setEndTime(endTime);

		this.i = i;

		addJComponents();
	}

	private void addJComponents() {

		Font font = Fonts.getAppFont();

		CustomLabel label = new CustomLabel(150, 200, 300, 40, new SupportedColors(Color.darkGray, 50),
				"Hi started playing", new SupportedColors(Color.yellow, 50), font, "Hover");
		addCustomComponent(label);

		ImageIcon icon = new ImageIcon(getClass().getResource("/icons/close-circle-512.png"));
		SpotLight lightComp_same = new SpotLight(0, 20, 100, 100, new SupportedColors(Color.red, 50), "Seek",
				getBackground(), font, icon.getImage(), "", Link_type.LINK_TO_SAME_VIDEO, (long) 50000);
		addCustomComponent(lightComp_same);

		SpotLight lightComp_diff = new SpotLight(120, 20, 100, 100, new SupportedColors(Color.blue, 50), "next video",
				getBackground(), font, icon.getImage(), "", Link_type.LINK_TO_OTHER_VIDEO,
				"file:///C:/Users/hp/Desktop/elan-example1.mpg", (long) 50000);
		addCustomComponent(lightComp_diff);

		SpotLight lightComp_web = new SpotLight(240, 20, 100, 100, new SupportedColors(Color.GREEN, 50), "Web",
				getBackground(), font, null, "", Link_type.LINK_TO_OTHER_WEB_PAGE, "http://www.google.com");
		addCustomComponent(lightComp_web);

		SpeechBubble speechBubble = new SpeechBubble(360, 120, 160, 40, new SupportedColors(Color.red, 0), "Web",
				new SupportedColors(Color.RED, 0), new Font(Fonts.CORBEL, Font.ITALIC, 10), SpeechBubbleImageType.BLACK,
				"");
		addCustomComponent(speechBubble);

		CustomEntireVideoComment vc = new CustomEntireVideoComment(50, new SupportedColors(Color.darkGray, 50),
				"Hi started playing", new SupportedColors(Color.green, 20), new Font(Fonts.AGENCY_FB, Font.ITALIC, 18),
				"Hover");
		addCustomComponent(vc);

	}

	public void addCustomComponent(CustomJComponent comp) {
		logger.trace("added " + comp);
		add(comp);
		if (comp != null && comp instanceof SpotLight) {
			add(((SpotLight) comp).getTextPane());
			logger.trace("added " + ((SpotLight) comp).getTextPane());
		} else if (comp != null && comp instanceof SpeechBubble) {
			add(((SpeechBubble) comp).getTextPane());
			logger.trace("added " + ((SpeechBubble) comp).getTextPane());
		}
		add(comp.getCloseButton());
		logger.trace("added " + comp.getCloseButton());
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (i == 0)
			draw1(g);
		if (i == 1)
			draw2(g);
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public void draw1(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		PaintStaticComponent.drawRectangle(g2, Color.white, 100, 150, 100, 100, true);
		PaintStaticComponent.fillArrow(g2, Color.BLUE, 80, 292, 50, ArrowDirection.LEFT);
		PaintStaticComponent.drawString(g, Color.red, "Heavyweight overlay test", 100, 300, Fonts.getAppFont());
		PaintStaticComponent.drawImage(g2, Color.black,
				new ImageIcon(getClass().getResource("/icons/camera.png")).getImage(), 400, 200);

	}

	private void draw2(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g.setColor(Color.red);
		g.drawRect(200, 250, 100, 100);

		g.setFont(new Font("Sans", Font.BOLD, 32));
		g.drawString("Heavyweight overlay test", 100, 100);

	}
}
