package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JTextPane;

import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.play.CustomVideoPlayer;
import com.vid.play.OverLayGenerator;
import com.vid.test.CustomMediaPlayerFactory;

public class SpotLight extends CustomComponent {

	private static final long serialVersionUID = -14080388742114574L;

	private Link_type link_type;

	private URI uri;

	private Color displayStringColor;

	private String displayString;

	private Image componentImage;

	private static final SHAPE_TYPE shape_TYPE = SHAPE_TYPE.RECTANGLE;

	private static final COMPONENT_TYPE component_type = COMPONENT_TYPE.JCONPONENT;

	private long skip_time;

	private String link_address;

	private String media_address;

	private JTextPane textPane;

	// Constructor for same_video
	public SpotLight(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, Image componentImage, String hoverString, Link_type link_type, int skip_time) {

		super(startX, startY, width, height, hoverString);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setLink_type(link_type);
		setSkip_time(skip_time);
		setComponentImage(componentImage);
		setTextArea();
		textPane.setVisible(false);

	}

	// Constructor for other_video
	public SpotLight(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, Image componentImage, String hoverString, Link_type link_type,
			String media_Address, int skip_time) {

		super(startX, startY, width, height, hoverString);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setLink_type(link_type);
		setMedia_address(media_Address);
		setSkip_time(skip_time);
		setComponentImage(componentImage);
		setTextArea();
		textPane.setVisible(false);
	}

	// Constructor for web_link
	public SpotLight(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, Image componentImage, String hoverString, Link_type link_type,
			String link_address) {

		super(startX, startY, width, height, hoverString);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setLink_type(link_type);
		setLink_address(link_address);
		setComponentImage(componentImage);
		setTextArea();
		try {
			setUri(new URI(getLink_address()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		textPane.setVisible(false);
	}

	@Override
	public synchronized void addMouseListener(MouseListener l) {
		l = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				textPane.setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				textPane.setVisible(false);
			}
		};

		super.addMouseListener(l);
	}

	@Override
	public void registerListeners() {
		super.registerListeners();
		addMouseListener(new MouseAdapter() {

			private final OverLayGenerator generator = CustomVideoPlayer.getOverLayGenerator();

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				System.out.println("Clicked");
				try {
					if (link_type == Link_type.LINK_TO_SAME_VIDEO) {
						generator.enableOverlay(false);
						getMediaPlayer().stop();
						getMediaPlayer().play();
						getMediaPlayer().skip(skip_time);
						generator.enableOverlay(true);
					} else if (link_type == Link_type.LINK_TO_OTHER_VIDEO) {
						CustomMediaPlayerFactory.addMedia(getMedia_address());
						CustomMediaPlayerFactory.stopMedia();
						Thread.sleep(1000);
						CustomMediaPlayerFactory.playMedias(CustomMediaPlayerFactory.getMediaList().size() - 1);
					} else if (link_type == Link_type.LINK_TO_OTHER_WEB_PAGE) {
						getMediaPlayer().pause();
						CustomVideoPlayer.getControlsPanel().setPauseIcon();
						if (uri != null)
							Desktop.getDesktop().browse(uri);
					}
				} catch (InterruptedException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g2.setPaint(getBgColor());
		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.fillRect(0, 0, getWidth(), getHeight());
		if (getComponentImage() != null)
			g2.drawImage(getComponentImage(), 0, 0, getWidth() - 1, getHeight() - 1, null);
		g2.setPaint(Color.black);
		g2.setFont(getFont());

		// g2.drawString(getDisplayString(), 10, 16);
	}

	public enum Link_type {
		LINK_TO_SAME_VIDEO, LINK_TO_OTHER_VIDEO, LINK_TO_OTHER_WEB_PAGE;
	}

	public Link_type getLink_type() {
		return link_type;
	}

	public void setLink_type(Link_type link_type) {
		this.link_type = link_type;
	}

	public Color getDisplayStringColor() {
		return displayStringColor;
	}

	public void setDisplayStringColor(Color displayStringColor) {
		this.displayStringColor = displayStringColor;
	}

	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public Image getComponentImage() {
		return componentImage;
	}

	public void setComponentImage(Image componentImage) {
		this.componentImage = componentImage;
	}

	public long getSkip_time() {
		return skip_time;
	}

	public void setSkip_time(long seek_time) {
		this.skip_time = seek_time;
	}

	public String getLink_address() {
		return link_address;
	}

	public void setLink_address(String link_address) {
		this.link_address = link_address;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getMedia_address() {
		return media_address;
	}

	public void setMedia_address(String media_address) {
		this.media_address = media_address;
	}

	public static SHAPE_TYPE getShapeType() {
		return shape_TYPE;
	}

	public static COMPONENT_TYPE getComponentType() {
		return component_type;
	}

	public JTextPane getTextArea() {
		return textPane;
	}

	public void setTextArea() {
		textPane = new JTextPane();
		textPane.setBackground(new Color(255, 0, 0, 50));
		//textPane.setCaretColor(getDisplayStringColor());
		textPane.setText(getDisplayString());
		textPane.setBounds(getStartX(), getStartY() + getHeight(), getWidth(), getFont().getSize() + 5);
	}

	public void setTextArea(JTextPane textArea) {
		this.textPane = textArea;
	}

}
