package com.vid.commons;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import org.jdom2.Element;

import com.vid.execute.AppLogger;
import com.vid.log.trace.TraceLog;
import com.vid.overlay.comp.Jcomp.SpeechBubble.SpeechBubbleImageType;
import com.vid.overlay.comp.Jcomp.SpotLight.Link_type;
import com.vid.overlay.comp.Scomp.PaintStaticComponent.ArrowDirection;

public class Helper {

	private static final TraceLog logger = AppLogger.getTraceLog();

	public static String setTotalTime(long millis) {
		String s = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		return s;
	}

	public static Object parseParameter(Class<?> pType, Element parameter) {

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
				else
					newInstance = Fonts.getFont(parameter.getTextTrim(), Font.PLAIN, 18);
			} else if (pType.getCanonicalName().equalsIgnoreCase(Image.class.getCanonicalName())) {
				newInstance = new ImageIcon(Helper.class.getResource(parameter.getTextTrim())).getImage();
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

}
