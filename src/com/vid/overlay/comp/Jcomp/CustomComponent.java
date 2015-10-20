package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.vid.commons.Fonts;
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.play.CustomVideoPlayer;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CustomComponent extends JComponent {

	private static final long serialVersionUID = 7426416230230060677L;

	// Shape related attributes
	private int startX;
	private int startY;
	private int width;
	private int height;

	private Font font = Fonts.SANSERIF;

	private JButton closeButton = new JButton("Close");

	private Color bgColor;
	private boolean fillSelected;

	private String hoverString;

	private SHAPE_TYPE shpe;

	private final JFrame mainFrame = CustomVideoPlayer.getMainFrame();
	private final EmbeddedMediaPlayer mediaPlayer = CustomVideoPlayer.getMediaPlayer();

	private static final COMPONENT_TYPE component_TYPE = COMPONENT_TYPE.JCONPONENT;

	public CustomComponent(int startX, int startY, int width, int height, String hoverString) {

		super();

		setOpaque(false);

		setStartX(startX);
		setStartY(startY);
		setWidth(width);
		setHeight(height);
		setHoverString(hoverString);

		setBounds(startX, startY, width, height);
		closeButton.setBounds(startX + width, startY, 10, 10);
		closeButton.setToolTipText("hide");

		// Register events for media,frame
		registerListeners();

	}

	public CustomComponent() {

		super();
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public SHAPE_TYPE getShpe() {
		return shpe;
	}

	public void setShpe(SHAPE_TYPE shpe) {
		this.shpe = shpe;
	}

	public static COMPONENT_TYPE getComponentType() {
		return component_TYPE;
	}

	public boolean isFillSelected() {
		return fillSelected;
	}

	public void setFillSelected(boolean fillSelected) {
		this.fillSelected = fillSelected;
	}

	public String getHoverString() {
		return hoverString;
	}

	public void setHoverString(String hoverString) {
		this.hoverString = hoverString;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void registerListeners() {

		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				closeButton.getParent().getComponentAt(closeButton.getX() - getWidth(), closeButton.getY()).hide();
			}
		});

	}
}
