package de.urkallinger.copymanager.data;

import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Task {

    private UUID id;
    private StringProperty text = new SimpleStringProperty();
    private Node graphic;

    public Task(String text, TaskStatus status) {
        this.id = UUID.randomUUID();
        this.text.set(text);
        setStatus(status);
    }

    public Node getGraphic() {
        return graphic;
    }

    public void setGraphic(Node graphic) {
        this.graphic = graphic;
    }

    public final StringProperty textProperty() {
        return this.text;
    }

    public final String getText() {
        return this.textProperty().get();
    }

    public final void setText(final String text) {
        this.textProperty().set(text);
    }

    public UUID getId() {
        return id;
    }

    public void setStatus(TaskStatus status) {
        switch(status) {
        case SUCCESS:
            graphic = getImageViewByResource("/images/done.png");
            break;
        case FAILURE:
            graphic = getImageViewByResource("/images/clear.png");
            break;
        case IN_PROGRESS:
            graphic = getLoadingIndicator();
            break;
        }
    }

    private ProgressIndicator getLoadingIndicator() {
        final ProgressIndicator pi = new ProgressIndicator();
        pi.setVisible(true);
        pi.setStyle(" -fx-progress-color: #729917;");
        pi.setMaxWidth(20);
        pi.setMaxHeight(20);
        pi.setPrefWidth(20);
        pi.setPrefHeight(20);
        return pi;
    }

    private ImageView getImageViewByResource(String resource) {
        ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream(resource)));
        imgView.setFitHeight(20);
        imgView.setFitWidth(20);
        return imgView;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Task) {
            Task other = (Task) obj;
            if(id.equals(other.getId())) {
                return true;
            }
        }

        return false;
    }
}
