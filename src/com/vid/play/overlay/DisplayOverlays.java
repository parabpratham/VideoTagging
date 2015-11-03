package com.vid.play.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.vid.execute.AppLogger;

import com.vid.log.trace.overlay.OverlayLog;

import com.vid.play.CustomVideoPlayer;
import com.vid.play.overlay.OverlayFactory.CustomOverlayMarker;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class DisplayOverlays implements Runnable {

	private EmbeddedMediaPlayer mediaPlayer;

	private long currentTime;

	public final static Object overlay = new Object();

	private static final OverlayLog logger = AppLogger.getOverlayLog();

	private int nextTime;

	private List<Integer> keySet = new ArrayList<Integer>();

	private Map<Integer, CustomOverlayMarker> overlays;

	public DisplayOverlays() {
	}

	public DisplayOverlays(MediaPlayer mediaPlayer, long currentTime) {
		this.mediaPlayer = (EmbeddedMediaPlayer) mediaPlayer;
		this.currentTime = currentTime;
	}

	public static void notifyOverlay() {
		synchronized (overlay) {
			overlay.notifyAll();
		}
	}

	@Override
	public void run() {
		overlays = new HashMap<>(OverLayGenerator.getOverlays());
		if (getOverlays() != null) {
			keySet.addAll(OverLayGenerator.getOverlays().keySet());
			java.util.Collections.sort(keySet);
		}
		registerListeners();
		nextTime = 0;
	}

	private void registerListeners() {
		getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void newMedia(MediaPlayer mediaPlayer) {
				super.newMedia(mediaPlayer);
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				super.timeChanged(mediaPlayer, newTime);
				if (newTime > nextTime) {
					int overlayIndex = 0;
					for (; overlayIndex < keySet.size() - 1; overlayIndex++) {
						if (newTime >= keySet.get(overlayIndex) && newTime < keySet.get(overlayIndex + 1)) {
							try {
								logger.trace("Display overlay " + overlayIndex + " " + keySet.get(overlayIndex) + " "
										+ newTime + " " + keySet.get(overlayIndex + 1));
								CustomOverlay customeOverlay = new CustomOverlay(null,
										getOverlays().get(keySet.get(overlayIndex)));
								customeOverlay.setId(overlayIndex);
								addOverlay(customeOverlay);
							} catch (Exception e) {
								e.printStackTrace();
							}
							nextTime = keySet.get(overlayIndex + 1);
							break;

						}
					}

					if (overlayIndex == keySet.size()) {
						nextTime = Integer.MAX_VALUE;
					}
				}
			}
		});

	}

	public static Object getOverlay() {
		return overlay;
	}

	public Map<Integer, CustomOverlayMarker> getOverlays() {
		return overlays;
	}

	public void setOverlays(Map<Integer, CustomOverlayMarker> overlays) {
		this.overlays = overlays;
	}

	public EmbeddedMediaPlayer getMediaPlayer() {
		return CustomVideoPlayer.getMediaPlayer();
	}

	public synchronized void addOverlay(CustomOverlay overlay) {
		getMediaPlayer().setOverlay(null);
		getMediaPlayer().setOverlay(overlay);
		getMediaPlayer().enableOverlay(true);

	}

	// In the previous approach where different thread was used
	private void displayOverlay(List<Integer> keySet) {
		logger.trace(getOverlays().size() + " " + currentTime);
		synchronized (overlay) {
			if (OverLayGenerator.getOverlays().size() == 0) {
				mediaPlayer.setOverlay(null);
				System.out.println(Thread.currentThread().getName() + " setOverlay(null) " + currentTime);
				logger.trace(" setOverlay(null) " + currentTime);
			} else {
				Outer: for (int startTime : keySet) {
					System.out.println(startTime + " " + OverLayGenerator.getOverlays().get(startTime));
					CustomOverlay customeOverlay = new CustomOverlay(null, getOverlays().get(startTime));
					// TODO replace with appropriate
					if (startTime <= currentTime + 267) {
						logger.trace(Thread.currentThread().getName() + " " + startTime + " " + currentTime);
						customeOverlay.setHasBeenDisplayed(true);
						// System.out.println(Thread.currentThread().getName()
						// + " " + startTime + " " + currentTime
						// + " " + customeOverlay.getComponentCount());
						mediaPlayer.setOverlay(customeOverlay);
						// mediaPlayer.setOverlay(new
						// CustomOverlayTest(null, 0, 0, 1));
						mediaPlayer.enableOverlay(true);
						OverLayGenerator.getOverlays().remove(customeOverlay.getStartTime());
					} else if (customeOverlay != null && customeOverlay.isHasBeenDisplayed()
							&& customeOverlay.getEndTime() < currentTime + 267) {
						if (getOverlays().containsKey(startTime))
							getOverlays().remove(startTime);
					}
					break Outer;
				}
			}
		}
	}

}
