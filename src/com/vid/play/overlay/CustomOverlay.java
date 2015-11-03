package com.vid.play.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

import org.jdom2.Element;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.vid.commons.Fonts;
import com.vid.commons.Helper;
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
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.play.overlay.OverlayFactory.CustomOverlayMarker;
import com.vid.play.overlay.XMLJDomParser.Annotation;

public class CustomOverlay extends JWindow {

	private static final long serialVersionUID = -730047965056443654L;

	private int id;

	private int startTime;
	private int endTime;

	private boolean hasBeenDisplayed;

	private Map<String, Map<String, Class<?>[]>> classMethods;
	private Map<Integer, Object> jcompMap;// map for annotation_id vs
											// generated custom comp
	private CustomOverlayMarker customOverlayMarker;
	private static OverlayLog logger = AppLogger.getOverlayLog();

	public CustomOverlay(Window owner, CustomOverlayMarker customOverlayMarker) {
		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		AWTUtilities.setWindowOpaque(this, false);
		setLayout(null);

		classMethods = new HashMap<>();
		jcompMap = new HashMap<>();
		hasBeenDisplayed = false;

		this.setStartTime(customOverlayMarker.getStartTime());
		this.setEndTime(customOverlayMarker.getEndTime());

		this.customOverlayMarker = customOverlayMarker;

		addJComponentsFromCustomOverlayMarker();
	}

	private void addJComponentsFromCustomOverlayMarker() {

		List<Annotation> annotations = customOverlayMarker.getAnnotations();

		for (Annotation annotation : annotations) {

			if (jcompMap.get(annotation.getId()) != null) {
				addCustomComponent(jcompMap.get(annotation.getId()));
				System.out.println("From map called");
			} else {
				Map<String, Class<?>[]> addToMethodMap = Helper.addToMethodMap(annotation.getClassName());
				if (annotation.getComponent_TYPE() == COMPONENT_TYPE.JCOMPONENT) {
					try {
						Object component = Class.forName(annotation.getClassName()).newInstance();
						for (Element parameter : annotation.getParameters().getChildren()) {
							Method method = component.getClass().getMethod("set" + parameter.getName(),
									addToMethodMap.get("set" + parameter.getName()));
							Class<?>[] pType = method.getParameterTypes();
							for (int i = 0; i < pType.length; i++) {
								Object obj = Helper.parseParameter(pType[i], parameter);
								if (obj != null) {
									method.invoke(component, obj);
								}
							}
						}

						Method method1 = component.getClass().getDeclaredMethod("defineParameter");
						method1.setAccessible(true);
						method1.invoke(component, null);

						jcompMap.put(annotation.getId(), component);
						addCustomComponent(component);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void addJComponents() {

		Font font = Fonts.getAppFont();

		CustomLabel label = new CustomLabel(150, 200, 300, 40, new SupportedColors(Color.darkGray, 50),
				"Hi started playing", new SupportedColors(Color.yellow, 50), font, "Hover");
		addCustomComponent(label);

		ImageIcon icon = new ImageIcon(getClass().getResource("/icons/close-circle-512.png"));
		SpotLight lightComp_same = new SpotLight(0, 120, 100, 100, new SupportedColors(Color.red, 50), "Seek",
				getBackground(), font, icon.getImage(), "", Link_type.LINK_TO_SAME_VIDEO, (long) 50000);
		addCustomComponent(lightComp_same);

		SpotLight lightComp_diff = new SpotLight(120, 120, 100, 100, new SupportedColors(Color.blue, 50), "next video",
				getBackground(), font, icon.getImage(), "", Link_type.LINK_TO_OTHER_VIDEO,
				"file:///C:/Users/hp/Desktop/elan-example1.mpg", (long) 50000);
		addCustomComponent(lightComp_diff);

		SpotLight lightComp_web = new SpotLight(240, 120, 100, 100, new SupportedColors(Color.GREEN, 50), "Web",
				getBackground(), font, null, "", Link_type.LINK_TO_OTHER_WEB_PAGE, "http://www.google.com");
		addCustomComponent(lightComp_web);

		SpeechBubble speechBubble = new SpeechBubble(360, 120, 160, 40, new SupportedColors(Color.red, 0),
				"This is speech Bubble", new SupportedColors(Color.RED, 0), new Font(Fonts.CORBEL, Font.ITALIC, 10),
				SpeechBubbleImageType.BLACK, "");
		addCustomComponent(speechBubble);

		CustomEntireVideoComment vc = new CustomEntireVideoComment(50, new SupportedColors(Color.white, 50),
				"Custom Entire Video Comment", new SupportedColors(Color.red, 20),
				new Font(Fonts.AGENCY_FB, Font.ITALIC, 18), "Hover");
		addCustomComponent(vc);

	}

	public void addCustomComponent(Object comp) {
		if (comp instanceof CustomJComponent) {
			logger.trace("added " + comp);
			CustomJComponent comp1 = (CustomJComponent) comp;
			add(comp1);
			if (comp1 != null && comp1 instanceof SpotLight) {
				add(((SpotLight) comp1).getTextPane());
				logger.trace("added " + ((SpotLight) comp).getTextPane());
			} else if (comp1 != null && comp1 instanceof SpeechBubble) {
				add(((SpeechBubble) comp1).getTextPane());
				logger.trace("added " + ((SpeechBubble) comp).getTextPane());
			}
			add(comp1.getCloseButton());
			logger.trace("added " + comp1.getCloseButton());
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// System.out.println(this.getClass().getName() + " " +
		// this.getStartTime() + ": paint called " + this.hashCode());
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		addScomponents(g2);

	}

	private void addScomponents(Graphics2D g2) {
		List<Annotation> annotations = customOverlayMarker.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.getComponent_TYPE() == COMPONENT_TYPE.SHAPE) {
				try {
					SHAPE_TYPE shape_TYPE = annotation.getShape_type();
					PaintStaticComponent.drawComponent(g2, shape_TYPE, annotation.getParameters());

				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
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

	public Map<String, Map<String, Class<?>[]>> getClassMethods() {
		return classMethods;
	}

	public void setClassMethods(Map<String, Map<String, Class<?>[]>> classMethods) {
		this.classMethods = classMethods;
	}

	public boolean isHasBeenDisplayed() {
		return hasBeenDisplayed;
	}

	public void setHasBeenDisplayed(boolean hasBeenDisplayed) {
		this.hasBeenDisplayed = hasBeenDisplayed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
