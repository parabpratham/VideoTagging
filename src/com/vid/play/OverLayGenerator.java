package com.vid.play;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import com.vid.execute.AppLogger;
import com.vid.log.overlay.OverlayLog;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class OverLayGenerator {

	private Map<Integer, CustomeOverlays> overlays;

	private boolean isTransperantWindowSupport;

	private final EmbeddedMediaPlayer mediaPlayer;

	private long currentTime;
	private ThreadGroup tg;

	public final static Object obj = new Object();
	public final static Object og = new Object();
	public final static Object overlay = new Object();

	private final OverlayLog trace;

	public OverLayGenerator(EmbeddedMediaPlayer mediaPlayer) {
		trace = AppLogger.getOverlayLog();
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
		trace.trace(tg.activeCount() + " threads in thread group.");

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
				// TODO Auto-generated method stub
				super.timeChanged(mediaPlayer, newTime);
				new Thread(new DisplayOverlays(mediaPlayer, newTime), "Thread_Generate_Overlay-" + newTime).start();
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
			trace.trace(t.getName() + " Stopping " + t.getName());
			if (t.isAlive())
				t.stop();
		}

	}

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
						trace.trace(Thread.currentThread().getName() + " " + startTime + " " + currentTime);
						CustomeOverlays customeOverlay = overlays.get(startTime);
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
								trace.trace(Thread.currentThread().getName() + " " + customeOverlay.endTime + " "
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
								trace.error(e.getLocalizedMessage());
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

	private final class GenerateOverlays implements Runnable {

		int i = 0;

		@Override
		public void run() {
			// System.out.println(Thread.currentThread().getName() + "
			// Started");
			trace.trace(Thread.currentThread().getName() + " Started");
			try {
				synchronized (obj) {
					obj.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				trace.error(e.getMessage());
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
								trace.trace(Thread.currentThread().getName() + " Overlay generated " + i);
								if (i == 0 && currentTime < 1000000)
									overlays.put(1000000, new CustomeOverlays(1000000, 1200000, draw()));
								else if (currentTime < 5103365)
									overlays.put(5103365, new CustomeOverlays(5103365, 6103365, draw1()));
								i++;
							}
						} catch (Exception e) {
							e.printStackTrace();
							trace.error(e.getMessage());
						}
					}
				});
				synchronized (og) {
					try {
						// System.out.println(Thread.currentThread().getName() +
						// " Waiting on og");
						trace.trace(Thread.currentThread().getName() + " Waiting on og");
						og.wait();
						i = 0;
						// System.out.println(Thread.currentThread().getName() +
						// " wait over on og");
						trace.trace(Thread.currentThread().getName() + " wait over on og");
					} catch (InterruptedException e) {
						e.printStackTrace();
						trace.error(e.getMessage());
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
		test.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
			}
		});
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
