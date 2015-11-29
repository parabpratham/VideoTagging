package com.vid.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.vid.commons.ComponentMarker;
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
import com.vid.overlay.comp.Scomp.PaintStaticComponent.ArrowDirection;
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.JComponentType;

public class CustomOverlayTest extends JWindow {

	private static final long serialVersionUID = -730047965056443654L;

	private int startTime;
	private int endTime;

	private int i;
	private Map<String, Map<String, Class<?>[]>> classMethods;
	private List<CustomJComponent> compList;
	private static OverlayLog logger = AppLogger.getOverlayLog();

	public CustomOverlayTest(Window owner, int startTime, int endTime, int i) {

		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		AWTUtilities.setWindowOpaque(this, false);
		setLayout(null);
		classMethods = new HashMap<>();
		compList = new ArrayList<>();
		this.setStartTime(startTime);
		this.setEndTime(endTime);

		this.i = i;
		// saddJComponents();
		xmlQuery(
				"k:/Install/Study/Programming/SpringWorkspace/VideoPlayer/src/resources/overlay_xml/CustomOverlay.xml");
		addJComponentsFromCompList();
	}

	public CustomOverlayTest(Window owner, Map<Integer, ComponentMarker> compMap) {
		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		AWTUtilities.setWindowOpaque(this, false);
		setLayout(null);
		addJcompList(compMap);
	}


	
	private void addJcompList(Map<Integer, ComponentMarker> compMap) {
		Set<Integer> keySet = compMap.keySet();
		for (int key : keySet) {
			ComponentMarker componentMarker = compMap.get(key);
			JComponentType jComponentType = componentMarker.getComponentType();
			System.out
					.println(componentMarker.getX() + " " + componentMarker.getY() + " " + " " + jComponentType.name());
			addJComponents(key, componentMarker.getX(), componentMarker.getY(), jComponentType);
		}

	}

	private void addJComponentsFromCompList() {
		for (CustomJComponent component : compList) {
			if (component == null)
				continue;
			System.out.println(component.getjComponentType() + " " + component.getId());
			addCustomComponent(component);
		}
	}

	private Map<String, Class<?>[]> addToMethodMap(String className) {

		if (getClassMethods().get(className) == null) {
			logger.trace(className);
			Map<String, Class<?>[]> methodParameterMap = new HashMap<String, Class<?>[]>();
			try {
				Method[] methods = Class.forName(className).newInstance().getClass().getMethods();
				for (Method method : methods) {
					if (method.getName().startsWith("set")) {
						methodParameterMap.put(method.getName(), method.getParameterTypes());
					}
				}
				getClassMethods().put(className, methodParameterMap);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return getClassMethods().get(className);
	}

	private void xmlQuery(String fileName) {

		try {

			File inputFile = new File(fileName);
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(inputFile);
			Element classElement = document.getRootElement();
			List<Element> annotations = classElement.getChildren("annotation");
			for (Element annotation : annotations) {
				String id = annotation.getAttributeValue("id");
				String className = annotation.getAttributeValue("type");
				String comp_type = annotation.getChildTextTrim("comp_type");
				logger.trace(" Comp_type " + comp_type);
				Element parameters = annotation.getChild("parameters");

				Map<String, Class<?>[]> addToMethodMap = addToMethodMap(className);
				if (parameters == null)
					continue;

				Object component = Class.forName(className).newInstance();
				if (COMPONENT_TYPE.getFromName(comp_type) == COMPONENT_TYPE.JCOMPONENT) {
					((CustomJComponent) component).setId(id);
					try {
						for (Element parameter : parameters.getChildren()) {
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
						compList.add((CustomJComponent) component);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addJComponents(int key, int startX, int startY, JComponentType componentType) {
		if (componentType == JComponentType.CUSTOM_LABEL) {
			CustomLabel label = new CustomLabel(startX, startY, 300, 40, SupportedColors.DEFAULT_BGCOLOR,
					"Write text in adjucent window", SupportedColors.DEFAULT_FONTCOLOR, Fonts.getAppFont(), "", true);
			label.setId("" + key);
			addCustomComponent(label);
		} else if (componentType == JComponentType.SPEECH_BUBLE) {
			SpeechBubble speechBubble = new SpeechBubble(startX, startY, 300, 40, SupportedColors.DEFAULT_BGCOLOR,
					"Write text in adjucent window", SupportedColors.DEFAULT_FONTCOLOR, Fonts.getAppFont(),
					SpeechBubbleImageType.BLACK, "", true);
			addCustomComponent(speechBubble);
		}
	}

	private void addJComponents() {

		Font font = Fonts.getAppFont();

		CustomLabel label = new CustomLabel(150, 200, 300, 40, new SupportedColors(Color.darkGray, 50),
				"Hi started playing", new SupportedColors(Color.yellow, 50), font, "Hover", false);
		addCustomComponent(label);

		ImageIcon icon = new ImageIcon(getClass().getResource("/icons/close-circle-512.png"));
		SpotLight lightComp_same = new SpotLight(0, 120, 100, 100, new SupportedColors(Color.red, 50), "Seek",
				getBackground(), font, icon.getImage(), "", Link_type.LINK_TO_SAME_VIDEO, (long) 50000, false);
		addCustomComponent(lightComp_same);

		SpotLight lightComp_diff = new SpotLight(120, 120, 100, 100, new SupportedColors(Color.blue, 50), "next video",
				getBackground(), font, icon.getImage(), "", Link_type.LINK_TO_OTHER_VIDEO,
				"file:///C:/Users/hp/Desktop/elan-example1.mpg", (long) 50000, false);
		addCustomComponent(lightComp_diff);

		SpotLight lightComp_web = new SpotLight(240, 120, 100, 100, new SupportedColors(Color.GREEN, 50), "Web",
				getBackground(), font, null, "", Link_type.LINK_TO_OTHER_WEB_PAGE, "http://www.google.com", false);
		addCustomComponent(lightComp_web);

		SpeechBubble speechBubble = new SpeechBubble(360, 120, 160, 40, new SupportedColors(Color.red, 0),
				"This is speech Bubble", new SupportedColors(Color.RED, 0), new Font(Fonts.CORBEL, Font.ITALIC, 10),
				SpeechBubbleImageType.BLACK, "", false);
		addCustomComponent(speechBubble);

		CustomEntireVideoComment vc = new CustomEntireVideoComment(50, new SupportedColors(Color.white, 50),
				"Custom Entire Video Comment", new SupportedColors(Color.red, 20),
				new Font(Fonts.AGENCY_FB, Font.ITALIC, 18), "Hover", false);
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
		// logger.trace("added " + comp.getCloseButton());

		if (comp.isResizeble()) {
			add(comp.getResizeEast());
			add(comp.getResizeDown());
			add(comp.getResizeCross());
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
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

		PaintStaticComponent.drawRectangle(g2, Color.white, 100, 550, 100, 100, true);
		PaintStaticComponent.drawArrow(g2, Color.BLUE, 100, 500, 50, ArrowDirection.LEFT, false);
		PaintStaticComponent.drawString(g, Color.red, "Heavyweight overlay test", 100, 500, Fonts.getAppFont());
		PaintStaticComponent.drawImage(g2, Color.black,
				new ImageIcon(getClass().getResource("/icons/camera.png")).getImage(), 80, 500);

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

	public Map<String, Map<String, Class<?>[]>> getClassMethods() {
		return classMethods;
	}

	public void setClassMethods(Map<String, Map<String, Class<?>[]>> classMethods) {
		this.classMethods = classMethods;
	}

	public List<CustomJComponent> getCompList() {
		return compList;
	}

	public void setCompList(List<CustomJComponent> compList) {
		this.compList = compList;
	}

}
