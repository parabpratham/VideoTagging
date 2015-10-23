package com.vid.play;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.test.CustomMediaPlayerFactory;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;

public class OverLayGenerator {

	private static Map<Integer, CustomOverlay> overlays;

	private boolean isTransperantWindowSupport;

	private static final EmbeddedMediaPlayer mediaPlayer = CustomVideoPlayer.getMediaPlayer();
	private static final MediaListPlayer mediaListPlayer = CustomMediaPlayerFactory.getMediaListPlayer();
	private static final JFrame mainFrame = CustomVideoPlayer.getMainFrame();

	// For displaying information in the information panel
	private static final VideoInformationDisplayPanel videoInfoPanel = CustomVideoPlayer
			.getVideoInformationDisplayPanel();;

	private long currentTime;
	private ThreadGroup tg;

	private final OverlayLog overalyTrace = AppLogger.getOverlayLog();;

	/**
	 * 
	 * Constructor for the OverlayGenerator class
	 * 
	 * @param mediaPlayer
	 * @param mainFrame
	 */
	public OverLayGenerator() {
		// Check if Transparent video overlay is supported
		isTransperantWindowSupport = true;
		try {
			Class.forName("com.sun.awt.AWTUtilities");
		} catch (Exception e) {
			isTransperantWindowSupport = false;
		}

		// Start if the media is preloaded
		startNewGeneratorFactory();

		registerListeners();
	}

	public boolean isTransperantWindowSupport() {
		return isTransperantWindowSupport;
	}

	public void setTransperantWindowSupport(boolean isTransperantWindowSupport) {
		this.isTransperantWindowSupport = isTransperantWindowSupport;
	}

	private void startNewGeneratorFactory() {
		overlays = new HashMap<Integer, CustomOverlay>();
		tg = new ThreadGroup("Overlays Threads");
		new Thread(tg, new OverlayFactory(), "Generate_Overlay").start();
		overalyTrace.trace(tg.activeCount() + " threads in thread group.");
	}

	@SuppressWarnings("deprecation")
	public void stopVideoOerlays() {
		overalyTrace.trace("StopVideo Called " + Calendar.getInstance().getTimeInMillis());
		Thread thrds[] = new Thread[tg.activeCount()];
		tg.enumerate(thrds);
		for (Thread t : thrds) {
			overalyTrace.trace(t.getName() + " Stopping " + t.getName());
			if (t.isAlive())
				t.stop();
		}

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
				super.stopped(mediaPlayer);
				enableOverlay(false);
				stopVideoOerlays();
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				super.timeChanged(mediaPlayer, newTime);
				enableOverlay(true);
				new Thread(new DisplayOverlays(mediaPlayer, newTime), "Display_Overlay").start();
			}

			@Override
			public void newMedia(MediaPlayer mediaPlayer) {
				super.newMedia(mediaPlayer);
				overalyTrace.trace("-- new media ----" + mediaPlayer.getTitle());
			}

			@Override
			public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
				super.mediaChanged(mediaPlayer, media, mrl);
				overalyTrace.trace("-- media changed ----" + mrl);
				enableOverlay(false);
				if (mrl.equals("0")) {
					stopVideoOerlays();
					startNewGeneratorFactory();
				}
				enableOverlay(true);
			}

		});

		mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {

			@Override
			public void stopped(MediaListPlayer mediaListPlayer) {
				super.stopped(mediaListPlayer);
				enableOverlay(false);
				stopVideoOerlays();
			}

			@Override
			public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
				enableOverlay(false);
				stopVideoOerlays();
				super.nextItem(mediaListPlayer, item, itemMrl);
				// System.out.println("Start nwe generator factory");
				overalyTrace.trace("Start nwe generator factory");
				startNewGeneratorFactory();
				enableOverlay(true);
			}

			@Override
			public void mediaStateChanged(MediaListPlayer mediaListPlayer, int newState) {
				// TODO Auto-generated method stub
				super.mediaStateChanged(mediaListPlayer, newState);
			}
		});

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

			while (getOverlays() == null || getOverlays().size() == 0)
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			currentTime = mediaPlayer.getTime();
			List<Integer> keySet = new ArrayList<Integer>();
			keySet.addAll(getOverlays().keySet());
			java.util.Collections.sort(keySet);

			synchronized (overlay) {
				if (getOverlays().size() == 0)
					mediaPlayer.setOverlay(null);
				else {
					Outer: for (int startTime : keySet) {
						// System.out.println(Thread.currentThread().getName() +
						// " " + startTime + " " + currentTime);
						overalyTrace.trace(Thread.currentThread().getName() + " " + startTime + " " + currentTime);
						CustomOverlay customeOverlay = getOverlays().get(startTime);
						// TODO replace with appropriate
						if (startTime <= currentTime + 267) {
							if (customeOverlay == null) {
								break;
							}
							mediaPlayer.setOverlay(customeOverlay == null ? null : customeOverlay);
							mediaPlayer.enableOverlay(true);
							getOverlays().remove(customeOverlay.getStartTime());
							try {
								// System.out.println(Thread.currentThread().getName()
								// + " " + customeOverlay.endTime + " "
								// + mediaPlayer.getTime() + " " + currentTime +
								// " " + mediaPlayer.getTime());
								overalyTrace.trace(Thread.currentThread().getName() + " " + customeOverlay.getEndTime()
										+ " " + mediaPlayer.getTime() + " " + currentTime + " "
										+ mediaPlayer.getTime());
								int n = (int) (customeOverlay.getEndTime() - mediaPlayer.getTime()) / 1000;
								while (customeOverlay.getEndTime() >= mediaPlayer.getTime()
										&& currentTime <= mediaPlayer.getTime()) {
									if (n == 0)
										n = 1;
									// System.out.println(Thread.currentThread().getName()
									// + " Sleep for "
									// + ((customeOverlay.endTime -
									// mediaPlayer.getTime()) / n) + " "
									// + mediaPlayer.getTime() + " " +
									// customeOverlay.endTime);
									Thread.sleep((customeOverlay.getEndTime() - mediaPlayer.getTime()) / n);
									n = (int) (customeOverlay.getEndTime() - mediaPlayer.getTime()) / 1000;

								}
							} catch (InterruptedException e) {
								e.printStackTrace();
								overalyTrace.error(e.getLocalizedMessage());
							} finally {
								mediaPlayer.setOverlay(null);
							}
						} else if (customeOverlay.getEndTime() < currentTime + 267) {
							getOverlays().remove(startTime);
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
							// System.out.println(Thread.currentThread().getName()
							// + " Overlay generated " + i);
							if (currentTime < 1000) {
								overalyTrace.trace(Thread.currentThread().getName() + " Overlay generated " + i);
								getOverlays().put(1000, new CustomOverlay(null, 1000, 19000, i));
							}
							if (currentTime < 25000) {
								overalyTrace.trace(Thread.currentThread().getName() + " Overlay generated " + i);
								getOverlays().put(25000, new CustomOverlay(null, 25000, 30000000, i));
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

	public static void NotifyObj() {
		synchronized (obj) {
			obj.notifyAll();
		}
	}

	public static Map<Integer, CustomOverlay> getOverlays() {
		return overlays;
	}

	public void enableOverlay(boolean b) {
		mediaPlayer.enableOverlay(b);
	}

}
