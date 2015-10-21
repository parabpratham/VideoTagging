package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.vid.commons.Fonts;
import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.JComponentLog;
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.play.CustomVideoPlayer;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CustomComponent extends JComponent {

	private static final long serialVersionUID = 7426416230230060677L;

	private static final JComponentLog logger = AppLogger.getJComponentLog();

	// Shape related attributes
	private int startX;
	private int startY;
	private int width;
	private int height;

	private Font font = Fonts.SANSERIF;

	private JButton closeButton;

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

		closeButton = new JButton("Close") {

			private static final long serialVersionUID = 6671595156657341960L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon icon = new ImageIcon(getClass().getResource("/icons/close-circle-512.png"));
				if (icon != null) {
					g.drawImage(icon.getImage(), 0, 0, 15, 15, null);
				}
			}
		};
		closeButton.setBounds(startX + width, startY, 15, 15);
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
				closeButton.getParent().getComponentAt(closeButton.getX() - getWidth() + 10, closeButton.getY() + 10)
						.setVisible(false);
				closeButton.setVisible(false);
			}
		});

	}

	public JButton getCloseButton() {
		return closeButton;
	}

	public void setCloseButton(JButton closeButton) {
		this.closeButton = closeButton;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public EmbeddedMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

}
