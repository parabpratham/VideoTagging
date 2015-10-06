package com.vid.play;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class TestPlayerMouseListener extends MouseAdapter {

	FullScreenStatergy fullScreenStatergy;

	EmbeddedMediaPlayer mediaPlayer;

	public TestPlayerMouseListener(FullScreenStatergy fullScreenStatergy, EmbeddedMediaPlayer mediaPlayer) {

		this.fullScreenStatergy = fullScreenStatergy;
		this.mediaPlayer = mediaPlayer;

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// logger.trace("mouseMoved(e={})", e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// logger.debug("mousePressed(e={})", e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// logger.debug("mouseReleased(e={})", e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// logger.debug("mouseClicked(e={})", e);
		if (e.getClickCount() == 2) {
			fullScreenStatergy.toggleFullScreen(mediaPlayer);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// logger.debug("mouseWheelMoved(e={})", e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// logger.debug("mouseEntered(e={})", e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// logger.debug("mouseExited(e={})", e);
	}

}
