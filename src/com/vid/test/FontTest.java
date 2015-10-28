package com.vid.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class FontTest extends Frame {

	private static final long serialVersionUID = 4179004561264146676L;

	private String[] fonts;

	private String[] fontIndex;

	private JComboBox<String> fontList;

	private static final String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames();

	private static final String[] stylenames = { "Plain", "Italic", "Bold", "Bold Italic" };

	public FontTest() {

		super("Fonts Combo box");

		setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

		fonts = new String[families.length];
		fontIndex = new String[families.length];

		int fontId = 0;
		for (int f = 0; f < families.length; f++) { // for each family
			fonts[fontId] = families[f];
			fontIndex[fontId] = "" + fontId;
			fontId++;
		}

		fontList = new JComboBox<String>(fontIndex);

		// add components to this frame
		add(fontList);
		fontList.setRenderer(new MyListCellRenderer(fonts));
		pack();
		setLocationRelativeTo(null);

	}

	class MyListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1203372645544348813L;

		private String[] fonts;

		public MyListCellRenderer(String[] fonts) {
			this.fonts = fonts;
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList jc, Object val, int idx, boolean isSelected,
				boolean cellHasFocus) {

			int f = Integer.parseInt(val.toString());

			setText(fonts[f]);
			if (isSelected)
				setBackground(Color.LIGHT_GRAY);
			else
				setBackground(Color.WHITE);

			Font font = new Font(families[f], Font.PLAIN, 18); // createfont
			setFont(font);

			return this;
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new FontTest().setVisible(true);
			}
		});
	}

}
