package com.vid.play;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaDetails;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CustomeVideoPlayer {

	private static final int width = 800;
	private static final int height = 600;
	public static final String VIDEO_ADD = "file:///J:/Movies/Hollywood/we are legion the story of the hacktivists 2012 dvdrip x264.avi";

	private static JFrame mainFrame;

	private JFileChooser fileChooser;

	public static JFrame getFrame() {
		return mainFrame;
	}

	private PlayerControlsPanel controlsPanel;
	private EmbeddedMediaPlayerComponent videoMediaPlayerComponent;
	private FullScreenStatergy fullScreenStrategy;
	private Canvas videoSurface;
	private MediaPlayerFactory mediaPlayerFactory;
	private EmbeddedMediaPlayer mediaPlayer;
	private com.vid.play.PlayerVideoAdjustPanel videoAdjustPanel;
	private VideoInformationDisplayPanel videoInformationDisplayPanel;
	private OverLayGenerator overLayGenerator;

	// Constructor for the video
	public CustomeVideoPlayer() {

		videoSurface = new Canvas();
		videoSurface.setBackground(Color.black);
		videoSurface.setSize(width, height); // Only for initial layout

		mainFrame = new JFrame("VLCJ Test Player");
		mainFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());

		List<String> vlcArgs = new ArrayList<String>();

		vlcArgs.add("--no-snapshot-preview");
		vlcArgs.add("--quiet");
		vlcArgs.add("--quiet-synchro");
		vlcArgs.add("--intf");
		vlcArgs.add("dummy");

		// Special case to help out users on Windows (supposedly this is not
		// actually needed)...
		// if(RuntimeUtil.isWindows()) {
		// vlcArgs.add("--plugin-path=" + WindowsRuntimeUtil.getVlcInstallDir()
		// + "\\plugins");
		// }
		// else {
		// vlcArgs.add("--plugin-path=/home/linux/vlc/lib");
		// }

		// vlcArgs.add("--plugin-path=" + System.getProperty("user.home") +
		// "/.vlcj");

		// logger.debug("vlcArgs={}", vlcArgs);

		// TODO init
		fileChooser = new JFileChooser();

		mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		mediaPlayerFactory.setUserAgent("vlcj test player");

		// Set full screen statergy
		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
		overLayGenerator = new OverLayGenerator(mediaPlayer);
		controlsPanel = new PlayerControlsPanel(mediaPlayer, overLayGenerator);
		videoAdjustPanel = new PlayerVideoAdjustPanel(mediaPlayer);
		videoInformationDisplayPanel = new VideoInformationDisplayPanel(mediaPlayer);

		fullScreenStrategy = new FullScreenStatergy(mediaPlayer, controlsPanel, mainFrame);
		mediaPlayer.setFullScreenStrategy(fullScreenStrategy);
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
		mediaPlayer.setPlaySubItems(true);

		mediaPlayer.setEnableKeyInputHandling(false);
		mediaPlayer.setEnableMouseInputHandling(false);

		// Since we're mixing lightweight Swing components and heavyweight AWT
		// components this is probably a good idea
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		TestPlayerMouseListener mouseListener = new TestPlayerMouseListener(fullScreenStrategy, mediaPlayer);
		videoSurface.addMouseListener(mouseListener);
		videoSurface.addMouseMotionListener(mouseListener);
		videoSurface.addMouseWheelListener(mouseListener);

		mainFrame.setLayout(new BorderLayout());
		mainFrame.setBackground(Color.black);
		mainFrame.add(videoSurface, BorderLayout.CENTER);
		mainFrame.add(controlsPanel, BorderLayout.SOUTH);

		// mainFrame.add(videoAdjustPanel, BorderLayout.EAST);
		// mainFrame.add(videoInformationDisplayPanel, BorderLayout.EAST);

		JTabbedPane infoPane = new JTabbedPane();
		infoPane.add("Media Information", videoInformationDisplayPanel);
		infoPane.add("Video Adjust", videoAdjustPanel);
		mainFrame.add(infoPane, BorderLayout.EAST);

		mainFrame.setJMenuBar(buildMenuBar());
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				// logger.debug("windowClosing(evt={})", evt);

				if (mediaPlayer != null) {
					mediaPlayer.release();
					mediaPlayer = null;
				}

				if (mediaPlayerFactory != null) {
					mediaPlayerFactory.release();
					mediaPlayerFactory = null;
				}
			}
		});

		// Global AWT key handler, you're better off using Swing's InputMap and
		// ActionMap with a JFrame - that would solve all sorts of focus issues
		// too
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				if (event instanceof KeyEvent) {
					KeyEvent keyEvent = (KeyEvent) event;
					if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
						if (keyEvent.getKeyCode() == KeyEvent.VK_F12) {
							controlsPanel.setVisible(!controlsPanel.isVisible());
							videoAdjustPanel.setVisible(!videoAdjustPanel.isVisible());
							mainFrame.getJMenuBar().setVisible(!mainFrame.getJMenuBar().isVisible());
							mainFrame.invalidate();
							mainFrame.validate();
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_A) {
							mediaPlayer.setAudioDelay(mediaPlayer.getAudioDelay() - 50000);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_S) {
							mediaPlayer.setAudioDelay(mediaPlayer.getAudioDelay() + 50000);
						}
						// else if(keyEvent.getKeyCode() == KeyEvent.VK_N) {
						// mediaPlayer.nextFrame();
						// }
						else if (keyEvent.getKeyCode() == KeyEvent.VK_1) {
							mediaPlayer.setTime(60000 * 1);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_2) {
							mediaPlayer.setTime(60000 * 2);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_3) {
							mediaPlayer.setTime(60000 * 3);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_4) {
							mediaPlayer.setTime(60000 * 4);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_5) {
							mediaPlayer.setTime(60000 * 5);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_6) {
							mediaPlayer.setTime(60000 * 6);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_7) {
							mediaPlayer.setTime(60000 * 7);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_8) {
							mediaPlayer.setTime(60000 * 8);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_9) {
							mediaPlayer.setTime(60000 * 9);
						}
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

		mainFrame.setVisible(true);

		mediaPlayer.addMediaPlayerEventListener(new TestPlayerMediaPlayerEventListener());

		// This might be useful
		// enableMousePointer(false);

		// For developing
		playMedia(VIDEO_ADD);

	}

	public void playMedia(String mediaAdd) {
		mediaPlayer.playMedia(mediaAdd);
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

	private void setMouseAndKeyboard() {
		Canvas videoSurface = videoMediaPlayerComponent.getVideoSurface();
		videoSurface.requestFocusInWindow();

		videoSurface.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// on double-click toggle full screen
				if (e.getClickCount() == 2) {
					fullScreenStrategy.toggleFullScreen(mediaPlayer);
					;
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
				/*
				 * if (e.getKeyCode() == KeyEvent.VK_SPACE)
				 * controlsPanel.playPauseVideo(); else if (e.getKeyCode() ==
				 * KeyEvent.VK_ESCAPE)
				 */ // fullScreenStatergy.escapeFullScreen(videoMediaPlayerComponent);

			}
		});

	}

	private JMenuBar buildMenuBar() {
		// Menus are just added as an example of overlapping the video - they
		// are
		// non-functional in this demo player

		JMenuBar menuBar = new JMenuBar();

		JMenu mediaMenu = new JMenu("Media");
		mediaMenu.setMnemonic('m');

		JMenuItem mediaPlayFileMenuItem = new JMenuItem("Play File...");
		mediaPlayFileMenuItem.setMnemonic('f');
		mediaPlayFileMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.enableOverlay(false);
				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(mainFrame)) {
					mediaPlayer.playMedia(fileChooser.getSelectedFile().getAbsolutePath());
				}
				mediaPlayer.enableOverlay(true);
			}
		});

		mediaMenu.add(mediaPlayFileMenuItem);

		JMenuItem mediaPlayStreamMenuItem = new JMenuItem("Play Stream...");
		mediaPlayFileMenuItem.setMnemonic('s');
		mediaMenu.add(mediaPlayStreamMenuItem);

		mediaMenu.add(new JSeparator());

		JMenuItem mediaExitMenuItem = new JMenuItem("Exit");
		mediaExitMenuItem.setMnemonic('x');
		mediaMenu.add(mediaExitMenuItem);

		menuBar.add(mediaMenu);

		JMenu playbackMenu = new JMenu("Playback");
		playbackMenu.setMnemonic('p');

		JMenu playbackChapterMenu = new JMenu("Chapter");
		playbackChapterMenu.setMnemonic('c');
		for (int i = 1; i <= 25; i++) {
			JMenuItem chapterMenuItem = new JMenuItem("Chapter " + i);
			playbackChapterMenu.add(chapterMenuItem);
		}
		playbackMenu.add(playbackChapterMenu);

		JMenu subtitlesMenu = new JMenu("Subtitles");
		playbackChapterMenu.setMnemonic('s');
		String[] subs = { "01 English (en)", "02 English Commentary (en)", "03 French (fr)", "04 Spanish (es)",
				"05 German (de)", "06 Italian (it)" };
		for (int i = 0; i < subs.length; i++) {
			JMenuItem subtitlesMenuItem = new JMenuItem(subs[i]);
			subtitlesMenu.add(subtitlesMenuItem);
		}
		playbackMenu.add(subtitlesMenu);

		menuBar.add(playbackMenu);

		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('t');

		JMenuItem toolsPreferencesMenuItem = new JMenuItem("Preferences...");
		toolsPreferencesMenuItem.setMnemonic('p');
		toolsMenu.add(toolsPreferencesMenuItem);

		menuBar.add(toolsMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');

		JMenuItem helpAboutMenuItem = new JMenuItem("About...");
		helpAboutMenuItem.setMnemonic('a');
		helpMenu.add(helpAboutMenuItem);

		menuBar.add(helpMenu);

		return menuBar;
	}

	private void closeWindow() {
		mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
	}

	private final class TestPlayerMediaPlayerEventListener extends MediaPlayerEventAdapter {
		@Override
		public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
			// logger.debug("mediaChanged(mediaPlayer={},media={},mrl={})",
			// mediaPlayer, media, mrl);
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
			mainFrame.setTitle(mediaMeta.getTitle());

			System.out.println("--------------------------------------");
			System.out.println("Starting " + mediaMeta.getTitle());
			System.out.println(mediaDetails);
			System.out.println("--------------------------------------");
			OverLayGenerator.NotifyObj();
			System.out.println("Notify overlay");
			System.out.println("--------------------------------------");

			final Dimension dimension = mediaPlayer.getVideoDimension();
			// logger.debug("dimension={}", dimension);
			if (dimension != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						videoSurface.setSize(dimension);
						mainFrame.pack();
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
			 * mediaPlayer.setMarqueeSize(40);
			 * mediaPlayer.setMarqueeOpacity(95);
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
			// logger.debug("error(mediaPlayer={})", mediaPlayer);
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
	}

	/**
	 *
	 *
	 * @param enable
	 */
	@SuppressWarnings("unused")
	private void enableMousePointer(boolean enable) {
		// logger.debug("enableMousePointer(enable={})", enable);
		if (enable) {
			videoSurface.setCursor(null);
		} else {
			Image blankImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			videoSurface.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(blankImage, new Point(0, 0), ""));
		}
	}

}
