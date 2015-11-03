package com.vid.play.overlay;

import java.util.Calendar;
import java.util.Map;

import javax.swing.JFrame;

import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
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

	// private static Map<Integer, CustomOverlayTest> overlays;

	private static Map<Integer, CustomOverlayMarker> overlays;

	private boolean isTransperantWindowSupport;

	private static final EmbeddedMediaPlayer mediaPlayer = CustomVideoPlayer.getMediaPlayer();
	private static final MediaListPlayer mediaListPlayer = CustomMediaPlayerFactory.getMediaListPlayer();

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

	public boolean isTransperantWindowSupport() {
		return isTransperantWindowSupport;
	}

	public void setTransperantWindowSupport(boolean isTransperantWindowSupport) {
		this.isTransperantWindowSupport = isTransperantWindowSupport;
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
		tg = new ThreadGroup("Overlays Threads");
		new OverlayFactory();
		logger.trace(tg.activeCount() + " threads in thread group.");
		overlays = OverlayFactory.getOverlayMarkerMap();
		new Thread(dg, new DisplayOverlays(), "Display_Overlay").start();
		DisplayOverlays.notifyOverlay();
	}

	private static void resetOverlays() {

		Thread thrds[] = new Thread[dg.activeCount()];
		dg.enumerate(thrds);
		for (Thread t : thrds) {
			logger.trace(t.getName() + " Stopping " + t.getName());
			if (t.isAlive())
				t.stop();
		}
	}

	@SuppressWarnings("deprecation")
	public void stopVideoOerlays() {
		logger.trace("StopVideo Called " + Calendar.getInstance().getTimeInMillis());
		enableOverlay(false);
		Thread thrds[] = new Thread[tg.activeCount()];
		tg.enumerate(thrds);
		for (Thread t : thrds) {
			logger.trace(t.getName() + " Stopping " + t.getName());
			if (t.isAlive())
				t.stop();
		}

		resetOverlays();

	}

	static int i = 0;

	private void registerListeners() {
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
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
				logger.trace("Start nwe generator factory");
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

	public void enableOverlay(boolean b) {
		mediaPlayer.enableOverlay(b);
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

}
