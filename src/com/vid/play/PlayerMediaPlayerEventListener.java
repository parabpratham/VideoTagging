package com.vid.play;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.vid.execute.AppLogger;
import com.vid.log.trace.TraceLog;
import com.vid.play.overlay.OverLayGenerator;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaDetails;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public final class PlayerMediaPlayerEventListener extends MediaPlayerEventAdapter {

	private final static TraceLog logger = AppLogger.getTraceLog();
	private final static JFrame frame = CustomVideoPlayer.getMainFrame();

	@Override
	public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
		logger.debug("mediaChanged(mediaPlayer=" + mediaPlayer + ",media=" + media + ",mrl=" + mrl);
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
		// logger.debug("finished(mediaPlayer={})", mediaPlayer);
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
		// logger.debug("paused(mediaPlayer={})", mediaPlayer);
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {
		// logger.debug("playing(mediaPlayer={})", mediaPlayer);
		MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
		// logger.info("mediaDetails={}", mediaDetails);
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
		// logger.debug("stopped(mediaPlayer={})", mediaPlayer);
	}

	@Override
	public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
		// logger.debug("videoOutput(mediaPlayer={},newCount={})",
		// mediaPlayer, newCount);
		if (newCount == 0) {
			return;
		}

		MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
		// logger.info("mediaDetails={}", mediaDetails);
		MediaMeta mediaMeta = mediaPlayer.getMediaMeta();
		// logger.info("mediaMeta={}", mediaMeta);
		frame.setTitle(mediaMeta.getTitle());

		logger.trace("--------------------------------------");
		logger.trace("Starting " + mediaMeta.getTitle());
		logger.trace("" + mediaDetails);
		logger.trace("--------------------------------------");
		OverLayGenerator.NotifyObj();
		logger.trace("Notify overlay");
		logger.trace("--------------------------------------");

		final Dimension dimension = mediaPlayer.getVideoDimension();
		// logger.debug("dimension={}", dimension);
		if (dimension != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getVideosurface().setSize(dimension);
					frame.pack();
				}
			});
		}

		// You can set a logo like this if you like...
		File logoFile = new File("./etc/vlcj-logo.png");
		if (logoFile.exists()) {
			mediaPlayer.setLogoFile(logoFile.getAbsolutePath());
			mediaPlayer.setLogoOpacity(0.5f);
			mediaPlayer.setLogoLocation(10, 10);
			mediaPlayer.enableLogo(true);
		}

		// Demo the marquee
		/*
		 * mediaPlayer.setMarqueeText("vlcj java bindings for vlc");
		 * mediaPlayer.setMarqueeSize(40); mediaPlayer.setMarqueeOpacity(95);
		 * mediaPlayer.setMarqueeColour(Color.white);
		 * mediaPlayer.setMarqueeTimeout(5000);
		 * mediaPlayer.setMarqueeLocation(50, 120);
		 * mediaPlayer.enableMarquee(true);
		 */

		// Not quite sure how crop geometry is supposed to work...
		//
		// Assertions in libvlc code:
		//
		// top + height must be less than visible height
		// left + width must be less than visible width
		//
		// With DVD source material:
		//
		// Reported size is 1024x576 - this is what libvlc reports when you
		// call
		// get video size
		//
		// mpeg size is 720x576 - this is what is reported in the native log
		//
		// The crop geometry relates to the mpeg size, not the size reported
		// through the API
		//
		// For 720x576, attempting to set geometry to anything bigger than
		// 719x575 results in the assertion failures above (seems like it
		// should
		// allow 720x576) to me

		// mediaPlayer.setCropGeometry("4:3");
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		logger.error("error(mediaPlayer=" + mediaPlayer + ")");
	}

	@Override
	public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
		// logger.debug("mediaSubItemAdded(mediaPlayer={},subItem={})",
		// mediaPlayer, subItem);
	}

	@Override
	public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
		// logger.debug("mediaDurationChanged(mediaPlayer={},newDuration={})",
		// mediaPlayer, newDuration);
	}

	@Override
	public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
		// logger.debug("mediaParsedChanged(mediaPlayer={},newStatus={})",
		// mediaPlayer, newStatus);
	}

	@Override
	public void mediaFreed(MediaPlayer mediaPlayer) {
		// logger.debug("mediaFreed(mediaPlayer={})", mediaPlayer);
	}

	@Override
	public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
		// logger.debug("mediaStateChanged(mediaPlayer={},newState={})",
		// mediaPlayer, newState);
	}

	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		// logger.debug("mediaMetaChanged(mediaPlayer={},metaType={})",
		// mediaPlayer, metaType);
	}

	@SuppressWarnings("unused")
	private void enableMousePointer(boolean enable) {
		// logger.debug("enableMousePointer(enable={})", enable);
		if (enable) {
			getVideosurface().setCursor(null);
		} else {
			Image blankImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			getVideosurface()
					.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(blankImage, new Point(0, 0), ""));
		}
	}

	private Canvas getVideosurface() {
		return CustomVideoPlayer.getVideosurface();
	}
}
