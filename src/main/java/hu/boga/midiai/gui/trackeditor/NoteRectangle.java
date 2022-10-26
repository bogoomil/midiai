package hu.boga.midiai.gui.trackeditor;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class NoteRectangle extends Rectangle {
    private static final Logger LOG = LoggerFactory.getLogger(NoteRectangle.class);
    public static final Color SELECTED_COLOR = Color.color(Color.LAWNGREEN.getRed(), Color.LAWNGREEN.getGreen(), Color.LAWNGREEN.getBlue(), 0.7);;
    public static final Color DEFAULT_COLOR = Color.color(Color.DEEPSKYBLUE.getRed(), Color.DEEPSKYBLUE.getGreen(), Color.DEEPSKYBLUE.getBlue(), 0.7);
    private int length;
    private boolean selected;
    private boolean isDragging;

    private int tick;
    private int pitch;

    public NoteRectangle(final int tick, final int pitch) {
        this.tick = tick;
        this.pitch = pitch;
        this.setFill(DEFAULT_COLOR);
        setUpEventHandlers();
    }

    private void setUpEventHandlers() {

        setOnMouseDragged(event -> handleMouseDragged(event));

    }

    private List<TrackEventListener> trackEventListeners = new ArrayList<>();


    private void handleMouseDragged(MouseEvent e) {
        isDragging = true;
        if (contains(e.getX(), e.getY())) {
            setX(e.getX() - getWidth() / 2);
        }
        e.consume();
    }

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
        setFill(isSelected() ? SELECTED_COLOR : DEFAULT_COLOR);
    }

    public void addTrackEventListener(TrackEventListener trackEventListener){
        this.trackEventListeners.add(trackEventListener);
    }

    public void setDragging(boolean b) {
        isDragging = false;
    }


    public int getTick() {
        return this.tick;
    }

    public int getPitch() {
        return this.pitch;
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    public void toggleSlection() {
        setSelected(!isSelected());
    }
}
