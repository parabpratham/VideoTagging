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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.player.MediaDetails;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class VideoInformationDisplayPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final MediaPlayer mediaPlayer;

	private JCheckBox editVideoInformation;

	private JLabel videoTitleLabel;
	private JTextArea videoTitleText;

	private JLabel videoTotalLengthLabel;
	private JTextField videoTotalLengthText;

	private JLabel hueLabel;
	private JSlider hueSlider;

	private JLabel saturationLabel;
	private JSlider saturationSlider;

	private JLabel gammaLabel;
	private JSlider gammaSlider;

	private MediaDetails mediaDetails;
	private MediaMeta mediaMeta;

	public VideoInformationDisplayPanel() {
		this.mediaPlayer = CustomeVideoPlayer.getMediaPlayer();

		System.out.println("Creating video info UI");
		createUI();

		// new Thread(new UpdateVideoInfo(mediaPlayer)).start();
	}

	public void updateMediaInfo() {
		mediaDetails = mediaPlayer.getMediaDetails();
		mediaMeta = mediaPlayer.getMediaMeta();

		videoTitleText.setText(mediaMeta.getTitle());
		videoTitleText.setEditable(true);

	}

	private void createUI() {
		createControls();
		layoutControls();
		registerListeners();

	}

	private void createControls() {
		editVideoInformation = new JCheckBox("Change Video Information");

		videoTitleLabel = new JLabel("Title");
		videoTitleText = new JTextArea();
		videoTitleText.setEnabled(false);
		String newTitle;
		videoTitleText.addInputMethodListener(new InputMethodListener() {

			@Override
			public void inputMethodTextChanged(InputMethodEvent event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void caretPositionChanged(InputMethodEvent event) {
				// TODO Auto-generated method stub

			}
		});

		videoTotalLengthLabel = new JLabel("Total Length");
		videoTotalLengthText = new JTextField();
		videoTotalLengthText.setText("hh:mm:ss");

		hueLabel = new JLabel("Hue");
		hueSlider = new JSlider();
		hueSlider.setOrientation(JSlider.HORIZONTAL);
		hueSlider.setMinimum(LibVlcConst.MIN_HUE);
		hueSlider.setMaximum(LibVlcConst.MAX_HUE);
		hueSlider.setPreferredSize(new Dimension(100, 40));
		hueSlider.setToolTipText("Change ");
		hueSlider.setEnabled(false);

		saturationLabel = new JLabel("Saturation");
		saturationSlider = new JSlider();
		saturationSlider.setOrientation(JSlider.HORIZONTAL);
		saturationSlider.setMinimum(Math.round(LibVlcConst.MIN_SATURATION * 100.0f));
		saturationSlider.setMaximum(Math.round(LibVlcConst.MAX_SATURATION * 100.0f));
		saturationSlider.setPreferredSize(new Dimension(100, 40));
		saturationSlider.setToolTipText("Change ");
		saturationSlider.setEnabled(false);

		gammaLabel = new JLabel("Gamma");
		gammaSlider = new JSlider();
		gammaSlider.setOrientation(JSlider.HORIZONTAL);
		gammaSlider.setMinimum(Math.round(LibVlcConst.MIN_GAMMA * 100.0f));
		gammaSlider.setMaximum(Math.round(LibVlcConst.MAX_GAMMA * 100.0f));
		gammaSlider.setPreferredSize(new Dimension(100, 40));
		gammaSlider.setToolTipText("Change ");
		gammaSlider.setEnabled(false);

		// contrastSlider.setValue(Math.round(mediaPlayer.getBrightness() *
		// 100.0f));
		hueSlider.setValue(mediaPlayer.getHue());
		saturationSlider.setValue(Math.round(mediaPlayer.getSaturation() * 100.0f));
		gammaSlider.setValue(Math.round(mediaPlayer.getGamma() * 100.0f));
	}

	private void layoutControls() {
		setBorder(new EmptyBorder(4, 4, 4, 4));

		setLayout(new BorderLayout());

		JPanel slidersPanel = new JPanel();
		slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
		slidersPanel.add(editVideoInformation);
		slidersPanel.add(videoTitleLabel);
		slidersPanel.add(videoTitleText);
		slidersPanel.add(videoTotalLengthLabel);
		slidersPanel.add(videoTotalLengthText);
		slidersPanel.add(hueLabel);
		slidersPanel.add(hueSlider);
		slidersPanel.add(saturationLabel);
		slidersPanel.add(saturationSlider);
		slidersPanel.add(gammaLabel);
		slidersPanel.add(gammaSlider);

		add(slidersPanel, BorderLayout.CENTER);
	}

	public void setvideoTotalLengthText(String s) {
		videoTotalLengthText.setText(s);
	}

	public String getvideoTotalLengthText() {
		return videoTotalLengthText.getText();
	}

	public void setTitleText(String s) {
		videoTitleText.setText(s);
	}

	public String getTitleText() {
		return videoTitleText.getText();
	}

	private void registerListeners() {

		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void playing(MediaPlayer mediaPlayer) {
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				setvideoTotalLengthText(Helper.setTotalTime(newTime));
			}

			@Override
			public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
				super.videoOutput(mediaPlayer, newCount);
			}
		});

		editVideoInformation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean enabled = editVideoInformation.isSelected();
				hueSlider.setEnabled(enabled);
				saturationSlider.setEnabled(enabled);
				gammaSlider.setEnabled(enabled);
				mediaPlayer.setAdjustVideo(enabled);
			}
		});

		hueSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if(!source.getValueIsAdjusting()) {
				mediaPlayer.setHue(source.getValue());
				// }
			}
		});

		saturationSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if(!source.getValueIsAdjusting()) {
				mediaPlayer.setSaturation(source.getValue() / 100.0f);
				// }
			}
		});

		gammaSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if(!source.getValueIsAdjusting()) {
				mediaPlayer.setGamma(source.getValue() / 100.0f);
				// }
			}
		});
	}

	private final class UpdateVideoInfo implements Runnable {

		MediaPlayer mediaPlayer;

		public UpdateVideoInfo(MediaPlayer mediaPlayer) {
			this.mediaPlayer = mediaPlayer;
		}

		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					videoTotalLengthText.setText(mediaPlayer.getTime() + "");
					System.out.println("videoTotalLengthText.setText " + mediaPlayer.getTime());
				}
			});
		}

	}
}
