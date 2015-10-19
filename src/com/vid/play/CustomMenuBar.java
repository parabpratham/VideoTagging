package com.vid.play;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CustomMenuBar {

	private static final JMenuBar menuBar = new JMenuBar();
	private static final EmbeddedMediaPlayer mediaPlayer = CustomVideoPlayer.getMediaPlayer();
	private static final JFileChooser fileChooser = CustomVideoPlayer.getFilechooser();
	private static final JFrame mainFrame = CustomVideoPlayer.getMainFrame();;

	public CustomMenuBar() {

		// Menus are just added as an example of overlapping the video - they
		// are
		// non-functional in this demo player

		JMenu mediaMenu = new JMenu("Media");
		mediaMenu.setMnemonic('m');

		JMenuItem mediaPlayFileMenuItem = new JMenuItem("Play File...");
		mediaPlayFileMenuItem.setMnemonic('f');
		mediaPlayFileMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.enableOverlay(false);
				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(mainFrame)) {
					CustomVideoPlayer.playMedia(fileChooser.getSelectedFile().getAbsolutePath());
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

		getMenubar().add(mediaMenu);

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

		getMenubar().add(playbackMenu);

		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('t');

		JMenuItem toolsPreferencesMenuItem = new JMenuItem("Preferences...");
		toolsPreferencesMenuItem.setMnemonic('p');
		toolsMenu.add(toolsPreferencesMenuItem);

		getMenubar().add(toolsMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');

		JMenuItem helpAboutMenuItem = new JMenuItem("About...");
		helpAboutMenuItem.setMnemonic('a');
		helpMenu.add(helpAboutMenuItem);

		getMenubar().add(helpMenu);

	}

	public static JMenuBar getMenubar() {
		return menuBar;
	}

}
