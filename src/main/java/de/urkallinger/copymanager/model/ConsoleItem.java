package de.urkallinger.copymanager.model;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public class ConsoleItem {

	private String text;
	private Color color;
	private Node graphic;

	public ConsoleItem(String text, Color color) {
		this.text = text;
		this.color = color;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	public Node getGraphic() {
		return graphic;
	}

	public void setGraphic(Node graphic) {
		this.graphic = graphic;
	}
}
