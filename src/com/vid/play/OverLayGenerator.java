package com.vid.play;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class OverLayGenerator {

	private Map<Integer, CustomeOverlays> overlays;

	private boolean isTransperantWindowSupport;

	private final EmbeddedMediaPlayer mediaPlayer;

	// For displaying information in the information panel
	private VideoInformationDisplayPanel videoInfoPanel;

	private long currentTime;
	private ThreadGroup tg;

	private final JFrame mainFrame;

	private final OverlayLog overalyTrace;

	/**
	 * 
	 * Constructor for the OverlayGenerator class
	 * 
	 * @param mediaPlayer
	 * @param mainFrame
	 */
	public OverLayGenerator() {

		// Initialize components
		overalyTrace = AppLogger.getOverlayLog();
		this.mediaPlayer = CustomeVideoPlayer.getMediaPlayer();
		this.mainFrame = CustomeVideoPlayer.getMainFrame();
		overlays = new HashMap<Integer, CustomeOverlays>();
		this.videoInfoPanel = CustomeVideoPlayer.getVideoInformationDisplayPanel();

		// Check if Transparent video overlay is supported
		isTransperantWindowSupport = true;
		try {
			Class.forName("com.sun.awt.AWTUtilities");
		} catch (Exception e) {
			isTransperantWindowSupport = false;
		}

		tg = new ThreadGroup("Overlays Threads");
		new Thread(tg, new OverlayFactory(), "Generate_Overlay").start();
		overalyTrace.trace(tg.activeCount() + " threads in thread group.");

		registerListeners();
	}

	private void registerListeners() {
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void playing(MediaPlayer mediaPlayer) {
			}

			@Override
			public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
			}

			@Override
			public void stopped(MediaPlayer mediaPlayer) {
				// TODO Auto-generated method stub
				super.stopped(mediaPlayer);
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				super.timeChanged(mediaPlayer, newTime);
				new Thread(new DisplayOverlays(mediaPlayer, newTime), "Generate_Overlay").start();
			}

			@Override
			public void newMedia(MediaPlayer mediaPlayer) {
				// TODO Auto-generated method stub
				super.newMedia(mediaPlayer);
			}

		});

	}

	@SuppressWarnings("deprecation")
	public void stopVideoOerlays() {
		Thread thrds[] = new Thread[tg.activeCount()];
		tg.enumerate(thrds);
		for (Thread t : thrds) {
			overalyTrace.trace(t.getName() + " Stopping " + t.getName());
			if (t.isAlive())
				t.stop();
		}

	}

	public final static Object overlay = new Object();

	private final class DisplayOverlays implements Runnable {

		private final EmbeddedMediaPlayer mediaPlayer;
		private long currentTime;

		public DisplayOverlays(MediaPlayer mediaPlayer, long currentTime) {
			this.mediaPlayer = (EmbeddedMediaPlayer) mediaPlayer;
			this.currentTime = currentTime;
		}

		@Override
		public void run() {
			currentTime = mediaPlayer.getTime();
			List<Integer> keySet = new ArrayList<Integer>();
			keySet.addAll(overlays.keySet());
			java.util.Collections.sort(keySet);

			synchronized (overlay) {
				if (overlays.size() == 0)
					mediaPlayer.setOverlay(null);
				else {
					Outer: for (int startTime : keySet) {
						// System.out.println(Thread.currentThread().getName() +
						// " " + startTime + " " + currentTime);
						overalyTrace.trace(Thread.currentThread().getName() + " " + startTime + " " + currentTime);
						CustomeOverlays customeOverlay = overlays.get(startTime);
						// TODO replace with appropriate
						if (startTime <= currentTime + 267) {
							if (customeOverlay == null) {
								break;
							}
							mediaPlayer.setOverlay(customeOverlay == null ? null : customeOverlay.test);
							mediaPlayer.enableOverlay(true);
							overlays.remove(customeOverlay.startTime);
							try {
								// System.out.println(Thread.currentThread().getName()
								// + " " + customeOverlay.endTime + " "
								// + mediaPlayer.getTime() + " " + currentTime +
								// " " + mediaPlayer.getTime());
								overalyTrace.trace(Thread.currentThread().getName() + " " + customeOverlay.endTime + " "
										+ mediaPlayer.getTime() + " " + currentTime + " " + mediaPlayer.getTime());
								int n = (int) (customeOverlay.endTime - mediaPlayer.getTime()) / 1000;
								while (customeOverlay.endTime >= mediaPlayer.getTime()
										&& currentTime <= mediaPlayer.getTime()) {
									if (n == 0)
										n = 1;
									// System.out.println(Thread.currentThread().getName()
									// + " Sleep for "
									// + ((customeOverlay.endTime -
									// mediaPlayer.getTime()) / n) + " "
									// + mediaPlayer.getTime() + " " +
									// customeOverlay.endTime);
									Thread.sleep((customeOverlay.endTime - mediaPlayer.getTime()) / n);
									n = (int) (customeOverlay.endTime - mediaPlayer.getTime()) / 1000;

								}
							} catch (InterruptedException e) {
								e.printStackTrace();
								overalyTrace.error(e.getLocalizedMessage());
							} finally {
								mediaPlayer.setOverlay(null);
							}
						} else if (customeOverlay.endTime < currentTime + 267) {
							overlays.remove(startTime);
						}
						break Outer;
					}
				}
			}
		}
	}

	public final static Object obj = new Object();
	public final static Object og = new Object();

	/**
	 * @author pratham
	 * 
	 *         This class is used to generate the overlays that are added to a
	 *         hash-map with its starting time as key. The overlays are stored
	 *         in a XML file and are pre-loaded as the media starts playing
	 *
	 */
	private final class OverlayFactory implements Runnable {

		int i = 0;

		@Override
		public void run() {
			generateOveralys();
		}

		private void generateOveralys() {
			// System.out.println(Thread.currentThread().getName() + "
			// Started");
			overalyTrace.trace(Thread.currentThread().getName() + " Started");
			try {
				synchronized (obj) {
					obj.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				overalyTrace.error(e.getMessage());
			}

			while (true) {
				currentTime = mediaPlayer.getTime();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							if (overlays.isEmpty() || overlays.get(currentTime) == null) {
								// System.out.println(Thread.currentThread().getName()
								// + " Overlay generated " + i);
								overalyTrace.trace(Thread.currentThread().getName() + " Overlay generated " + i);
								if (i == 0 && currentTime < 20000)
									overlays.put(20000, new CustomeOverlays(20000, 1200000, draw()));
								else if (currentTime < 1000)
									overlays.put(1000, new CustomeOverlays(1000, 30000, draw2()));
								i++;
							}
						} catch (Exception e) {
							e.printStackTrace();
							overalyTrace.error(e.getMessage());
						}
					}
				});
				synchronized (og) {
					try {
						// System.out.println(Thread.currentThread().getName() +
						// " Waiting on og");
						overalyTrace.trace(Thread.currentThread().getName() + " Waiting on og");
						og.wait();
						i = 0;
						// System.out.println(Thread.currentThread().getName() +
						// " wait over on og");
						overalyTrace.trace(Thread.currentThread().getName() + " wait over on og");
					} catch (InterruptedException e) {
						e.printStackTrace();
						overalyTrace.error(e.getMessage());
					}
				}
			}

		}

	}

	public void updateCurrentTime() {
		synchronized (og) {
			og.notifyAll();
		}
	}

	private Window draw2() {
		Overlay overlay = new Overlay(null);
		return overlay;
	}

	private Window draw() {
		JWindow test = null;
		if (isTransperantWindowSupport) {
			test = new JWindow(null, WindowUtils.getAlphaCompatibleGraphicsConfiguration()) {
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

			JTextArea superImposedLightweigtLabel = new JTextArea("Hello, VLC.");
			superImposedLightweigtLabel.setOpaque(false);
			test.getContentPane().add(superImposedLightweigtLabel);
			superImposedLightweigtLabel.setVisible(true);

		}
		return test;
	}

	private Window draw1() {
		JWindow test = null;
		if (isTransperantWindowSupport) {
			test = new JWindow(null, WindowUtils.getAlphaCompatibleGraphicsConfiguration()) {
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

			final JLabel superImposedLightweigtLabel = new JLabel("Hello, VLC.", JLabel.CENTER);
			superImposedLightweigtLabel.setOpaque(false);
			test.getContentPane().add(superImposedLightweigtLabel);
			superImposedLightweigtLabel.setVisible(true);
		}

		test.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				overalyTrace.trace("Ovelay mouse Clicked " + point);
				System.out.println("Ovelay mouse Clicked " + point);

				if (new Rectangle(200, 250, 100, 100).contains(point)) {
					overalyTrace.trace("Ovelay mouse showed " + point);
					videoInfoPanel.setTitleText(" Hi rectangle " + point);
				}
				if (new Rectangle(100, 80, 10000, 20).contains(point)) {
					overalyTrace.trace("Ovelay mouse entered " + point);
					videoInfoPanel.setTitleText(" Hi string " + point);
				}

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				Point point = e.getPoint();
				overalyTrace.trace("Ovelay mouse entered " + point);
				// System.out.println("Ovelay mouse entered " + point);
				super.mouseEntered(e);
			}
		});

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
