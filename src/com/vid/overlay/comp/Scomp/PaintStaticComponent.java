package com.vid.overlay.comp.Scomp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import com.vid.commons.Points;

public class PaintStaticComponent {

	public static void drawRectangle(Graphics g, Color color, int x, int y, int width, int height, boolean fillArea) {
		if (fillArea)
			fillRect(g, color, x, y, width, height);
		else
			drawRectangle(g, color, x, y, width, height);
	}

	public static void drawRectangle(Graphics g, Color color, int startX, int startY, int width, int height) {
		g.setColor(color);
		g.drawRect(100, 150, 100, 100);
	}

	public static void drawString(Graphics g, Color color, String str, int x, int y, Font font) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(str, x, y);
	}

	public static void drawArrow(Graphics g, Color c, int x, int y, int size, ArrowDirection direction) {
		g.setColor(c);
		Points points = getPoints(x, y, size, direction);
		g.drawPolygon(points.getxPoints(), points.getyPoints(), points.getSize());
	}

	private static Points getPoints(int x, int y, int size, ArrowDirection direction) {
		// TODO Auto-generated method stub
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

	public static void fillArrow(Graphics g, Color c, int x, int y, int size, ArrowDirection direction) {
		g.setColor(c);
		Points points = getPoints(x, y, size, direction);
		g.fillPolygon(points.getxPoints(), points.getyPoints(), points.getSize());
	}

	public static void drawLine(Graphics g, Color color, int x1, int y1, int x2, int y2) {
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}

	public static void fillRect(Graphics g, Color color, int x, int y, int width, int height) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

	public static void clearRect(Graphics g, Color color, int x, int y, int width, int height) {
		g.setColor(color);
		g.clearRect(x, y, width, height);
	}

	public static void drawRoundRect(Graphics g, Color color, int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		g.setColor(color);
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public static void fillRoundRect(Graphics g, Color color, int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		g.setColor(color);
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public static void drawOval(Graphics g, Color color, int x, int y, int width, int height) {
		g.setColor(color);
		g.drawOval(x, y, width, height);
	}

	public static void fillOval(Graphics g, Color color, int x, int y, int width, int height) {
		g.setColor(color);
		g.fillOval(x, y, width, height);
	}

	public static void drawArc(Graphics g, Color color, int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		g.setColor(color);
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public static void fillArc(Graphics g, Color color, int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		g.setColor(color);
		g.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public static void drawPolyline(Graphics g, Color color, int[] xPoints, int[] yPoints, int nPoints) {
		g.setColor(color);
		g.drawPolygon(xPoints, yPoints, nPoints);
	}

	public static void drawPolygon(Graphics g, Color c, int[] xPoints, int[] yPoints, int nPoints) {
		g.setColor(c);
		g.drawPolyline(xPoints, yPoints, nPoints);
	}

	public static void fillPolygon(Graphics g, Color c, int[] xPoints, int[] yPoints, int nPoints) {
		g.setColor(c);
		g.fillPolygon(xPoints, yPoints, nPoints);
	}

	public static boolean drawImage(Graphics g, Color c, Image img, int x, int y) {
		g.setColor(c);
		return g.drawImage(img, x, y, null);
	}

	public static boolean drawImage(Graphics g, Color c, Image img, int x, int y, int width, int height) {
		g.setColor(c);
		return g.drawImage(img, x, y, c, null);
	}

	public static boolean drawImage(Graphics g, Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
			int sx2, int sy2) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	public static boolean drawImage(Graphics g, Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
			int sx2, int sy2, Color bgcolor) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, null);
	}

	public enum ArrowDirection {
		UP, DOWN, RIGHT, LEFT;
	}

}
