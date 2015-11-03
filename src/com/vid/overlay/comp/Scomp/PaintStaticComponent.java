package com.vid.overlay.comp.Scomp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jdom2.Element;

import com.vid.commons.Fonts;
import com.vid.commons.Helper;
import com.vid.commons.Points;
import com.vid.commons.SupportedColors;
import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.overlay.comp.Jcomp.SpeechBubble.SpeechBubbleImageType;
import com.vid.overlay.comp.Jcomp.SpotLight.Link_type;
import com.vid.overlay.comp.master.SHAPE_TYPE;

public class PaintStaticComponent {

	public static int x = 0;

	private static final String[] shapes = { "Rectangle", "Line", "Oval", "Polygon", "String", "Image", "Arrow",
			"RoundRect" };

	private static OverlayLog logger = AppLogger.getOverlayLog();

	private static Map<String, Class<?>[]> methodMap;

	public PaintStaticComponent() {
	}

	private static void createMethodMap() {
		try {
			
			methodMap = new HashMap<String, Class<?>[]>();
			Method[] methods = PaintStaticComponent.class.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("draw")) {
					methodMap.put(method.getName(), method.getParameterTypes());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void drawComponent(Graphics g, SHAPE_TYPE shape_TYPE, Element parameters)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		if (methodMap == null) {
			createMethodMap();
		}

		String methodName = "draw" + shapes[shape_TYPE.ordinal()];
		Method method = PaintStaticComponent.class.getMethod(methodName, methodMap.get(methodName));
		Class<?>[] pType = method.getParameterTypes();
		Object[] args = new Object[pType.length];
		args[0] = g;
		List<Element> params = parameters.getChildren();
		for (int i = 1; i < pType.length; i++) {
			Object obj = Helper.parseParameter(pType[i], params.get(i - 1));
			args[i] = obj;
		}
		method.invoke(null, args);
	}


	public static void drawComponent(Graphics g, CustomStaticComponent sComponent, Element parameters)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		if (methodMap == null) {
			createMethodMap();
		}

		logger.trace(sComponent + " " + sComponent.getId() + " " + (x++));
		String methodName = "draw" + shapes[sComponent.getShape_TYPE().ordinal()];
		Method method = PaintStaticComponent.class.getMethod(methodName, methodMap.get(methodName));
		Class<?>[] pType = method.getParameterTypes();
		Object[] args = new Object[pType.length];
		args[0] = g;
		List<Element> params = parameters.getChildren();
		for (int i = 1; i < pType.length; i++) {
			Object obj = Helper.parseParameter(pType[i], params.get(i - 1));
			args[i] = obj;
		}
		method.invoke(null, args);
	}

	public static void drawRectangle(Graphics g, Color color, int x, int y, int width, int height, boolean fillArea) {
		g.setColor(color);
		if (fillArea)
			g.fillRect(x, y, width, height);
		else
			g.drawRect(x, y, width, height);
	}

	public static void clearRectangle(Graphics g, Color color, int x, int y, int width, int height) {
		g.setColor(color);
		g.clearRect(x, y, width, height);
	}

	public static void drawString(Graphics g, Color color, String str, int x, int y, Font font) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(str, x, y);
	}

	public static void drawArrow(Graphics g, Color c, int x, int y, int size, ArrowDirection direction,
			boolean fillArea) {
		g.setColor(c);
		Points points = getPoints(x, y, size, direction);
		if (!fillArea)
			g.drawPolygon(points.getxPoints(), points.getyPoints(), points.getSize());
		else
			g.fillPolygon(points.getxPoints(), points.getyPoints(), points.getSize());
	}

	private static Points getPoints(int x, int y, int size, ArrowDirection direction) {

		Points arrowPoint = new Points(7);
		arrowPoint.addPoint(x, y);
		int ln = 5 * size;// length of arrow

		if (direction == ArrowDirection.DOWN) {
			arrowPoint.addPoint(x - (int) ((5.0 / 6) * size), y - (int) (1.0 * ln / 2));
			arrowPoint.addPoint(x - (int) ((1.0 / 2) * size), y - (int) (1.0 * ln / 2));
			arrowPoint.addPoint(x - (int) ((1.0 / 2) * size), y - ln);
			arrowPoint.addPoint(x + (int) ((1.0 / 2) * size), y - ln);
			arrowPoint.addPoint(x + (int) ((1.0 / 2) * size), y - (int) (1.0 * ln / 2));
			arrowPoint.addPoint(x + (int) ((5.0 / 6) * size), y - (int) (1.0 * ln / 2));
		} else if (direction == ArrowDirection.UP) {
			arrowPoint.addPoint(x - (int) ((5.0 / 6) * size), y + (int) (1.0 * ln / 2));
			arrowPoint.addPoint(x - (int) ((1.0 / 2) * size), y + (int) (1.0 * ln / 2));
			arrowPoint.addPoint(x - (int) ((1.0 / 2) * size), y + ln);
			arrowPoint.addPoint(x + (int) ((1.0 / 2) * size), y + ln);
			arrowPoint.addPoint(x + (int) ((1.0 / 2) * size), y + (int) (1.0 * ln / 2));
			arrowPoint.addPoint(x + (int) ((5.0 / 6) * size), y + (int) (1.0 * ln / 2));
		} else if (direction == ArrowDirection.LEFT) {
			arrowPoint.addPoint(x + (int) (1.0 * ln / 2), y - (int) ((5.0 / 6) * size));
			arrowPoint.addPoint(x + (int) (1.0 * ln / 2), y - (int) ((1.0 / 2) * size));
			arrowPoint.addPoint(x + ln, y - (int) ((1.0 / 2) * size));
			arrowPoint.addPoint(x + ln, y + (int) ((1.0 / 2) * size));
			arrowPoint.addPoint(x + (int) (1.0 * ln / 2), y + (int) ((1.0 / 2) * size));
			arrowPoint.addPoint(x + (int) (1.0 * ln / 2), y + (int) ((5.0 / 6) * size));
		} else if (direction == ArrowDirection.RIGHT) {
			arrowPoint.addPoint(x - (int) (1.0 * ln / 2), y - (int) ((5.0 / 6) * size));
			arrowPoint.addPoint(x - (int) (1.0 * ln / 2), y - (int) ((1.0 / 2) * size));
			arrowPoint.addPoint(x - ln, y - (int) ((1.0 / 2) * size));
			arrowPoint.addPoint(x - ln, y + (int) ((1.0 / 2) * size));
			arrowPoint.addPoint(x - (int) (1.0 * ln / 2), y + (int) ((1.0 / 2) * size));
			arrowPoint.addPoint(x - (int) (1.0 * ln / 2), y + (int) ((5.0 / 6) * size));
		}

		return arrowPoint;
	}

	public static void drawLine(Graphics g, Color color, int x1, int y1, int x2, int y2) {
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}

	public static void drawRoundRect(Graphics g, Color color, int x, int y, int width, int height, int arcWidth,
			int arcHeight, boolean fillArea) {
		g.setColor(color);
		if (fillArea)
			g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		else
			g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public static void drawOval(Graphics g, Color color, int x, int y, int width, int height, boolean fillArea) {
		g.setColor(color);
		if (fillArea)
			g.fillOval(x, y, width, height);
		else
			g.drawOval(x, y, width, height);
	}

	public static void drawArc(Graphics g, Color color, int x, int y, int width, int height, int startAngle,
			int arcAngle, boolean fillArea) {
		g.setColor(color);
		if (fillArea)
			g.fillArc(x, y, width, height, startAngle, arcAngle);
		else
			g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public static void drawPolyline(Graphics g, Color color, int[] xPoints, int[] yPoints, int nPoints,
			boolean fillArea) {
		g.setColor(color);
		if (fillArea)
			g.fillPolygon(xPoints, yPoints, nPoints);
		else
			g.drawPolygon(xPoints, yPoints, nPoints);
	}

	public static boolean drawImage(Graphics g, Color c, Image img, int x, int y) {
		g.setColor(c);
		return g.drawImage(img, x, y, null);
	}

	private static boolean drawImages(Graphics g, Color c, Image img, int x, int y, int width, int height) {
		g.setColor(c);
		return g.drawImage(img, x, y, c, null);
	}

	private static boolean drawImages(Graphics g, Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
			int sx2, int sy2) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	private static boolean drawImages(Graphics g, Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
			int sx2, int sy2, Color bgcolor) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, null);
	}

	public enum ArrowDirection {
		UP, DOWN, RIGHT, LEFT;

		public static ArrowDirection getFromName(String name) {
			ArrowDirection[] values = values();
			for (ArrowDirection jcomponent_TYPE : values) {
				if (jcomponent_TYPE.name().equalsIgnoreCase(name))
					return jcomponent_TYPE;
			}
			return null;
		}

	}
}
