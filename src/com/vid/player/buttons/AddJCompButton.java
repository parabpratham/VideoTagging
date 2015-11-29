package com.vid.player.buttons;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

import com.vid.commons.ComponentMarker;
import com.vid.commons.ComponentMarker;
import com.vid.overlay.comp.master.JComponentType;
import com.vid.test.CustomOverlayTest;
import com.vid.test.OverlayTest;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class AddJCompButton extends JButton {

	private static final long serialVersionUID = 824307805073873036L;

	private JComponentType componentType;

	public static Map<Integer, ComponentMarker> compMap;

	private static int compCounter = 0;

	public AddJCompButton(JComponentType componentType) {

		super("Add " + componentType.name());
		this.componentType = componentType;

		setToolTipText("Dragto the surface");

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				getMediaPlayer().setOverlay(setOverlay(e.getX(), e.getY()));
				getMediaPlayer().enableOverlay(true);
			}

			private EmbeddedMediaPlayer getMediaPlayer() {
				return OverlayTest.getMediaPlayer();
			}
		});

		setEnabled(false);
	}

	private CustomOverlayTest setOverlay(int startX, int startY) {
		compMap.put(compCounter, new ComponentMarker(startX + 1200, startY, componentType));
		System.err.println(startX + " " + startY + " " + compMap.size());
		return new CustomOverlayTest(null, compMap);
	}

	public static Map<Integer, ComponentMarker> getCompMap() {
		return compMap;
	}

	public static void setCompMap() {
		AddJCompButton.compMap = new HashMap<>();
		compCounter = 0;
	}

	public static void removeCompFromMap(int key) {
		compMap.remove(key);
	}

}
