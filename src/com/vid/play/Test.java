package com.vid.play;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Test {

	public static void main(String[] args) {

		/*****************************************
		 * GUI to display video
		 * 
		 */

		JFrame f = new JFrame();
		f.setLocation(100, 100);
		f.setSize(1000, 600);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		// Create a canvas to display video

		Canvas c = new Canvas();
		// background is black
		c.setBackground(Color.black);
		// JPanel p = new JPanel();
		// p.setLayout(new BorderLayout());
		// video take all the surface of JPanel
		// p.add(c);
		// f.add(p);
		// f.setContentPane(p);

		/*****************************************************************
		 * Secondly we read files using vlcj and the native library of vlc
		 * 
		 */

		// Load the native library of vlc from the directory where vlc is
		// installed
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "c:/Program Files/VideoLAN/VLC/");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		// Initialize the media player
		MediaPlayerFactory mpf = new MediaPlayerFactory();
		// Control all interaction
		EmbeddedMediaPlayer emp = mpf.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(f));
		PlayerControlsPanel p = new PlayerControlsPanel(emp);
		p.setLayout(new BorderLayout());
		p.add(c);
		f.add(p);
		f.setContentPane(p);
		emp.setVideoSurface(mpf.newVideoSurface(c));

		String file = "file:///J:\\Movies\\Hollywood\\we are legion the story of the hacktivists 2012 dvdrip x264.avi";
		// emp.prepareMedia(file);
		// emp.play()
		// emp.enableOverlay(true);
		// emp.setMarqueeText("VLCJ is quite good");
		// emp.setMarqueeOpacity(70);
		// emp.setMarqueeColour(Color.green);
		// emp.setMarqueeTimeout(3000);
		// emp.setMarqueeLocation(300, 400);
		// emp.enableMarquee(true);
		System.out.println(emp.playMedia(file));

	}

}
