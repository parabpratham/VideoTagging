package com.vid.play;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import uk.co.caprica.vlcj.binding.internal.libvlc_marquee_position_e;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.Marquee;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class PlayerControlsPanel extends JPanel {

	private static final long serialVersionUID = -4900847066329586765L;

	private JButton skipButton;
	private JButton rewindButton;
	private JButton pauseButton;

	private EmbeddedMediaPlayerComponent videoMediaPlayerComponent;

	public PlayerControlsPanel(EmbeddedMediaPlayerComponent videoMediaPlayerComponent) {
		this.videoMediaPlayerComponent = videoMediaPlayerComponent;
		setControlButtons();
	}

	public PlayerControlsPanel() {
	}

	private void setControlButtons() {
		setPauseButton();
		setRewindButton();
		setSkipButton();

		add(rewindButton);
		add(pauseButton);
		add(skipButton);
	}

	/*
	 * To pause and play videos
	 */

	private boolean isPlaying = false;

	private void setPauseButton() {
		pauseButton = new JButton();
		pauseButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_pause_blue.png")));
		pauseButton.setToolTipText("Play/pause");
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseVideo();
			}

		});

	}

	public void pauseVideo() {

		if (isPlaying) {

			Marquee marquee = Marquee.marquee().text("Play").size(40).colour(Color.WHITE).timeout(3000)
					.position(libvlc_marquee_position_e.bottom).opacity(0.8f).enable();
			videoMediaPlayerComponent.getMediaPlayer().setMarquee(marquee);

			pauseButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_pause_blue.png")));
			pauseButton.setToolTipText("Play/pause");
			isPlaying = false;

		} else {

			Marquee marquee1 = Marquee.marquee().text("Pause").size(40).colour(Color.WHITE).timeout(3000)
					.position(libvlc_marquee_position_e.bottom).opacity(0.8f).enable();
			videoMediaPlayerComponent.getMediaPlayer().setMarquee(marquee1);

			pauseButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_play_blue.png")));
			pauseButton.setToolTipText("Play/pause");
			isPlaying = true;

		}

		videoMediaPlayerComponent.getMediaPlayer().pause();

	}

	/*
	 * To rewind videos
	 */
	private void setRewindButton() {
		rewindButton = new JButton();
		rewindButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_rewind_blue.png")));
		rewindButton.setToolTipText("Rewind");
		rewindButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				videoMediaPlayerComponent.getMediaPlayer().skip(-10000);
			}
		});
	}

	/*
	 * To forward videos
	 */
	private void setSkipButton() {
		skipButton = new JButton();
		skipButton
				.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_fastforward_blue.png")));
		skipButton.setToolTipText("Skip forward");
		skipButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				videoMediaPlayerComponent.getMediaPlayer().skip(10000);
			}
		});
	}

}