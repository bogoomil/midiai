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
    public static final Color SELECTED_COLOR = Color.RED;
    public static final Color DEFAULT_COLOR = Color.DEEPSKYBLUE;
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

//        addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
////                LOG.debug("event type: " + event.getEventType());
//                switch (event.getEventType().getName()){
//                    case "MOUSE_CLICKED":{
//                        handleMouseClick();
//                        break;
//                    }
//                    case "MOUSE_DRAGGED": {
//                        handleMouseDragged(event);
//                        break;
//                    }
//                }
//                event.consume();
//            }
//        });
    }

    private List<TrackEventListener> trackEventListeners = new ArrayList<>();


    private void handleMouseDragged(MouseEvent e) {
        isDragging = true;
        if (contains(e.getX(), e.getY())) {
            setX(e.getX() - getWidth() / 2);
//                setY(e.getY() - 60 / 2);
        }
        e.consume();
    }

    private void handleMouseClick() {
        setSelected(!isSelected());
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
