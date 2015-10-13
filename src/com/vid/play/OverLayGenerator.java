package com.vid.play;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class OverLayGenerator {

	private Map<Integer, CustomeOverlays> overlays;

	private boolean isTransperantWindowSupport;

	private final EmbeddedMediaPlayer mediaPlayer;

	private long currentTime;

	private long totalTime;

	private ThreadGroup tg;

	public final static Object obj = new Object();
	public final static Object og = new Object();

	public OverLayGenerator(EmbeddedMediaPlayer mediaPlayer) {

		this.mediaPlayer = mediaPlayer;
		isTransperantWindowSupport = true;
		try {
			Class.forName("com.sun.awt.AWTUtilities");
		} catch (Exception e) {
			isTransperantWindowSupport = false;
		}
		overlays = new HashMap<Integer, CustomeOverlays>();

		tg = new ThreadGroup("Overlays Threads");
		new Thread(tg, new GenerateOverlays(), "Generate Overlay").start();
		new Thread(tg, new DisplayOverlays(mediaPlayer), "Diaplaying Overlay").start();
		System.out.println(tg.activeCount() + " threads in thread group.");
	}

	@SuppressWarnings("deprecation")
	public void stopVideoOerlays() {
		Thread thrds[] = new Thread[tg.activeCount()];
		tg.enumerate(thrds);
		for (Thread t : thrds) {
			System.out.println("Stopping " + t.getName());
			if (t.isAlive())
				t.stop();
		}

	}

	private long getCurrentTime() {
		return mediaPlayer.getTime();
	}

	private final class DisplayOverlays implements Runnable {
		private final EmbeddedMediaPlayer mediaPlayer;

		public DisplayOverlays(EmbeddedMediaPlayer mediaPlayer) {
			this.mediaPlayer = mediaPlayer;
		}

		@Override
		public void run() {
			int fps = 267;

			try {
				synchronized (obj) {
					obj.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			currentTime = mediaPlayer.getTime();
			totalTime = mediaPlayer.getLength();

			System.out.println(Thread.currentThread().getName() + " Started " + currentTime + " " + totalTime);

			while (currentTime < totalTime) {
				List<Integer> keySet = new ArrayList<Integer>();
				keySet.addAll(overlays.keySet());
				java.util.Collections.sort(keySet);
				for (int startTime : keySet) {
					while (startTime > currentTime) {
						try {
							System.out.println("Disaply Overlay " + startTime + " " + currentTime + " Sleeping");
							Thread.sleep((fps));
							currentTime = currentTime + fps;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.println("Overlay out " + startTime + " " + currentTime);
					if (startTime <= currentTime) {
						CustomeOverlays customeOverlays = overlays.get(startTime);
						mediaPlayer.setOverlay(customeOverlays == null ? null : customeOverlays.test);
						mediaPlayer.enableOverlay(true);
						overlays.remove(customeOverlays);
						try {
							System.out.println(
									"Overlay End sleep " + customeOverlays.endTime + " " + mediaPlayer.getTime());
							Thread.sleep(customeOverlays.endTime - mediaPlayer.getTime());
							mediaPlayer.setOverlay(null);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				mediaPlayer.enableOverlay(false);

			}
			System.out.println(Thread.currentThread().getName() + " Ended ");
		}
	}

	private final class GenerateOverlays implements Runnable {

		int i = 0;
		int j = 0;

		@Override
		public void run() {
			currentTime = mediaPlayer.getTime();
			System.out.println(Thread.currentThread().getName() + " Started");
			// Updates to user interface components must be executed on the
			// Event
			// Dispatch Thread
			try {
				synchronized (obj) {
					obj.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			while (j < 2) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							if (overlays.isEmpty() || overlays.get(currentTime) == null) {
								System.out.println("Overlay generated " + i);
								if (i == 0 && currentTime < 10000)
									overlays.put(10000, new CustomeOverlays(10000, 20000, draw()));
								else if (currentTime < 30000)
									overlays.put(30000, new CustomeOverlays(30000, 50000, draw1()));
								i++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				j++;
			}

			synchronized (og) {
				try {
					og.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void updateCurrentTime(long currentTime) {
		this.currentTime = currentTime;
		synchronized (og) {
			og.notify();
		}
	}

	private Window draw() {
		Window test = null;
		if (isTransperantWindowSupport) {
			test = new Window(null, WindowUtils.getAlphaCompatibleGraphicsConfiguration()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void paint(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

					g.setColor(Color.white);
					g.drawRect(100, 150, 100, 100);

					g.setFont(new Font("Sans", Font.BOLD, 32));
					g.drawString("Heavyweight overlay test", 100, 300);
				}
			};

			AWTUtilities.setWindowOpaque(test, false); // Doesn't work in
														// full-screen
														// exclusive
														// mode, you would
														// have
														// to use
														// 'simulated'
														// full-screen -
														// requires
														// Sun/Oracle
														// JDK
			test.setBackground(new Color(0, 0, 0, 0)); // This is what you
														// do in
		}
		return test;
	}

	private Window draw1() {
		Window test = null;
		if (isTransperantWindowSupport) {
			test = new Window(null, WindowUtils.getAlphaCompatibleGraphicsConfiguration()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void paint(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

					g.setColor(Color.red);
					g.drawRect(200, 250, 100, 100);

					g.setFont(new Font("Sans", Font.BOLD, 32));
					g.drawString("Heavyweight overlay test", 100, 100);
				}
			};

			AWTUtilities.setWindowOpaque(test, false); // Doesn't work in
														// full-screen
														// exclusive
														// mode, you would
														// have
														// to use
														// 'simulated'
														// full-screen -
														// requires
														// Sun/Oracle
														// JDK
			test.setBackground(new Color(0, 0, 0, 0)); // This is what you
														// do in
		}
		return test;
	}

	public static void NotifyObj() {
		synchronized (obj) {
			obj.notifyAll();
		}
	}

	private class CustomeOverlays {

		private Window test;
		private int startTime;
		private int endTime;

		public CustomeOverlays(int startTime, int endTime, Window test) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.test = test;

		}

	}
}
