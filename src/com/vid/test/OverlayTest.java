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
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.vid.commons.SupportedColors;
import com.vid.overlay.comp.Jcomp.CustomLabel;
import com.vid.play.CustomVideoPlayer;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.medialist.MediaListItem;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;

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

	private EmbeddedMediaPlayer mediaPlayer;

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

		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--no-snapshot-preview");
		vlcArgs.add("--quiet");
		vlcArgs.add("--quiet-synchro");
		vlcArgs.add("--intf");
		vlcArgs.add("dummy");

		CustomMediaPlayerFactory factory = new CustomMediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		mediaPlayer = CustomMediaPlayerFactory.getMediaPlayer();
		mediaPlayer.setVideoSurface(factory.newVideoSurface(vs));
		CustomMediaPlayerFactory.addMedia(CustomVideoPlayer.getVideoAdd());
		CustomMediaPlayerFactory.playMedias();

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

		// mediaPlayer.setOverlay(new CustomOverlay(null, 0, 0, 1));
		mediaPlayer.setOverlay(new Overlay(null));
		mediaPlayer.enableOverlay(true);

		mediaPlayer.playMedia(mrl);

		// LibXUtil.setFullScreenWindow(f, true);

		registerListeners();
	}

	private class Overlay extends Window {

		private static final long serialVersionUID = 1L;

		public Overlay(Window owner) {
			super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());

			AWTUtilities.setWindowOpaque(this, false);

			setLayout(null);

			OverlayTranComp c = new OverlayTranComp();
			c.setDisplayString("Translucent Web");
			c.setBounds(0, 0, 300, 40);
			c.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						mediaPlayer.pause();
						Desktop.getDesktop().browse(new URI("http://www.google.com/webhp?nomo=1&hl=fr"));
					} catch (URISyntaxException | IOException ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					c.setForeground(new Color(255, 255, 255, 60));
				}

			});
			add(c);

			OverlayTranComp d = new OverlayTranComp();
			d.setDisplayString("Translucent next video");
			d.setBounds(300, 500, 300, 40);
			d.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						System.out.println("Clicked");
						CustomMediaPlayerFactory.addMedia("file:///C:/Users/hp/Desktop/elan-example1.mpg");
						CustomMediaPlayerFactory.stopMedia();
						List<MediaListItem> items = CustomMediaPlayerFactory.getMediaList().items();
						for (MediaListItem mediaListItem : items) {
							System.out.println(mediaListItem.mrl());
						}
						Thread.sleep(1000);
						CustomMediaPlayerFactory.playMedias(1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					d.setForeground(new Color(255, 255, 255, 60));
				}

			});
			add(d);

			OverlayTranComp b = new OverlayTranComp();
			b.setBounds(0, 300, 300, 40);
			b.setDisplayString("Translucent seek");
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					System.out.println(mediaPlayer.getLength() - 2000);
					mediaPlayer.stop();
					mediaPlayer.play(); 
					mediaPlayer.skip(50000);
				}
			});
			add(b);

			CustomLabel label = new CustomLabel(150, 200, 300, 40, new SupportedColors(Color.darkGray, 50),
					"Hi started playing", Color.black, "Hover");
			add(label);
			add(label.getCloseButton());

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

	private void registerListeners() {
		CustomMediaPlayerFactory.getMediaListPlayer()
				.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
					@Override
					public void stopped(MediaListPlayer mediaListPlayer) {
						super.stopped(mediaListPlayer);
						System.out.println(mediaListPlayer + " stopped");
						mediaPlayer.enableOverlay(false);
					}

					@Override
					public void played(MediaListPlayer mediaListPlayer) {
						// TODO Auto-generated method stub
						super.played(mediaListPlayer);
						System.out.println(mediaListPlayer + " played");
					}

					@Override
					public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
						super.nextItem(mediaListPlayer, item, itemMrl);
						System.out.println(mediaListPlayer + " next");
						mediaPlayer.enableOverlay(false);
					}

					@Override
					public void mediaStateChanged(MediaListPlayer mediaListPlayer, int newState) {
						// TODO Auto-generated method stub
						super.mediaStateChanged(mediaListPlayer, newState);
					}
				});
	}

}