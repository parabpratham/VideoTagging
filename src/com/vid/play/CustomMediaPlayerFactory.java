package com.vid.play;

import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;

public class CustomMediaPlayerFactory extends MediaPlayerFactory {

	private static MediaListPlayer mediaListPlayer;

	private static MediaList mediaList;

	private static EmbeddedMediaPlayer mediaPlayer;

	public CustomMediaPlayerFactory(String[] vrgs) {
		super(vrgs);
		mediaListPlayer = newMediaListPlayer();
		mediaList = newMediaList();
		mediaPlayer = newEmbeddedMediaPlayer();
		mediaListPlayer.setMediaPlayer(mediaPlayer);
		mediaListPlayer.setMediaList(mediaList);
		//mediaListPlayer.setMode(MediaListPlayerMode.LOOP);
	}

	public static void addMedia(String mrl) {
		mediaList.addMedia(mrl);
	}

	public static void removeMedia(int index) {
		mediaList.removeMedia(index);
	}

	public static void clearMedia() {
		mediaList.clear();
	}

	public static MediaListPlayer getMediaListPlayer() {
		return mediaListPlayer;
	}

	public static void playMedias() {
		mediaListPlayer.play();
	}

	public static void playMedias(int index) {
		mediaListPlayer.playItem(index);
	}

	public static void playNextMedia() {
		mediaListPlayer.playNext();
	}

	public static void stopMedia() {
		mediaListPlayer.stop();
	}

	public static void pauseMedia() {
		mediaListPlayer.pause();
	}

	public static void setMediaListPlayer(MediaListPlayer mediaListPlayer) {
		CustomMediaPlayerFactory.mediaListPlayer = mediaListPlayer;
	}

	public static MediaList getMediaList() {
		return mediaList;
	}

	public static void setMediaList(MediaList mediaList) {
		CustomMediaPlayerFactory.mediaList = mediaList;
	}

	public static EmbeddedMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public static void setMediaPlayer(EmbeddedMediaPlayer mediaPlayer) {
		CustomMediaPlayerFactory.mediaPlayer = mediaPlayer;
	}

}
