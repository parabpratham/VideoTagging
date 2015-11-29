package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.vid.overlay.comp.master.JComponentType;
import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.play.CustomMediaPlayerFactory;
import com.vid.play.CustomVideoPlayer;
import com.vid.play.overlay.OverLayGenerator;

public class SpotLight extends CustomJComponent implements JCompoWithTextPane {

	private static final long serialVersionUID = -14080388742114574L;

	private Link_type link_type;

	private URI uri;

	private Color displayStringColor;

	private String displayString;

	private Image componentImage;

	private static final SHAPE_TYPE shape_TYPE = SHAPE_TYPE.RECTANGLE;

	private Long skip_time;

	private String link_address;

	private String md_address;

	private static final JComponentType type = JComponentType.SPOT_LIGHT;

	public SpotLight() {
		super();
	}

	// Constructor for same_video
	public SpotLight(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, Font font, Image componentImage, String hoverString, Link_type link_type,
			Long skip_time, boolean setResizeble) {

		super(startX, startY, width, height, hoverString, setResizeble);

		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setLink_type(link_type);
		setSkip_time(skip_time);
		setComponentImage(componentImage);
		setFont(font);
	}

	@Override
	public void defineParameter() {
		super.defineParameter();
		setjComponentType(JComponentType.SPOT_LIGHT);
		setChildTextPane();
		textPane.setVisible(false);

		if (getLink_type() == Link_type.LINK_TO_OTHER_WEB_PAGE)
			try {
				setUri(new URI(getLink_address()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
	}

	// Constructor for other_video
	public SpotLight(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, Font font, Image componentImage, String hoverString, Link_type link_type,
			String media_Address, Long skip_time, boolean setResizeble) {

		super(startX, startY, width, height, hoverString, setResizeble);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setLink_type(link_type);
		setMd_address(media_Address);
		setSkip_time(skip_time);
		setComponentImage(componentImage);
		setFont(font);
	}

	// Constructor for web_link
	public SpotLight(int startX, int startY, int width, int height, Color bgColor, String displayString,
			Color displayStringColor, Font font, Image componentImage, String hoverString, Link_type link_type,
			String link_address, boolean setResizeble) {

		super(startX, startY, width, height, hoverString, setResizeble);
		setBgColor(bgColor);
		setDisplayString(displayString);
		setDisplayStringColor(displayStringColor);
		setLink_type(link_type);
		setLink_address(link_address);
		setComponentImage(componentImage);
		setFont(font);
	}

	@Override
	public void registerListeners() {
		super.registerListeners();
		addMouseListener(new MouseAdapter() {

			private OverLayGenerator generator = CustomVideoPlayer.getOverLayGenerator();

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				logger.trace("Clicked");
				try {
					if (link_type == Link_type.LINK_TO_SAME_VIDEO) {
						generator.enableOverlay(false);
						getMediaPlayer().skip(skip_time - getMediaPlayer().getTime());
						generator.enableOverlay(true);
					} else if (link_type == Link_type.LINK_TO_OTHER_VIDEO) {
						CustomMediaPlayerFactory.addMedia(getMd_address());
						CustomMediaPlayerFactory.stopMedia();
						Thread.sleep(1000);
						CustomMediaPlayerFactory.playMedias(CustomMediaPlayerFactory.getMediaList().size() - 1);
					} else if (link_type == Link_type.LINK_TO_OTHER_WEB_PAGE) {
						CustomMediaPlayerFactory.pauseMedia();
						CustomVideoPlayer.getControlsPanel().setPauseIcon();
						if (uri != null)
							Desktop.getDesktop().browse(uri);
					}
				} catch (InterruptedException | IOException e1) {
					e1.printStackTrace();
				}
			}

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
		});
	}

	@Override
	public void paint(Graphics g) {

		// System.out.println(this.getClass().getName() + " " +
		// SpotLight.class.getName() + ": paint called ");

		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g2.setPaint(getBgColor());
		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.fillRect(0, 0, getWidth(), getHeight());
		if (getComponentImage() != null)
			g2.drawImage(getComponentImage(), 0, 0, getWidth() - 1, getHeight() - 1, null);

	}

	protected void paintComponentOld(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g2.setPaint(getBgColor());
		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.fillRect(0, 0, getWidth(), getHeight());
		if (getComponentImage() != null)
			g2.drawImage(getComponentImage(), 0, 0, getWidth() - 1, getHeight() - 1, null);
	}

	public enum Link_type {
		LINK_TO_SAME_VIDEO, LINK_TO_OTHER_VIDEO, LINK_TO_OTHER_WEB_PAGE;

		public static Link_type getFromName(String name) {
			Link_type[] values = values();
			for (Link_type link_type : values) {
				if (link_type.name().equalsIgnoreCase(name))
					return link_type;
			}
			return null;
		}
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

	public void setSkip_time(Long seek_time) {
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

	public static SHAPE_TYPE getShapeType() {
		return shape_TYPE;
	}

	public void setChildTextPane() {
		super.setTextPane();
		setTextPaneBounds(getStartX(), getStartY() + getHeight(), getWidth(), getFont().getSize() + 5);
	}

	public static JComponentType getType() {
		return type;
	}

	public String getMd_address() {
		return md_address;
	}

	public void setMd_address(String md_address) {
		this.md_address = md_address;
	}

}
