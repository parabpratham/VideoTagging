package com.vid.play;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import com.vid.execute.AppLogger;
import com.vid.log.trace.TraceLog;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CustomVideoPlayer {

	private static final int width = 800;
	private static final int height = 600;
	private static final String VIDEO_ADD = "file:///J:/Movies/Hollywood/we are legion the story of the hacktivists 2012 dvdrip x264.avi";

	private static final JFrame mainFrame = new JFrame("VLCJ Test Player");;
	private static final Canvas videoSurface = new Canvas();
	private static final JTabbedPane infoPane = new JTabbedPane();
	private static final JFileChooser fileChooser = new JFileChooser();
	private final static TraceLog logger = AppLogger.getTraceLog();

	private static PlayerControlsPanel controlsPanel;
	private static EmbeddedMediaPlayerComponent videoMediaPlayerComponent;
	private static FullScreenStatergy fullScreenStrategy;
	private static MediaPlayerFactory mediaPlayerFactory;
	private static EmbeddedMediaPlayer mediaPlayer;
	private static PlayerVideoAdjustPanel videoAdjustPanel;
	private static VideoInformationDisplayPanel videoInformationDisplayPanel;
	private static OverLayGenerator overLayGenerator;

	/**
	 * Constructor for the player
	 * <p>
	 * Create MainFrame
	 * <p>
	 * Create MediaPlayer
	 * <p>
	 * Create PlayerControlsPanel
	 * 
	 * <p>
	 * These are all static and will be common across all the classes
	 * 
	 */
	public CustomVideoPlayer() {

		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--no-snapshot-preview");
		vlcArgs.add("--quiet");
		vlcArgs.add("--quiet-synchro");
		vlcArgs.add("--intf");
		vlcArgs.add("dummy");

		/*
		 * Special case to help out users on Windows (supposedly this is not
		 * actually needed)...
		 * 
		 * if(RuntimeUtil.isWindows()) { vlcArgs.add("--plugin-path=" +
		 * WindowsRuntimeUtil.getVlcInstallDir() + "\\plugins"); } else {
		 * vlcArgs.add("--plugin-path=/home/linux/vlc/lib"); }
		 * 
		 * vlcArgs.add("--plugin-path=" + System.getProperty("user.home") +
		 * "/.vlcj"); logger.debug("vlcArgs={}", vlcArgs);
		 * 
		 */
		setMediaPlayerFactory(new CustomMediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()])));
		getMediaPlayerFactory().setUserAgent("vlcj test player");

		// Set full screen media player
		setMediaPlayer(CustomMediaPlayerFactory.getMediaPlayer());

		// Set full overlay generator
		setOverLayGenerator(new OverLayGenerator());

		// Set full controls panel
		setControlsPanel(new PlayerControlsPanel());

		// set video adjust panel
		setVideoAdjustPanel(new PlayerVideoAdjustPanel());

		// Set Video Information Display Panel
		setVideoInformationDisplayPanel(new VideoInformationDisplayPanel());

		// TODO Set Full Screen Strategy
		setFullScreenStrategy(new FullScreenStatergy());
		getMediaPlayer().setFullScreenStrategy(getFullScreenStrategy());

		// TODD add to fetch the width height from property files
		getVideosurface().setBackground(Color.black);
		getVideosurface().setSize(width, height); // Only for initial layout
		getMainFrame().setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());

		// Media player configurations
		getMediaPlayer().setVideoSurface(getMediaPlayerFactory().newVideoSurface(getVideosurface()));
		getMediaPlayer().setPlaySubItems(true);
		getMediaPlayer().setEnableKeyInputHandling(false);
		getMediaPlayer().setEnableMouseInputHandling(false);

		// Since we're mixing lightweight Swing components and heavyweight AWT
		// components this is probably a good idea
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		// Adding custom mouse-listener
		/*
		 * TestPlayerMouseListener mouseListener = new
		 * TestPlayerMouseListener(getFullScreenStrategy(), getMediaPlayer());
		 * getVideosurface().addMouseListener(mouseListener);
		 * getVideosurface().addMouseMotionListener(mouseListener);
		 * getVideosurface().addMouseWheelListener(mouseListener);
		 */

		getMainFrame().setLayout(new BorderLayout());
		getMainFrame().setBackground(Color.black);
		getMainFrame().add(getVideosurface(), BorderLayout.CENTER);
		getMainFrame().add(getControlsPanel(), BorderLayout.SOUTH);

		// mainFrame.add(videoAdjustPanel, BorderLayout.EAST);
		// mainFrame.add(videoInformationDisplayPanel, BorderLayout.EAST);
		getInfopane().add("Media Information", getVideoInformationDisplayPanel());
		getInfopane().add("Video Adjust", getVideoAdjustPanel());
		getMainFrame().add(getInfopane(), BorderLayout.EAST);

		getMainFrame().setJMenuBar(buildMenuBar());
		getMainFrame().pack();
		getMainFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getMainFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				// logger.debug("windowClosing(evt={})", evt);
				if (getMediaPlayer() != null) {
					getMediaPlayer().release();
					setMediaPlayer(null);
				}

				if (getMediaPlayerFactory() != null) {
					getMediaPlayerFactory().release();
					setMediaPlayerFactory(null);
				}
			}
		});
		// Add keyboard event listners
		addAWTEventListener();

		getMainFrame().setVisible(true);
		getMediaPlayer().addMediaPlayerEventListener(new PlayerMediaPlayerEventListener());

		// This might be useful
		// enableMousePointer(false);

		// Only for testing purpose
		playMedia(getVideoAdd());

	}

	/**
	 * Global AWT key handler, you're better off using Swing's InputMap and
	 * ActionMap with a JFrame - that would solve all sorts of focus issues too
	 */
	private void addAWTEventListener() {

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				if (event instanceof KeyEvent) {
					KeyEvent keyEvent = (KeyEvent) event;
					if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
						if (keyEvent.getKeyCode() == KeyEvent.VK_F12) {
							getControlsPanel().setVisible(!getControlsPanel().isVisible());
							getVideoAdjustPanel().setVisible(!getVideoAdjustPanel().isVisible());
							mainFrame.getJMenuBar().setVisible(!mainFrame.getJMenuBar().isVisible());
							mainFrame.invalidate();
							mainFrame.validate();
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_A) {
							getMediaPlayer().setAudioDelay(getMediaPlayer().getAudioDelay() - 50000);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_S) {
							getMediaPlayer().setAudioDelay(getMediaPlayer().getAudioDelay() + 50000);
						}
						// else if(keyEvent.getKeyCode() == KeyEvent.VK_N) {
						// mediaPlayer.nextFrame();
						// }
						else if (keyEvent.getKeyCode() == KeyEvent.VK_1) {
							getMediaPlayer().setTime(60000 * 1);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_2) {
							getMediaPlayer().setTime(60000 * 2);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_3) {
							getMediaPlayer().setTime(60000 * 3);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_4) {
							getMediaPlayer().setTime(60000 * 4);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_5) {
							getMediaPlayer().setTime(60000 * 5);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_6) {
							getMediaPlayer().setTime(60000 * 6);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_7) {
							getMediaPlayer().setTime(60000 * 7);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_8) {
							getMediaPlayer().setTime(60000 * 8);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_9) {
							getMediaPlayer().setTime(60000 * 9);
						}
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

	}

	/**
	 * For playing content at location mrl mediaAdd
	 * 
	 * @param mediaAdd
	 */
	public static void playMedia(String mediaAdd) {
		parseInformation();
		CustomMediaPlayerFactory.addMedia(mediaAdd);
		CustomMediaPlayerFactory.playMedias();
		getMediaPlayer().mute();
	}

	/**
	 * Get information of the media currently playing
	 */
	private static void parseInformation() {

		try {
			getMediaPlayer().prepareMedia(getVideoAdd());
			getMediaPlayer().requestParseMedia();

			int titleCount = getMediaPlayer().getTitleCount();
			int title = getMediaPlayer().getTitle();
			logger.trace("Video Title " + titleCount + " " + title + " " + mediaPlayer.getTitleDescriptions().size());
			// mediaPlayer.setTitle(newTitle);

			int videoTrackCount = getMediaPlayer().getVideoTrackCount();
			int videoTrack = getMediaPlayer().getVideoTrack();
			logger.trace("VideoTrack " + videoTrackCount + " " + videoTrack);
			// mediaPlayer.setVideoTrack(newVideoTrack);

			int audioTrackCount = getMediaPlayer().getAudioTrackCount();
			int audioTrack = getMediaPlayer().getVideoTrack();
			logger.trace("AudioTrack " + audioTrackCount + " " + audioTrack);
			// mediaPlayer.setAudioTrack(newAudioTrack);

			int spuTrackCount = getMediaPlayer().getSpuCount();
			int spuTrack = getMediaPlayer().getVideoTrack();
			logger.trace("SubtitleTrackCount " + spuTrackCount + " " + spuTrack);
			// mediaPlayer.setSpu(newSpuTrack);

			int chapterCount = getMediaPlayer().getChapterCount();
			int currentChapter = getMediaPlayer().getChapter();
			logger.trace("ChapterCount " + chapterCount + " " + currentChapter);
		} catch (Exception e) {
			logger.error(CustomVideoPlayer.class.getName() + "-- Parse Information -- " + e.getMessage());
		}

	}

	/**
	 * 
	 * Builds menu for the player
	 * 
	 * @return instance of the menu bar
	 */
	private JMenuBar buildMenuBar() {
		new CustomMenuBar();
		return CustomMenuBar.getMenubar();
	}

	public static JFrame getMainFrame() {
		return mainFrame;
	}

	public static MediaPlayerFactory getMediaPlayerFactory() {
		return mediaPlayerFactory;
	}

	public static void setMediaPlayerFactory(MediaPlayerFactory mediaPlayerFactory) {
		CustomVideoPlayer.mediaPlayerFactory = mediaPlayerFactory;
	}

	public static EmbeddedMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public static void setMediaPlayer(EmbeddedMediaPlayer mediaPlayer) {
		CustomVideoPlayer.mediaPlayer = mediaPlayer;
	}

	public static String getVideoAdd() {
		return VIDEO_ADD;
	}

	public static PlayerControlsPanel getControlsPanel() {
		return controlsPanel;
	}

	public static void setControlsPanel(PlayerControlsPanel controlsPanel) {
		CustomVideoPlayer.controlsPanel = controlsPanel;
	}

	public static EmbeddedMediaPlayerComponent getVideoMediaPlayerComponent() {
		return videoMediaPlayerComponent;
	}

	public static void setVideoMediaPlayerComponent(EmbeddedMediaPlayerComponent videoMediaPlayerComponent) {
		CustomVideoPlayer.videoMediaPlayerComponent = videoMediaPlayerComponent;
	}

	public static FullScreenStatergy getFullScreenStrategy() {
		return fullScreenStrategy;
	}

	public static void setFullScreenStrategy(FullScreenStatergy fullScreenStrategy) {
		CustomVideoPlayer.fullScreenStrategy = fullScreenStrategy;
	}

	public static OverLayGenerator getOverLayGenerator() {
		return overLayGenerator;
	}

	public static void setOverLayGenerator(OverLayGenerator overLayGenerator) {
		CustomVideoPlayer.overLayGenerator = overLayGenerator;
	}

	public static VideoInformationDisplayPanel getVideoInformationDisplayPanel() {
		return videoInformationDisplayPanel;
	}

	public static void setVideoInformationDisplayPanel(VideoInformationDisplayPanel videoInformationDisplayPanel) {
		CustomVideoPlayer.videoInformationDisplayPanel = videoInformationDisplayPanel;
	}

	public static PlayerVideoAdjustPanel getVideoAdjustPanel() {
		return videoAdjustPanel;
	}

	public static void setVideoAdjustPanel(PlayerVideoAdjustPanel videoAdjustPanel) {
		CustomVideoPlayer.videoAdjustPanel = videoAdjustPanel;
	}

	public static Canvas getVideosurface() {
		return videoSurface;
	}

	public static JTabbedPane getInfopane() {
		return infoPane;
	}

	public static JFileChooser getFilechooser() {
		return fileChooser;
	}

}
