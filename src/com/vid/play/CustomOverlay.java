package com.vid.play;

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
import com.vid.commons.SupportedColors;
import com.vid.overlay.comp.Jcomp.CustomComponent;
import com.vid.overlay.comp.Jcomp.CustomLabel;
import com.vid.overlay.comp.Jcomp.SpotLight;
import com.vid.overlay.comp.Jcomp.SpotLight.Link_type;

public class CustomOverlay extends JWindow {

	private static final long serialVersionUID = -730047965056443654L;

	private int startTime;
	private int endTime;

	private int i;

	public CustomOverlay(Window owner, int startTime, int endTime, int i) {

		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		AWTUtilities.setWindowOpaque(this, false);
		setLayout(null);

		this.setStartTime(startTime);
		this.setEndTime(endTime);

		addJComponents();

		this.i = i;

	}

	private void addJComponents() {

		CustomLabel label = new CustomLabel(150, 200, 300, 40, new SupportedColors(Color.darkGray, 50),
				"Hi started playing", Color.black, "Hover");
		addCustomComponent(label);

		ImageIcon icon = new ImageIcon(getClass().getResource("/icons/close-circle-512.png"));
		SpotLight lightComp_same = new SpotLight(0, 20, 100, 100, new SupportedColors(Color.red, 50), "Seek",
				getBackground(), icon.getImage(), "", Link_type.LINK_TO_SAME_VIDEO, 50000);
		addCustomComponent(lightComp_same);

		SpotLight lightComp_diff = new SpotLight(120, 20, 100, 100, new SupportedColors(Color.red, 50), "next video",
				getBackground(), icon.getImage(), "", Link_type.LINK_TO_OTHER_VIDEO,
				"file:///C:/Users/hp/Desktop/elan-example1.mpg", 50000);
		addCustomComponent(lightComp_diff);

		SpotLight lightComp_web = new SpotLight(240, 20, 100, 100, new SupportedColors(Color.red, 50), "Web",
				getBackground(), null, "", Link_type.LINK_TO_OTHER_WEB_PAGE, "http://www.google.com");
		addCustomComponent(lightComp_web);

	}

	public void addCustomComponent(CustomComponent comp) {
		add(comp);

		if (comp != null && comp instanceof SpotLight) {
			add(((SpotLight) comp).getTextArea());
			System.out.println("added" + comp);
		}
		add(comp.getCloseButton());
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

		g.setColor(Color.white);
		g.drawRect(100, 150, 100, 100);

		g.setFont(new Font("Sans", Font.BOLD, 32));
		g.drawString("Heavyweight overlay test", 100, 300);

	}

	private void draw2(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g.setColor(Color.red);
		g.drawRect(200, 250, 100, 100);

		g.setFont(new Font("Sans", Font.BOLD, 32));
		g.drawString("Heavyweight overlay test", 100, 100);

		addJComponents();

	}
}
