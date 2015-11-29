package com.vid.commons;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import org.jdom2.Element;

import com.vid.execute.AppLogger;
import com.vid.log.trace.TraceLog;
import com.vid.matroska.MatroskaContainer;
import com.vid.overlay.comp.Jcomp.SpeechBubble.SpeechBubbleImageType;
import com.vid.overlay.comp.Jcomp.SpotLight.Link_type;
import com.vid.overlay.comp.Scomp.PaintStaticComponent.ArrowDirection;
import com.vid.play.overlay.OverlayFactory;

public class Helper {

	private static final TraceLog logger = AppLogger.getTraceLog();

	private static Map<String, Map<String, Class<?>[]>> classMethods = new HashMap<>();

	/**
	 * @param ClassName
	 * @return MethodParameterMap Map<MethodName, Parameters : Class<?>[]>
	 * 
	 *         <p>
	 *         A sort of cache for the CustomComponent classes setter
	 *         metthods.Stores the method name against the parameters for
	 *         invoking the methods later.
	 *         </p>
	 */
	public static Map<String, Class<?>[]> addToMethodMap(String className) {

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

	public static String setTotalTime(long millis) {
		String s = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		return s;
	}

	/**
	 * @param Class<?>
	 *            pType
	 * @param XMLElement
	 *            parameter
	 * @return InstanceOfTheComponent
	 * 
	 *         Depending upon the component_type generates the appropriate
	 *         instance of the parameters to be passed for invoking the setter
	 *         methods
	 * 
	 */

	public static Object parseParameter(Class<?> pType, Element parameter, MatroskaContainer container) {

		if (parameter.getTextTrim().equalsIgnoreCase("")
				&& !pType.getCanonicalName().equalsIgnoreCase(Font.class.getCanonicalName()))
			return null;

		// System.out.print(pType.getCanonicalName() + " " + parameter.getName()
		// + " " + parameter.getTextTrim() + " ");
		Object newInstance = null;
		try {

			if (pType.getCanonicalName().equalsIgnoreCase(Integer.class.getCanonicalName())) {
				newInstance = Integer.parseInt(parameter.getTextTrim());
			} else if (pType.getCanonicalName().equalsIgnoreCase(String.class.getCanonicalName())) {
				newInstance = parameter.getTextTrim();
			} else if (pType.getCanonicalName().equalsIgnoreCase(Color.class.getCanonicalName())) {
				String[] colorDetails = parameter.getTextTrim().split(",");
				Color color;
				try {
					Field field = Class.forName(Color.class.getCanonicalName()).getField(colorDetails[0]);
					color = (Color) field.get(null);
				} catch (Exception e) {
					color = null; // Not defined
					logger.error("Color not defined " + e.getMessage());
				}
				newInstance = new SupportedColors(color, Integer.parseInt(colorDetails[1]));
			} else if (pType.getCanonicalName().equalsIgnoreCase(Font.class.getCanonicalName())) {

				if (parameter.getTextTrim() == null || parameter.getTextTrim().equalsIgnoreCase(""))
					newInstance = Fonts.getAppFont();
				else {

					String[] parameters = parameter.getTextTrim().split(",");
					// 0 = name, 1=Stylr,2=size
					newInstance = Fonts.getFont(parameters[0], Integer.parseInt(parameters[1]),
							Integer.parseInt(parameters[2]));

				}
			} else if (pType.getCanonicalName().equalsIgnoreCase(Image.class.getCanonicalName())) {
				byte[] dataFile = container.getDataFile(parameter.getTextTrim());
				if (dataFile != null)
					newInstance = new ImageIcon(dataFile).getImage();
			} else if (pType.getCanonicalName().equalsIgnoreCase(Link_type.class.getCanonicalName())) {
				newInstance = Link_type.getFromName(parameter.getTextTrim());
			} else if (pType.getCanonicalName().equalsIgnoreCase(SpeechBubbleImageType.class.getCanonicalName())) {
				newInstance = SpeechBubbleImageType.getFromName(parameter.getTextTrim());
			} else if (pType.getCanonicalName().equalsIgnoreCase(ArrowDirection.class.getCanonicalName())) {
				newInstance = ArrowDirection.getFromName(parameter.getTextTrim());
			} else if (pType.getCanonicalName().equalsIgnoreCase("int")) {
				newInstance = Integer.parseInt("" + parameter.getTextTrim());
			} else if (pType.getCanonicalName().equalsIgnoreCase(boolean.class.getCanonicalName())) {
				newInstance = Boolean.parseBoolean(parameter.getTextTrim());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return newInstance;
	}

	public static Map<String, Map<String, Class<?>[]>> getClassMethods() {
		return classMethods;
	}

	public static void setClassMethods(Map<String, Map<String, Class<?>[]>> classMethods) {
		Helper.classMethods = classMethods;
	}

}
