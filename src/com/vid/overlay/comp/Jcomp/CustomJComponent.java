package com.vid.overlay.comp.Jcomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

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
import com.vid.player.buttons.AddJCompButton;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CustomJComponent extends JComponent {

	private static final long serialVersionUID = 7426416230230060677L;

	protected static final JComponentLog logger = AppLogger.getJComponentLog();

	private String id;

	private boolean resizeble = false;

	private JButton resizeEast;
	private JButton resizeDown;
	private JButton resizeCross;

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

	public CustomJComponent(Integer startX, Integer startY, Integer width, Integer height, String hoverString,
			boolean setResizeble) {

		super();
		setResizeble(setResizeble);
		setStartX(startX);
		setStartY(startY);
		setWidth(width);
		setHeight(height);
		setHoverString(hoverString);

		// defineParameter();
	}

	protected void defineParameters() {
		setOpaque(false);
		setBounds(getStartX(), getStartY(), getWidth(), getHeight());
		closeButton = new JButton("Close") {
			private static final long serialVersionUID = 6671595156657341960L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon icon = new ImageIcon(getClass().getResource("/icons/close-circle-512.png"));
				if (icon != null) {
					g.drawImage(icon.getImage(), startX + width, startY, 15, 15, null);
				}
			}
		};
		closeButton.setBackground(new Color(0, 0, 0, 0));
		closeButton.setSize(15, 15);
		closeButton.setToolTipText("hide");

		if (resizeble) {

			closeButton.setBounds(startX + width + 5, startY, 15, 15);

			resizeEast.setBounds(startX + width, startY, 5, getHeight());
			resizeEast.setToolTipText("Drag to expand Expand");

			resizeDown.setBounds(startX, startY + height, getWidth(), 5);
			resizeDown.setToolTipText("Drag to Expand");

			resizeCross.setBounds(startX + width, startY + height, 5, 5);
			resizeCross.setToolTipText("Drag to Expand");

		} else
			closeButton.setBounds(startX + width, startY, 15, 15);

		// Register events for media,frame
		registerListeners();

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
				if (resizeEast != null) {
					resizeEast.setVisible(false);
					resizeDown.setVisible(false);
					resizeCross.setVisible(false);
					try {
						AddJCompButton.removeCompFromMap(Integer.parseInt(getId()));
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}
		});

		if (resizeEast != null) {
			resizeEast.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent evt) {
					X1 = evt.getX();
				}

				public void mouseReleased(java.awt.event.MouseEvent evt) {
					resizeEastMouseReleased(evt);
				}
			});
			resizeEast.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
				public void mouseDragged(java.awt.event.MouseEvent evt) {
					resizeEastMouseDragged(evt);
				}
			});

		}

		if (resizeDown != null) {
			resizeDown.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					Y1 = e.getY();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					resizeDownMouseReleased(e);
				}

			});

			resizeDown.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					resizeDownMouseDragged(e);
				}
			});
		}

		if (resizeCross != null) {
			resizeCross.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					X1 = e.getX();
					Y1 = e.getY();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					resizeDownMouseReleased(e);
					resizeEastMouseReleased(e);
				}
			});

			resizeCross.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					resizeEastMouseDragged(e);
					resizeDownMouseDragged(e);
				}
			});
		}
	}

	int X1 = 0;
	int X2 = 0;

	int Y1 = 0;
	int Y2 = 0;

	private void resizeEastMouseReleased(java.awt.event.MouseEvent evt) {
		// update the location and size of all child components
	}

	private void resizeDownMouseReleased(java.awt.event.MouseEvent evt) {
		// update the location and size of all child components
	}

	private void resizeDownMouseDragged(java.awt.event.MouseEvent evt) {
		// resize jframe on the fly
		Y2 = evt.getY();
		// set minimum size to 100 high
		if ((getHeight() - (Y1 - Y2)) < 40) {
			Y2 = Y1;
		}

		System.out.println("set size " + getWidth() + "," + (getHeight() - (Y1 - Y2)));
		// resize east side only
		try {
			this.setSize(getWidth(), (getHeight() - (Y1 - Y2)));
			setHeight((getHeight() - (Y1 - Y2)));
			this.closeButton.setBounds(getStartX() + getWidth() + 5, getStartY(), 15, 15);
			this.resizeEast.setBounds(getStartX() + getWidth(), getStartY(), 5, getHeight());
			this.resizeDown.setBounds(getStartX(), getStartY() + getHeight(), getWidth(), 5);
			resizeCross.setBounds(getStartX() + getWidth(), getStartY() + getHeight(), 5, 5);
			this.repaint();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void resizeEastMouseDragged(java.awt.event.MouseEvent evt) {
		// resize jframe on the fly
		X2 = evt.getX();
		// set minimum size to 100 wide
		if ((getWidth() - (X1 - X2)) < 100) {
			X2 = X1;
		}

		System.out.println("set size " + (getWidth() - (X1 - X2)) + "," + getHeight());
		// resize east side only
		try {
			setSize(getWidth() - (X1 - X2), getHeight());
			setWidth(getWidth() - (X1 - X2));
			closeButton.setBounds(getStartX() + getWidth() + 5, getStartY(), 15, 15);
			resizeEast.setBounds(getStartX() + getWidth(), getStartY(), 5, getHeight());
			resizeDown.setBounds(getStartX(), getStartY() + getHeight(), getWidth(), 5);
			resizeCross.setBounds(getStartX() + getWidth(), getStartY() + getHeight(), 5, 5);
			repaint();

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public boolean isResizeble() {
		return resizeble;
	}

	public void setResizeble(boolean setResizeble) {
		this.resizeble = setResizeble;
		resizeEast = new javax.swing.JButton() {

			private static final long serialVersionUID = 3545544638208389216L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				setBackground(new SupportedColors(SupportedColors.white, 80));
			}
		};

		resizeDown = new javax.swing.JButton() {

			private static final long serialVersionUID = 3545544638208389216L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				setBackground(new SupportedColors(SupportedColors.white, 80));
			}
		};

		resizeCross = new javax.swing.JButton() {

			private static final long serialVersionUID = 3545544638208389216L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				setBackground(new SupportedColors(SupportedColors.white, 80));
			}
		};

	}

	public JButton getResizeEast() {
		return resizeEast;
	}

	public void setResizeEast(JButton resizeEast) {
		this.resizeEast = resizeEast;
	}

	public JButton getResizeDown() {
		return resizeDown;
	}

	public void setResizeDown(JButton resizeDown) {
		this.resizeDown = resizeDown;
	}

	public JButton getResizeCross() {
		return resizeCross;
	}

	public void setResizeCross(JButton resizeCross) {
		this.resizeCross = resizeCross;
	}

}
