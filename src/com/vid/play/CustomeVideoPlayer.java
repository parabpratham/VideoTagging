package com.vid.play;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.internal.libvlc_marquee_position_e;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class CustomeVideoPlayer {

	private static final int width = 600;
	private static final int height = 400;
	private static final String VIDEO_ADD = "file:///J:/Movies/Hollywood/we are legion the story of the hacktivists 2012 dvdrip x264.avi";

	private JFrame frame;
	private PlayerControlsPanel controlsPanel;
	private EmbeddedMediaPlayerComponent videoMediaPlayerComponent;
	private AudioMediaPlayerComponent audioMediaPlayerComponent;

	public static void main(String[] args) {
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new CustomeVideoPlayer();
			}
		});
	}

	private JPanel createJPanel() {
		// Add back panel
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(videoMediaPlayerComponent, BorderLayout.CENTER);

		// add control panel
		controlsPanel = new PlayerControlsPanel(videoMediaPlayerComponent);
		contentPane.add(controlsPanel, BorderLayout.SOUTH);

		return contentPane;
	}

	public CustomeVideoPlayer() {
		frame = new JFrame("Direct");
		frame.setBounds(100, 100, width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// To respond to the click on closing
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				videoMediaPlayerComponent.release();
				System.exit(0);
			}
		});

		// To create a media player
		setVideoMediaPlayer(frame);
		frame.setContentPane(videoMediaPlayerComponent);
		frame.setVisible(true);
		// Set content frame on frame
		frame.setContentPane(createJPanel());

		// set audio stream
		setAudioMediaPlayer();

		// Parse the video information
		parseInformation();

		// play
		start(VIDEO_ADD);
	}

	private void parseInformation() {
		videoMediaPlayerComponent.getMediaPlayer().prepareMedia(VIDEO_ADD);
		videoMediaPlayerComponent.getMediaPlayer().requestParseMedia();

		MediaPlayer mediaPlayer = videoMediaPlayerComponent.getMediaPlayer();
		int titleCount = mediaPlayer.getTitleCount();
		int title = mediaPlayer.getTitle();
		System.out.println("Video Title " + titleCount + " " + title + " " + mediaPlayer.getTitleDescriptions().size());
		// mediaPlayer.setTitle(newTitle);

		int videoTrackCount = mediaPlayer.getVideoTrackCount();
		int videoTrack = mediaPlayer.getVideoTrack();
		System.out.println("VideoTrack " + videoTrackCount + " " + videoTrack);
		// mediaPlayer.setVideoTrack(newVideoTrack);

		int audioTrackCount = mediaPlayer.getAudioTrackCount();
		int audioTrack = mediaPlayer.getVideoTrack();
		System.out.println("AudioTrack " + audioTrackCount + " " + audioTrack);
		// mediaPlayer.setAudioTrack(newAudioTrack);

		int spuTrackCount = mediaPlayer.getSpuCount();
		int spuTrack = mediaPlayer.getVideoTrack();
		// mediaPlayer.setSpu(newSpuTrack);

		int chapterCount = mediaPlayer.getChapterCount();
		int currentChapter = mediaPlayer.getChapter();
		System.out.println("ChapterCount " + chapterCount + " " + currentChapter);

	}

	// To setup audio media player events
	private void setAudioMediaPlayer() {
		audioMediaPlayerComponent = new AudioMediaPlayerComponent();
		audioMediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
		});

	}

	private void start(String mrl) {

		videoMediaPlayerComponent.getMediaPlayer().playMedia(VIDEO_ADD);
		// audioMediaPlayerComponent.getMediaPlayer().playMedia(VIDEO_ADD);
	}

	private String getFileName() {
		String[] add = VIDEO_ADD.split("/");
		return add[add.length - 1];
	}

	// To setup video media player events
	private void setVideoMediaPlayer(JFrame frame) {

		videoMediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
			private static final long serialVersionUID = 7286396827742362356L;

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							String title;
							if (videoMediaPlayerComponent.getMediaPlayer().getTitle() == 0) {
								title = getFileName();
							} else {
								title = videoMediaPlayerComponent.getMediaPlayer().getTitleDescriptions().get(0)
										.description();
							}
							frame.setTitle((String.format("My first Media Player -%s", title)));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						closeWindow();
					}
				});
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
				// TODO Auto-generated method stub
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(frame, "Failed to play media", "Erro", JOptionPane.ERROR_MESSAGE);
						closeWindow();
					};
				});
			}

		};

		// On Windows you must explicitly disable the native mouse and keyboard
		// input handling.
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			videoMediaPlayerComponent.getMediaPlayer().setEnableKeyInputHandling(false);
			videoMediaPlayerComponent.getMediaPlayer().setEnableMouseInputHandling(false);
		}
		// Mouse and keyboard
		setMouseAndKeyboard();

	}

	private void setMouseAndKeyboard() {
		Canvas videoSurface = videoMediaPlayerComponent.getVideoSurface();
		videoSurface.requestFocusInWindow();

		videoSurface.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// on double-click toggle full screen
				if (e.getClickCount() == 2) {
					System.out.println("Full screen");
					toggleFullScreen();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

		});

		videoSurface.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyCode());
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					controlsPanel.pauseVideo();
			}
		});

	}

	private void toggleFullScreen() {

	}

	private void setMarquee() {
		// Marquee marquee = Marquee.marquee().text("vlcj
		// tutorial").size(40).colour(Color.WHITE).timeout(3000)
		// .position(libvlc_marquee_position_e.top).opacity(0.8f).enable();
		// videoMediaPlayerComponent.getMediaPlayer().setMarquee(marquee);
		videoMediaPlayerComponent.getMediaPlayer().setMarqueeText("vlcj tutorial");
		videoMediaPlayerComponent.getMediaPlayer().setMarqueeSize(40);
		videoMediaPlayerComponent.getMediaPlayer().setMarqueeColour(Color.WHITE);
		videoMediaPlayerComponent.getMediaPlayer().setMarqueeTimeout(3000);
		videoMediaPlayerComponent.getMediaPlayer().setMarqueePosition(libvlc_marquee_position_e.bottom);
		videoMediaPlayerComponent.getMediaPlayer().setMarqueeOpacity(0.8f);
		videoMediaPlayerComponent.getMediaPlayer().enableMarquee(true);
	}

	private void closeWindow() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
