package hu.boga.midiai.gui.trackeditor;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NoteRectangle extends Rectangle {
    private int length;
    private boolean selected;

    private double startDrag;

    public NoteRectangle() {
        addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setSelected(!isSelected());
                setFill(isSelected() ? Color.RED : Color.BLACK);
                event.consume();
            }
        });
        setOnMouseDragged(e -> {
            if (contains(e.getX(), e.getY())) {
                setX(e.getX() - getWidth() / 2);
//                setY(e.getY() - 60 / 2);
            }
        });    }

    public int getLength() {
        return this.length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }


}
