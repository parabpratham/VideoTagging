package com.vid.commons;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class ColorListRenderer extends JLabel implements ListCellRenderer<Object> {

	private static final long serialVersionUID = 8911057101556874922L;

	public ColorListRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		setText(value.toString());
		switch (index) {
		case 0:
			setBackground(Color.white);
			break;
		case 1:
			setBackground(Color.red);
			break;
		case 2:
			setBackground(Color.blue);
			break;
		case 3:
			setBackground(Color.yellow);
			break;
		case 4:
			setBackground(Color.green);
			break;
		case 5:
			setBackground(Color.gray);
			break;
		}
		return this;
	}
}