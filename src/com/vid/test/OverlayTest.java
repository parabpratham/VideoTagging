package com.vid.test;
/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2009, 2010, 2011, 2012 Caprica Software Limited.
 */

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.vid.commons.SupportedColors;
import com.vid.overlay.comp.Jcomp.CustomLabel;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;

/**
 * An test player demonstrating how to achieve a transparent overlay and
 * translucent painting.
 * <p>
 * Press SPACE to pause the video play-back.
 * <p>
 * Press F11 to toggle the overlay.
 * <p>
 * If the video looks darker with the overlay enabled, then most likely you are
 * using a compositing window manager that is doing some fancy blending of the
 * overlay window and the main application window. You have to turn off those
 * window effects.
 * <p>
 * Note that it is not possible to use this approach if you also want to use
 * Full-Screen Exclusive Mode. If you want to use an overlay and you need full-
 * screen, then you have to emulate full-screen by changing your window bounds
 * rather than using FSEM.
 * <p>
 * This approach <em>does</em> work in full-screen mode if you use your desktop
 * window manager to put your application into full-screen rather than using the
 * Java FSEM.
 * <p>
 * If you want to provide an overlay that dynamically updates, e.g. if you want
 * some animation, then your overlay should sub-class <code>JWindow</code>
 * rather than <code>Window</code> since you will get double-buffering and
 * eliminate flickering. Since the overlay is transparent you must take care to
 * erase the overlay background properly.
 * <p>
 * Specify a single MRL to play on the command-line.
 */
public class OverlayTest {

	public static void main(final String[] args) throws Exception {
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new OverlayTest(
						"file:///J:/Movies/Hollywood/we are legion the story of the hacktivists 2012 dvdrip x264.avi");
			}
		});
	}

	public OverlayTest(String mrl) {
		Frame f = new Frame("Test Player");
		f.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());
		f.setSize(800, 600);
		f.setBackground(Color.black);
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		f.setLayout(new BorderLayout());
		Canvas vs = new Canvas();
		f.add(vs, BorderLayout.CENTER);
		f.setVisible(true);

		MediaPlayerFactory factory = new MediaPlayerFactory();

		final EmbeddedMediaPlayer mediaPlayer = factory.newEmbeddedMediaPlayer();
		mediaPlayer.setVideoSurface(factory.newVideoSurface(vs));

		f.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_F11:
					mediaPlayer.enableOverlay(!mediaPlayer.overlayEnabled());
					break;

				case KeyEvent.VK_SPACE:
					mediaPlayer.pause();
					break;
				}
			}
		});

		mediaPlayer.setOverlay(new Overlay(null));
		mediaPlayer.enableOverlay(true);

		mediaPlayer.playMedia(mrl);

		// LibXUtil.setFullScreenWindow(f, true);
	}

	private class Overlay extends Window {

		private static final long serialVersionUID = 1L;

		public Overlay(Window owner) {
			super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());

			AWTUtilities.setWindowOpaque(this, false);

			setLayout(null);

			OverlayTranComp c = new OverlayTranComp();
			c.setDisplayString("Translucent");
			c.setBounds(0, 0, 300, 40);
			add(c);

			CustomLabel label = new CustomLabel(150, 200, 300, 40, new SupportedColors(Color.darkGray, 50),
					"Hi started playing", Color.black, "Hover");

			add(label);
			JButton b = new JButton("JButton");
			b.setBounds(450, 200, 10, 10);
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					b.getParent().getComponentAt(b.getX() - 300, b.getY()).hide();
				}
			});
			add(b);

		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			GradientPaint gp = new GradientPaint(180.0f, 280.0f, new Color(255, 255, 255, 255), 250.0f, 380.0f,
					new Color(255, 255, 0, 0));
			g2.setPaint(gp);
			for (int i = 0; i < 3; i++) {
				g2.drawOval(150, 280, 100, 100);
				g2.fillOval(150, 280, 100, 100);
				g2.translate(120, 20);
			}
		}
	}
}