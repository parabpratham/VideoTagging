package com.vid.play.overlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.matroska.MatroskaContainer;
import com.vid.play.CustomVideoPlayer;
import com.vid.play.PlayerControlsPanel;
import com.vid.play.overlay.OverlayFactory.CustomOverlayMarker;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class DisplayOverlays implements Runnable {

	public final static Object overlay = new Object();

	private static final OverlayLog logger = AppLogger.getOverlayLog();

	private int nextTime;

	private List<Integer> keySet = new ArrayList<Integer>();

	private Map<Integer, CustomOverlayMarker> overlays;

	private MatroskaContainer container;

	public DisplayOverlays() {
	}

	public DisplayOverlays(MatroskaContainer container) {
		setContainer(container);
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
		// displayOverlayTimeLine();
	}

	private void displayOverlayTimeLine() {
		Hashtable<Integer, JComponent> table = new Hashtable<Integer, JComponent>();
		System.out.println("Overlay size " + keySet.size());
		for (Integer startTime : keySet) {
			table.put(startTime, getJcompToDisplay());
		}
		PlayerControlsPanel.test(table, true);
	}

	private JComponent getJcompToDisplay() {
		JLabel marker = new JLabel("Hi") {
		};
		return marker;
	}

	private void registerListeners() {
		getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void newMedia(MediaPlayer mediaPlayer) {
				super.newMedia(mediaPlayer);
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				logger.trace("Display overlay Event called");
				if (newTime >= nextTime) {
					int overlayIndex = 0;
					for (; overlayIndex < keySet.size() - 1; overlayIndex++) {
						if (newTime >= keySet.get(overlayIndex) && newTime < keySet.get(overlayIndex + 1)) {
							try {
								logger.trace("Display overlay " + overlayIndex + " " + keySet.get(overlayIndex) + " "
										+ newTime + " " + keySet.get(overlayIndex + 1));
								CustomOverlay customeOverlay = new CustomOverlay(null,
										getOverlays().get(keySet.get(overlayIndex)), getContainer());
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

	public MatroskaContainer getContainer() {
		return container;
	}

	public void setContainer(MatroskaContainer container) {
		this.container = container;
	}

}
