package com.vid.play;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class PlayerConfiguration {

	public static final String NATIVE_LIBRARY_SEARCH_PATH = "C:/Program Files/VideoLAN/VLC/";

	public static void main(String[] args) {
		boolean found = new NativeDiscovery().discover();
		System.out.println(found);
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
		System.out.println(LibVlc.INSTANCE.libvlc_get_version());
	}

}
