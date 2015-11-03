package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextPane;

import com.vid.commons.Fonts;
import com.vid.commons.SupportedColors;
import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.JComponentLog;
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.JComponentType;
import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.play.CustomVideoPlayer;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CustomJComponent extends JComponent {

	private static final long serialVersionUID = 7426416230230060677L;

	protected static final JComponentLog logger = AppLogger.getJComponentLog();

	private String id;

	// Shape related attributes
	private Integer startX;
	private Integer startY;
	private Integer width;
	private Integer height;

	private Font font = Fonts.getAppFont();

	private JButton closeButton;

	private Color bgColor;
	private boolean fillSelected;

	private String hoverString;

	private SHAPE_TYPE shpe;

	private final JFrame mainFrame = CustomVideoPlayer.getMainFrame();

	private final COMPONENT_TYPE component_TYPE = COMPONENT_TYPE.JCOMPONENT;

	private JComponentType jComponentType;

	protected JTextPane textPane;

	public CustomJComponent() {
		super();
	}

	public CustomJComponent(Integer startX, Integer startY, Integer width, Integer height, String hoverString) {

		super();

		setStartX(startX);
		setStartY(startY);
		setWidth(width);
		setHeight(height);
		setHoverString(hoverString);

		// defineParameter();
	}

	protected void defineParameter() {
		setOpaque(false);
		setBounds(getStartX(), getStartY(), getWidth(), getHeight());
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
		closeButton.setBackground(new Color(0, 0, 0, 0));
		closeButton.setBounds(startX + width, startY, 15, 15);
		closeButton.setToolTipText("hide");

		// Register events for media,frame
		registerListeners();
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setBgColor(Color bgColor) {
		if (bgColor == null)
			this.bgColor = new SupportedColors(Color.black, 0);
		else
			this.bgColor = bgColor;
	}

	public Integer getStartX() {
		return startX;
	}

	public void setStartX(Integer startX) {
		this.startX = startX;
	}

	public Integer getStartY() {
		return startY;
	}

	public void setStartY(Integer startY) {
		this.startY = startY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public SHAPE_TYPE getShpe() {
		return shpe;
	}

	public void setShpe(SHAPE_TYPE shpe) {
		this.shpe = shpe;
	}

	public COMPONENT_TYPE getComponentType() {
		return component_TYPE;
	}

	public boolean getFillSelected() {
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
				Component componentAt = closeButton.getParent().getComponentAt(closeButton.getX() - getWidth() + 10,
						closeButton.getY() + 10);
				componentAt.setVisible(false);
				closeButton.setVisible(false);
				if (textPane != null)
					textPane.setVisible(false);
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
		return CustomVideoPlayer.getMediaPlayer();
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public void setTextPane() {
		textPane = new JTextPane();
		textPane.setBackground(getBgColor());
		// TODO set color of text
		textPane.setFont(getFont());
		textPane.setText(getDisplayString());
	}

	public String getDisplayString() {
		return "";
	}

	public void setTextPaneBounds(Integer x, Integer y, Integer width, Integer height) {
		getTextPane().setBounds(x, y, width, height);
	}

	public JComponentType getjComponentType() {
		return jComponentType;
	}

	public void setjComponentType(JComponentType jComponentType) {
		this.jComponentType = jComponentType;
	}

	public COMPONENT_TYPE getComponent_TYPE() {
		return component_TYPE;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
