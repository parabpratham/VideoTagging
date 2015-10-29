package com.vid.play.overlay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.vid.commons.Fonts;
import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.overlay.comp.Jcomp.CustomJComponent;
import com.vid.overlay.comp.Jcomp.SpeechBubble;
import com.vid.overlay.comp.Jcomp.SpotLight;
import com.vid.overlay.comp.Scomp.CustomStaticComponent;
import com.vid.overlay.comp.Scomp.PaintStaticComponent;
import com.vid.overlay.comp.Scomp.PaintStaticComponent.ArrowDirection;
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.JComponentType;

public class CustomOverlay extends JWindow {

	private static final long serialVersionUID = -730047965056443654L;

	private int startTime;
	private int endTime;
	private List<CustomJComponent> jComponents;
	private List<CustomStaticComponent> sComponents;

	private boolean hasBeenDisplayed;

	private static final OverlayLog logger = AppLogger.getOverlayLog();

	public CustomOverlay(Window owner, int startTime, int endTime, List<CustomJComponent> jComponents,
			List<CustomStaticComponent> sComponents) {
		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		AWTUtilities.setWindowOpaque(this, false);
		setLayout(null);
		setHasBeenDisplayed(false);
		this.setStartTime(startTime);
		this.setEndTime(endTime);
		setjComponents(jComponents);
		setsComponents(sComponents);
		addJComponents();
		logger.trace("Generated custom overlay " + startTime + " " + endTime + " " + jComponents.size());
	}

	private void addJComponents() {
		for (CustomJComponent component : jComponents) {

			if (component == null)
				continue;

			addCustomComponent(component);
		}
	}

	public void addCustomComponent(CustomJComponent comp) {
		// logger.trace("added " + comp);
		add(comp);
		if (comp.getComponentType() == COMPONENT_TYPE.JCOMPONENT) {
			if (comp.getjComponentType() == JComponentType.SPOT_LIGHT) {
				add(((SpotLight) comp).getTextPane());
				// logger.trace("added " + ((SpotLight) comp).getTextPane());
			} else if (comp.getjComponentType() == JComponentType.SPEECH_BUBLE) {
				add(((SpeechBubble) comp).getTextPane());
				// logger.trace("added " + ((SpeechBubble) comp).getTextPane());
			}
		}
		add(comp.getCloseButton());
		// logger.trace("added " + comp.getCloseButton());
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;
		PaintStaticComponent paintComponents = new PaintStaticComponent();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		int n=0;
		for (CustomStaticComponent sComponent : sComponents) {
			try {
				if (sComponent == null)
					continue;
				paintComponents.drawComponent(g, sComponent, sComponent.getParameter());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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
		PaintStaticComponent.drawArrow(g2, Color.BLUE, 80, 292, 50, ArrowDirection.LEFT, false);
		PaintStaticComponent.drawString(g, Color.red, "Heavyweight overlay test", 100, 300, Fonts.getAppFont());
		PaintStaticComponent.drawImage(g2, Color.black,
				new ImageIcon(getClass().getResource("/icons/camera.png")).getImage(), 400, 200);

	}

	public List<CustomJComponent> getjComponents() {
		return jComponents;
	}

	public void setjComponents(List<CustomJComponent> jComponents) {
		this.jComponents = jComponents;
	}

	public boolean isHasBeenDisplayed() {
		return hasBeenDisplayed;
	}

	public void setHasBeenDisplayed(boolean hasBeenDisplayed) {
		this.hasBeenDisplayed = hasBeenDisplayed;
	}

	public List<CustomStaticComponent> getsComponents() {
		return sComponents;
	}

	public void setsComponents(List<CustomStaticComponent> sComponents) {
		this.sComponents = sComponents;
	}
}
