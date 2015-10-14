package com.vid.execute;

import javax.swing.SwingUtilities;

import com.vid.play.CustomeVideoPlayer;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class RunVideoPlayer {

	public static void main(String[] args) {
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new AppLogger();
				new CustomeVideoPlayer();
			}
		});
	}

}
