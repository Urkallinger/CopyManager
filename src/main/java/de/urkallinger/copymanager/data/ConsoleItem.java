package de.urkallinger.copymanager.data;

import org.apache.logging.log4j.Level;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public class ConsoleItem {

    private String text;
    private Color color;
    private Node graphic;

    public ConsoleItem(String text, Level level) {
        this.text = text;
        switch (level.getStandardLevel()) {
        case WARN:
            color = Color.DARKORANGE;
            break;
        case ERROR:
            color = Color.RED;
            break;
        default:
            color = Color.BLACK;
            break;
        }
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
