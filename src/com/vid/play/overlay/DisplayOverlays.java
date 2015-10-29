package com.vid.play.overlay;

import java.util.ArrayList;
import java.util.List;

import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class DisplayOverlays implements Runnable {

	private final EmbeddedMediaPlayer mediaPlayer;
	private long currentTime;

	public final static Object overlay = new Object();

	private static final OverlayLog logger = AppLogger.getOverlayLog();

	public DisplayOverlays(MediaPlayer mediaPlayer, long currentTime) {
		this.mediaPlayer = (EmbeddedMediaPlayer) mediaPlayer;
		this.currentTime = currentTime;
	}

	@Override
	public void run() {

		currentTime = mediaPlayer.getTime();
		if (OverLayGenerator.getCurrentOverlays() != null) {
			List<Integer> keySet = new ArrayList<Integer>();
			keySet.addAll(OverLayGenerator.getCurrentOverlays().keySet());
			java.util.Collections.sort(keySet);

			System.out.println(Thread.currentThread().getName() + " " + OverLayGenerator.getCurrentOverlays().size()
					+ " " + currentTime);

			logger.trace(OverLayGenerator.getCurrentOverlays().size() + " " + currentTime);
			synchronized (overlay) {
				if (OverLayGenerator.getCurrentOverlays().size() == 0) {
					mediaPlayer.setOverlay(null);
					System.out.println(Thread.currentThread().getName() + " setOverlay(null) " + currentTime);
					logger.trace(" setOverlay(null) " + currentTime);
				} else {
					Outer: for (int startTime : keySet) {
						// System.out.println(Thread.currentThread().getName()
						// + " " + startTime + " " + currentTime);
						logger.trace(Thread.currentThread().getName() + " " + startTime + " " + currentTime);
						CustomOverlay customeOverlay = OverLayGenerator.getCurrentOverlays().get(startTime);
						// TODO replace with appropriate
						if (startTime <= currentTime + 267) {
							if (customeOverlay == null) {
								break;
							}
							customeOverlay.setHasBeenDisplayed(true);
							mediaPlayer.setOverlay(customeOverlay == null ? null : customeOverlay);
							mediaPlayer.enableOverlay(true);
							OverLayGenerator.getCurrentOverlays().remove(customeOverlay.getStartTime());
							try {
								/*
								 * System.out.println(Thread.currentThread()
								 * .getName() + " " +
								 * customeOverlay.getEndTime() + " " +
								 * mediaPlayer.getTime() + " " + currentTime +
								 * " " + mediaPlayer.getTime());
								 * logger.trace(Thread.currentThread().
								 * getName() + " " + customeOverlay.getEndTime()
								 * + " " + mediaPlayer.getTime() + " " +
								 * currentTime + " " + mediaPlayer.getTime());
								 * int n = (int) (customeOverlay.getEndTime() -
								 * mediaPlayer.getTime()) / 1000; while
								 * (customeOverlay.getEndTime() >=
								 * mediaPlayer.getTime() && currentTime <=
								 * mediaPlayer.getTime()) { if (n == 0) n = 1;
								 * System.out.println(Thread.currentThread()
								 * .getName() + " Sleep for " +
								 * ((customeOverlay.getEndTime() -
								 * mediaPlayer.getTime()) / n) + " " +
								 * mediaPlayer.getTime() + " " +
								 * customeOverlay.getEndTime());
								 * Thread.sleep((customeOverlay.getEndTime() -
								 * mediaPlayer.getTime()) / n); n = (int)
								 * (customeOverlay.getEndTime() -
								 * mediaPlayer.getTime()) / 1000;
								 * 
								 * }
								 */
							} catch (Exception e) {
								e.printStackTrace();
								logger.error(e.getLocalizedMessage());
							} finally {
								// mediaPlayer.setOverlay(null);
							}
						} else if (customeOverlay != null && customeOverlay.isHasBeenDisplayed()
								&& customeOverlay.getEndTime() < currentTime + 267) {
							/*
							 * System.out
							 * .println(Thread.currentThread().getName() +
							 * " remove(startTime) " + currentTime +
							 * customeOverlay.getStartTime() + " " +
							 * customeOverlay.getEndTime());
							 */
							if (OverLayGenerator.getCurrentOverlays().containsKey(startTime))
								OverLayGenerator.getCurrentOverlays().remove(startTime);
						}
						break Outer;
					}
				}
			}
		}
	}
}
