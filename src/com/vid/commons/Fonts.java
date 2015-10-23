package com.vid.commons;

import java.awt.Font;

public class Fonts {

	public static final Font SANSERIF = new Font("Sansserif", Font.BOLD, 18);

	private static Font appFont = SANSERIF;

	public static Font getAppFont() {
		return appFont;
	}

	public static void setAppFont(Font appFont) {
		Fonts.appFont = appFont;
	}

}
