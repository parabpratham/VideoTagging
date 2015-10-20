/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009, 2010, 2011, 2012, 2013, 2014, 2015 Caprica Software Limited.
 */

package com.vid.play;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import com.vid.commons.Helper;

import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.binding.internal.libvlc_marquee_position_e;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import uk.co.caprica.vlcj.player.Marquee;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class PlayerControlsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int SKIP_TIME_MS = 10 * 1000;

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	private static final EmbeddedMediaPlayer mediaPlayer = CustomVideoPlayer.getMediaPlayer();;

	private JLabel timeLabel;
	// private JProgressBar positionProgressBar;
	private JSlider positionSlider;
	private JLabel chapterLabel;
	private JLabel totallengthLabel;
	private JButton previousChapterButton;
	private JButton rewindButton;
	private JButton stopButton;
	private JButton playPauseButton;
	private JButton fastForwardButton;
	private JButton nextChapterButton;

	private JButton toggleMuteButton;
	private JSlider volumeSlider;

	private JButton captureButton;

	private JButton ejectButton;
	private JButton connectButton;

	private JButton fullScreenButton;

	private JButton subTitlesButton;

	private JFileChooser fileChooser;

	private boolean mousePressedPlaying = false;

	private final static OverLayGenerator generator = CustomVideoPlayer.getOverLayGenerator();;

	public PlayerControlsPanel() {
		createUI();
		executorService.scheduleAtFixedRate(new UpdateRunnable(mediaPlayer), 0L, 1L, TimeUnit.SECONDS);
	}

	private void createUI() {
		createControls();
		layoutControls();
		registerListeners();
	}

	private void createControls() {
		timeLabel = new JLabel("hh:mm:ss");

		// positionProgressBar = new JProgressBar();
		// positionProgressBar.setMinimum(0);
		// positionProgressBar.setMaximum(1000);
		// positionProgressBar.setValue(0);
		// positionProgressBar.setToolTipText("Time");

		positionSlider = new JSlider();
		positionSlider.setMinimum(0);
		positionSlider.setMaximum(1000);
		positionSlider.setValue(0);
		positionSlider.setSize(new Dimension(1000, 10));
		positionSlider.setOrientation(SwingUtilities.HORIZONTAL);
		positionSlider.setToolTipText("Position");

		chapterLabel = new JLabel("00/00");
		totallengthLabel = new JLabel("hh:mm:ss");

		previousChapterButton = new JButton();
		previousChapterButton
				.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_start_blue.png")));
		previousChapterButton.setToolTipText("Go to previous chapter");

		rewindButton = new JButton();
		rewindButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_rewind_blue.png")));
		rewindButton.setToolTipText("Skip back");

		stopButton = new JButton();
		stopButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_stop_blue.png")));
		stopButton.setToolTipText("Stop");

		playPauseButton = new JButton();
		playPauseButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_pause_blue.png")));
		playPauseButton.setToolTipText("Play/pause");

		fastForwardButton = new JButton();
		fastForwardButton
				.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_fastforward_blue.png")));
		fastForwardButton.setToolTipText("Skip forward");

		nextChapterButton = new JButton();
		nextChapterButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_end_blue.png")));
		nextChapterButton.setToolTipText("Go to next chapter");

		toggleMuteButton = new JButton();
		toggleMuteButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/sound_mute.png")));
		toggleMuteButton.setToolTipText("Toggle Mute");

		volumeSlider = new JSlider();
		volumeSlider.setOrientation(JSlider.HORIZONTAL);
		volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
		volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
		volumeSlider.setPreferredSize(new Dimension(100, 40));
		volumeSlider.setToolTipText("Change volume");

		captureButton = new JButton();
		captureButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/camera.png")));
		captureButton.setToolTipText("Take picture");

		ejectButton = new JButton();
		ejectButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_eject_blue.png")));
		ejectButton.setToolTipText("Load/eject media");

		connectButton = new JButton();
		connectButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/connect.png")));
		connectButton.setToolTipText("Connect to media");

		fileChooser = new JFileChooser();
		fileChooser.setApproveButtonText("Play");
		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newVideoFileFilter());
		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newAudioFileFilter());
		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newPlayListFileFilter());
		FileFilter defaultFilter = SwingFileFilterFactory.newMediaFileFilter();
		fileChooser.addChoosableFileFilter(defaultFilter);
		fileChooser.setFileFilter(defaultFilter);

		fullScreenButton = new JButton();
		fullScreenButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/image.png")));
		fullScreenButton.setToolTipText("Toggle full-screen");

		subTitlesButton = new JButton();
		subTitlesButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/comment.png")));
		subTitlesButton.setToolTipText("Cycle sub-titles");
	}

	private void layoutControls() {
		setBorder(new EmptyBorder(4, 4, 4, 4));

		setLayout(new BorderLayout());

		JPanel positionPanel = new JPanel();
		positionPanel.setLayout(new GridLayout(1, 1));
		// positionPanel.add(positionProgressBar);
		positionPanel.add(positionSlider);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(8, 0));

		topPanel.add(timeLabel, BorderLayout.WEST);
		topPanel.add(positionPanel, BorderLayout.CENTER);
		// topPanel.add(chapterLabel, BorderLayout.EAST);
		topPanel.add(totallengthLabel, BorderLayout.EAST);

		add(topPanel, BorderLayout.NORTH);

		JPanel bottomPanel = new JPanel();

		bottomPanel.setLayout(new FlowLayout());

		bottomPanel.add(previousChapterButton);
		bottomPanel.add(rewindButton);
		bottomPanel.add(stopButton);
		bottomPanel.add(playPauseButton);
		bottomPanel.add(fastForwardButton);
		bottomPanel.add(nextChapterButton);

		bottomPanel.add(volumeSlider);
		bottomPanel.add(toggleMuteButton);

		bottomPanel.add(captureButton);

		bottomPanel.add(ejectButton);
		bottomPanel.add(connectButton);

		bottomPanel.add(fullScreenButton);

		bottomPanel.add(subTitlesButton);

		add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Broken out position setting, handles updating mediaPlayer
	 */
	private void setSliderBasedPosition() {
		if (!mediaPlayer.isSeekable()) {
			return;
		}

		int value = positionSlider.getValue();

		if (positionSlider.getMousePosition() != null) {
			if (positionSlider.getOrientation() == JSlider.HORIZONTAL) {
				value = positionSlider.getMousePosition().x;
			} else if (positionSlider.getOrientation() == JSlider.VERTICAL) {
				value = positionSlider.getMousePosition().y;
			}
			int positionValue = (int) (value * mediaPlayer.getLength() / positionSlider.getWidth());
			// System.out
			// .println(value + " " + positionValue + " " +
			// mediaPlayer.getLength()
			// + " " + positionSlider.getWidth());
			// Avoid end of file freeze-up
			/*
			 * if (positionValue > 0.99f) { positionValue = 0.99f; }
			 */
			mediaPlayer.setTime(positionValue);
		}
	}

	private void updateUIState() {
		if (!mediaPlayer.isPlaying()) {
			// Resume play or play a few frames then pause to show current
			// position in video
			mediaPlayer.play();
			if (!mousePressedPlaying) {
				try {
					// Half a second probably gets an iframe
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// Don't care if unblocked early
				}
				mediaPlayer.pause();
			}
		}
		long time = mediaPlayer.getTime();
		int position = (int) (mediaPlayer.getPosition() * positionSlider.getValue() * 1000.0f);
		int chapter = mediaPlayer.getChapter();
		int chapterCount = mediaPlayer.getChapterCount();
		updateTime(time);
		updateChapter(chapter, chapterCount);
	}

	private void skip(int skipTime) {
		// Only skip time if can handle time setting
		if (mediaPlayer.getLength() > 0) {
			mediaPlayer.skip(skipTime);
			updateUIState();
		}
	}

	public void playPauseVideo() {

		if (mediaPlayer.isPlaying()) {

			Marquee marquee = Marquee.marquee().text("Pause").size(40).colour(Color.WHITE).timeout(3000)
					.position(libvlc_marquee_position_e.bottom).opacity(0.8f).enable();
			mediaPlayer.setMarquee(marquee);

			playPauseButton
					.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_play_blue.png")));
			playPauseButton.setToolTipText("Play/pause");
			isPlaying = false;

			// Remove overlay
			// mediaPlayer.enableOverlay(false);

			mediaPlayer.pause();
		} else {

			Marquee marquee1 = Marquee.marquee().text("Play").size(40).colour(Color.WHITE).timeout(3000)
					.position(libvlc_marquee_position_e.bottom).opacity(0.8f).enable();
			mediaPlayer.setMarquee(marquee1);

			playPauseButton
					.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_pause_blue.png")));
			playPauseButton.setToolTipText("Play/pause");
			isPlaying = true;

			// If stooped and then played
			/*
			 * if (generator == null) generator = new OverLayGenerator();
			 */

			// Set overlay
			// mediaPlayer.enableOverlay(true);

		}

	}

	private boolean isPlaying = false;

	private void registerListeners() {

		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
				((EmbeddedMediaPlayer) mediaPlayer).enableOverlay(false);
				super.mediaChanged(mediaPlayer, media, mrl);
				((EmbeddedMediaPlayer) mediaPlayer).enableOverlay(true);
			}

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				// updateVolume(mediaPlayer.getVolume());
			}

			@Override
			public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
				setTotallengthLabel(Helper.setTotalTime(mediaPlayer.getLength()));
				super.videoOutput(mediaPlayer, newCount);
			}

		});

		positionSlider.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (mediaPlayer.isPlaying()) {
					mousePressedPlaying = true;
					mediaPlayer.pause();
				}
				setSliderBasedPosition();
				updateUIState();
				updateOverlay();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (mediaPlayer.isPlaying()) {
					mousePressedPlaying = true;
					mediaPlayer.pause();
				}
				setSliderBasedPosition();
				updateUIState();
				updateOverlay();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (mediaPlayer.isPlaying()) {
					mousePressedPlaying = true;
					mediaPlayer.pause();
				}
				setSliderBasedPosition();
				updateUIState();
				updateOverlay();
			}

			@Override
			public void mouseEntered(MouseEvent e) {

				int value = positionSlider.getValue();

				if (positionSlider.getMousePosition() != null) {
					if (positionSlider.getOrientation() == JSlider.HORIZONTAL) {
						value = positionSlider.getMousePosition().x;
					} else if (positionSlider.getOrientation() == JSlider.VERTICAL) {
						value = positionSlider.getMousePosition().y;
					}
					if (positionSlider.getMousePosition() != null) {
						int millis = (int) (value * mediaPlayer.getLength() / positionSlider.getWidth());
						String s = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
								TimeUnit.MILLISECONDS.toMinutes(millis)
										- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
								TimeUnit.MILLISECONDS.toSeconds(millis)
										- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
						// System.out.println(s + " " +
						// positionSlider.getMousePosition());
						positionSlider.setToolTipText(s);
					}
				}
			}

		});

		previousChapterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.previousChapter();
			}
		});

		rewindButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				skip(-SKIP_TIME_MS);
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generator.enableOverlay(false);
				playPauseButton
						.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_play_blue.png")));
				positionSlider.setValue(0);
				mediaPlayer.stop();
			}
		});

		playPauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playPauseVideo();
			}
		});

		fastForwardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				skip(SKIP_TIME_MS);
			}
		});

		nextChapterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.nextChapter();
			}
		});

		toggleMuteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.mute();
			}
		});

		volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if(!source.getValueIsAdjusting()) {
				mediaPlayer.setVolume(source.getValue());
				// }
			}
		});

		captureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.saveSnapshot();
			}
		});

		ejectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.enableOverlay(false);
				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(PlayerControlsPanel.this)) {
					mediaPlayer.playMedia(fileChooser.getSelectedFile().getAbsolutePath());
				}

				// Added to set new media to the overlay generator
				generator.stopVideoOerlays();
				generator.enableOverlay(true);

			}
		});

		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.enableOverlay(false);
				String mediaUrl = JOptionPane.showInputDialog(PlayerControlsPanel.this, "Enter a media URL",
						"Connect to media", JOptionPane.QUESTION_MESSAGE);
				if (mediaUrl != null && mediaUrl.length() > 0) {
					mediaPlayer.playMedia(mediaUrl);
				}
				mediaPlayer.enableOverlay(true);
			}
		});

		fullScreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.toggleFullScreen();
			}
		});

		subTitlesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int spu = mediaPlayer.getSpu();
				if (spu > -1) {
					spu++;
					if (spu > mediaPlayer.getSpuCount()) {
						spu = -1;
					}
				} else {
					spu = 0;
				}
				mediaPlayer.setSpu(spu);
			}
		});
	}

	private final class UpdateRunnable implements Runnable {

		private final MediaPlayer mediaPlayer;

		private UpdateRunnable(MediaPlayer mediaPlayer) {
			this.mediaPlayer = mediaPlayer;
		}

		@Override
		public void run() {
			final long time = mediaPlayer.getTime();
			final int position = (int) (mediaPlayer.getPosition() * 1000.0f);
			final int chapter = mediaPlayer.getChapter();
			final int chapterCount = mediaPlayer.getChapterCount();

			// Updates to user interface components must be executed on the
			// Event
			// Dispatch Thread
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					if (mediaPlayer.isPlaying()) {
						updateTime(time);
						updatePosition(position);
						updateChapter(chapter, chapterCount);
					}
				}
			});
		}
	}

	private void updateTime(long millis) {
		String s = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		timeLabel.setText(s);
	}

	private void updateOverlay() {
		// positionProgressBar.setValue(value);
		generator.updateCurrentTime();
	}

	private void updatePosition(int value) {
		// positionProgressBar.setValue(value);
		positionSlider.setValue(value);
	}

	private void updateChapter(int chapter, int chapterCount) {
		String s = chapterCount != -1 ? (chapter + 1) + "/" + chapterCount : "-";
		chapterLabel.setText(s);
		chapterLabel.invalidate();
		validate();
	}

	private void updateVolume(int value) {
		volumeSlider.setValue(value);
	}

	public JLabel getTimeLabel() {
		return timeLabel;
	}

	public void setTimeLabel(JLabel timeLabel) {
		this.timeLabel = timeLabel;
	}

	public String getTotallengthLabel() {
		return totallengthLabel.getText();
	}

	public void setTotallengthLabel(String s) {
		this.totallengthLabel.setText(s);
	}

	public int getVolumeSlider() {
		return volumeSlider.getValue();
	}

	public void setVolumeSlider(int value) {
		this.volumeSlider.setValue(value);
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}
