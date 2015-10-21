package com.vid.test;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JComponent;

public class OverlayTranComp extends JComponent {

	private static final long serialVersionUID = 1L;

	private String displayString;

	private URI uri;

	public OverlayTranComp() {
		setOpaque(false);
		try {
			uri = new URI("http://java.sun.com");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g2.setPaint(new Color(255, 128, 128, 64));

		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.setPaint(Color.YELLOW);
		g2.setFont(new Font("Sansserif", Font.BOLD, 18));
		g2.drawString(displayString, 16, 26);
	}

	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	class OpenUrlAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop.getDesktop().browse(new URI("http://www.google.com/webhp?nomo=1&hl=fr"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
	}
}
