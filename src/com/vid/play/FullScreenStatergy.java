package com.vid.play;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;

public class FullScreenStatergy {

	public FullScreenStatergy(EmbeddedMediaPlayerComponent videoMediaPlayerComponent, JFrame frame,
			PlayerControlsPanel controlsPanel) {
		setFullScreenStatergy(videoMediaPlayerComponent, frame, controlsPanel);
	}

	private void setFullScreenStatergy(EmbeddedMediaPlayerComponent videoMediaPlayerComponent, JFrame frame,
			PlayerControlsPanel controlsPanel) {
		videoMediaPlayerComponent.getMediaPlayer()
				.setFullScreenStrategy(new DefaultAdaptiveRuntimeFullScreenStrategy(frame) {

					@Override
					protected void beforeEnterFullScreen() {
						// TODO Auto-generated method stub
						controlsPanel.setVisible(false);
					}

					@Override
					protected void afterExitFullScreen() {
						// TODO Auto-generated method stub
						controlsPanel.setVisible(true);
					}
				});

	}

	public void toggleFullScreen(EmbeddedMediaPlayerComponent videoMediaPlayerComponent) {
		videoMediaPlayerComponent.getMediaPlayer().toggleFullScreen();
	}

	public void escapeFullScreen(EmbeddedMediaPlayerComponent videoMediaPlayerComponent) {
		if (videoMediaPlayerComponent.getMediaPlayer().isFullScreen())
			videoMediaPlayerComponent.getMediaPlayer().toggleFullScreen();
	}

}
