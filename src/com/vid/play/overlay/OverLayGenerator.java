package com.vid.play.overlay;

import java.util.Calendar;
import java.util.Map;

import javax.swing.JFrame;

import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.matroska.MatroskaContainer;
import com.vid.play.CustomMediaPlayerFactory;
import com.vid.play.CustomVideoPlayer;
import com.vid.play.overlay.OverlayFactory.CustomOverlayMarker;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;

public class OverLayGenerator {

	private static Map<Integer, CustomOverlayMarker> overlays;

	private boolean isTransperantWindowSupport;

	private static ThreadGroup tg;
	private static ThreadGroup dg;

	private static final OverlayLog logger = AppLogger.getOverlayLog();

	public OverLayGenerator() {
		// Check if Transparent video overlay is supported
		isTransperantWindowSupport = true;
		try {
			Class.forName("com.sun.awt.AWTUtilities");
		} catch (Exception e) {
			isTransperantWindowSupport = false;
		}

		// Start if the media is pre_loaded
		startNewGeneratorFactory();

		// Add listeners to comp
		registerListeners();
	}

	/**
	 * To read the annotations from the xml file and generate overlays to be
	 * displayed
	 * 
	 * called when <br>
	 * 1> Media is loaded for the first time <br>
	 * 2> Media changes
	 * 
	 */
	private void startNewGeneratorFactory() {

		MatroskaContainer container = new MatroskaContainer(CustomVideoPlayer.getVideoAdd());
		new OverlayFactory(container);
		overlays = OverlayFactory.getOverlayMarkerMap();
		if (overlays != null) {
			dg = new ThreadGroup("Overlays Threads");
			new Thread(dg, new DisplayOverlays(container), "Display_Overlay").start();
			logger.trace(dg.activeCount() + " threads in thread group.");
			DisplayOverlays.notifyOverlay();
		}
	}

	@SuppressWarnings("deprecation")
	private static void resetOverlays() {
		try {
			Thread thrds[] = new Thread[dg.activeCount()];
			dg.enumerate(thrds);
			for (Thread t : thrds) {
				logger.trace(t.getName() + " Stopping " + t.getName());
				if (t.isAlive())
					t.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void stopVideoOerlays() {
		if (tg != null) {
			try {
				logger.trace("StopVideo Called " + Calendar.getInstance().getTimeInMillis());
				enableOverlay(false);
				Thread thrds[] = new Thread[tg.activeCount()];
				tg.enumerate(thrds);
				for (Thread t : thrds) {
					logger.trace(t.getName() + " Stopping " + t.getName());
					if (t.isAlive())
						t.stop();
				}
			} catch (Exception e) {
			}
		}
		resetOverlays();
	}

	static int i = 0;

	private void registerListeners() {
		getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void playing(MediaPlayer mediaPlayer) {
			}

			@Override
			public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
				mediaPlayer.setVolume(mediaPlayer.getVolume());
				CustomVideoPlayer.getMainFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
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
				CustomVideoPlayer.getMainFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
			}

			@Override
			public void newMedia(MediaPlayer mediaPlayer) {
				super.newMedia(mediaPlayer);
				logger.trace("-- new media ----" + mediaPlayer.getTitle());
			}

			@Override
			public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
				super.mediaChanged(mediaPlayer, media, mrl);
				logger.trace("-- media changed ----" + mrl);
				enableOverlay(false);
				if (mrl.equals("0")) {
					stopVideoOerlays();
					startNewGeneratorFactory();
				}
				enableOverlay(true);
			}

		});

		getMediaListPlayer().addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {

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
				logger.trace("Start nwe generator factory");
				startNewGeneratorFactory();
				enableOverlay(true);
			}

			@Override
			public void mediaStateChanged(MediaListPlayer mediaListPlayer, int newState) {
				// TODO Auto-generated method stub
				logger.trace("Media state changed " + newState);
				super.mediaStateChanged(mediaListPlayer, newState);
			}
		});

	}

	public void enableOverlay(boolean b) {
		getMediaPlayer().enableOverlay(b);
	}

	public static Map<Integer, CustomOverlayMarker> getOverlays() {
		return overlays;
	}

	public static void setOverlays(Map<Integer, CustomOverlayMarker> overlays) {
		OverLayGenerator.overlays = overlays;
	}

	// TODO
	public final static Object obj = new Object();

	public static void NotifyObj() {
		synchronized (obj) {
			obj.notifyAll();
		}
	}

	public static final EmbeddedMediaPlayer getMediaPlayer() {
		return CustomVideoPlayer.getMediaPlayer();
	}

	public static final MediaListPlayer getMediaListPlayer() {
		return CustomMediaPlayerFactory.getMediaListPlayer();
	}

	public boolean isTransperantWindowSupport() {
		return isTransperantWindowSupport;
	}

	public void setTransperantWindowSupport(boolean isTransperantWindowSupport) {
		this.isTransperantWindowSupport = isTransperantWindowSupport;
	}

}
