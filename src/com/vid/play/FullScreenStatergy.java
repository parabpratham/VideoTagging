package com.vid.play;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class FullScreenStatergy extends DefaultFullScreenStrategy {

	public FullScreenStatergy() {
		super(CustomVideoPlayer.getMainFrame());
		setFullScreenStatergy(CustomVideoPlayer.getMediaPlayer(), CustomVideoPlayer.getControlsPanel(),
				CustomVideoPlayer.getMainFrame());
	}

	private void setFullScreenStatergy(EmbeddedMediaPlayer mediaPlayer, PlayerControlsPanel controlsPanel,
			JFrame frame) {
		mediaPlayer.setFullScreenStrategy(new DefaultAdaptiveRuntimeFullScreenStrategy(frame) {
			@Override
			protected void beforeEnterFullScreen() {
				controlsPanel.setVisible(false);
			}

			@Override
			protected void afterExitFullScreen() {
				controlsPanel.setVisible(true);
			}
		});

	}

	public void toggleFullScreen(EmbeddedMediaPlayer mediaPlayer) {
		mediaPlayer.toggleFullScreen();
	}

	public void escapeFullScreen(EmbeddedMediaPlayer mediaPlayer) {
		if (mediaPlayer.isFullScreen())
			mediaPlayer.toggleFullScreen();
	}

}
