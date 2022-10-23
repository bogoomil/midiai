package hu.boga.midiai.gui.trackeditor;

import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import hu.boga.midiai.core.musictheory.Pitch;
import hu.boga.midiai.core.musictheory.enums.NoteName;
import hu.boga.midiai.gui.GuiConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class TrackEditorPanel extends Pane {
    private static final int KEYBOARD_OFFSET = 0;
    int measureNum = 100;
    private int resolution;
    private float zoomFactor = 1f;
    private List<NoteDto> notes;
    private ContextMenu contextMenu;

    private EditorModeEnum currentMode = EditorModeEnum.ADD;
    private List<TrackEventListener> trackEventListeners = new ArrayList<>();

    public TrackEditorPanel(){
        createContextMenu();
        addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseClick(event);
            }
        });
    }

    public void addTrackEventListener(TrackEventListener trackEventListener){
        this.trackEventListeners.add(trackEventListener);
    }

    private void createContextMenu() {
        contextMenu = new ContextMenu();
        RadioMenuItem item = new RadioMenuItem("Add mode");
        ToggleGroup toggleGroup = new ToggleGroup();
        item.setToggleGroup(toggleGroup);
        item.setSelected(true);
        item.selectedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                currentMode = newValue ? EditorModeEnum.ADD : EditorModeEnum.DELETE;
            }
        });
        contextMenu.getItems().add(item);
        item = new RadioMenuItem("Delete mode");
        item.setToggleGroup(toggleGroup);
        item.selectedProperty().asObject().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                currentMode =  newValue ? EditorModeEnum.DELETE : EditorModeEnum.ADD;
            }
        });
        contextMenu.getItems().add(item);
    }

    private void handleMouseClick(MouseEvent event) {
        System.out.println("MOUSE EVENT CLICK: " + event.getButton());
        if(event.getButton() == MouseButton.SECONDARY){
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        } else if(event.getButton() == MouseButton.PRIMARY){
            AddNoteEvent addNoteEvent = new AddNoteEvent(getTickByX((int) event.getX()), getPitchByY((int) event.getY()).getMidiCode());
            this.trackEventListeners.forEach(trackEventListener -> trackEventListener.onAddNoteEvent(addNoteEvent));
            System.out.println("TICK: " + getTickByX((int) event.getX()) + " :: current mode: " + currentMode.name());
        }
    }

    Optional<Node> getChild(Point2D point){
        return this.getChildren().filtered(node -> node.contains(point)).stream().findFirst();
    }


    public void paintNotes() {
        this.getChildren().clear();
        initializeCanvas();
        paintVerticalLines();
        paintHorizontalLines();
        paintKeyboard();
        notes.forEach(noteDto -> {
            paintNote(noteDto);
        });
    }

    private void paintKeyboard() {
        List<String> noteNames = Arrays.stream(NoteName.values())
                .sorted(Comparator.comparingInt(NoteName::ordinal).reversed())
                .map(noteName -> noteName.name()).collect(Collectors.toList());
        int increment = getPitchHeight();
        int y = increment;
        for (int i = 0; i <= GuiConstants.OCTAVES; i++) {
            for(int j = 0; j < 12; j++){
                Text text = new Text(noteNames.get(j) + " " + (GuiConstants.OCTAVES - i));
                text.setX(5);
                text.setY(y - 5);
                y += increment;
                this.getChildren().add(text);
            }
        }
    }

    private void paintNote(NoteDto noteDto) {
        try {
            Rectangle rect = getNoteRectangle(noteDto);
            this.getChildren().add(rect);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Rectangle getNoteRectangle(NoteDto noteDto) {
        Rectangle rect = new Rectangle();
        rect.setX((int) (noteDto.tick * getTickWidth()));
        rect.setY(getYByPitch((int) noteDto.midiCode));
        rect.setWidth(this.getTickWidth() * noteDto.lengthInTicks);
        rect.setHeight(getPitchHeight());
        rect.setStroke(Color.ALICEBLUE);
        rect.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("RECTANGLE MOUSE CLICK: " + rect);
                event.consume();
            }
        });
        return rect;
    }

    private void initializeCanvas() {
        this.setPrefWidth(getWorkingWidth());
        this.setPrefHeight(getWorkingHeight());
        this.getChildren().removeAll();
    }

    private void paintVerticalLines() {
        int w32nds = get32ndsWidth();
        int counter = 0;
        for (int x = 0; x < getWorkingWidth(); x += w32nds) {
            Line line = new Line();
            line.setStartX(x);
            line.setStartY(0);
            line.setEndX(x);
            line.setEndY(this.getPrefHeight());
            if (counter % 32 == 0) {
                line.setStroke(Color.RED);
            } else {
                line.setStroke(Color.BLACK);
            }
            counter++;
            this.getChildren().addAll(line);
        }
    }

    private void paintHorizontalLines() {
        for (int y = 0; y < this.getPrefHeight(); y += getPitchHeight()) {
            Line line = new Line();
            line.setStartX(0);
            line.setStartY(y);
            line.setEndX(this.getPrefWidth());
            line.setEndY(y);
            this.getChildren().add(line);
        }
    }


    private int getPitchHeight() {
        return (int) (this.getPrefHeight() / (GuiConstants.OCTAVES * 12));
    }

    private int get32ndsWidth() {
        return (int) (((resolution * 4) / 32) * zoomFactor);
    }

    private int getWorkingWidth() {
        return (int) (getMeasureWidth() * measureNum);
    }

    private double getMeasureWidth() {
        return get32ndsWidth() * 32;
    }

    private int getWorkingHeight() {
        return GuiConstants.OCTAVES * GuiConstants.LINE_HEIGHT * 12;
    }

    private double getTickWidth() {
        return getMeasureWidth() / (resolution * 4);
    }

    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public void setNotes(List<NoteDto> notes) {
        this.notes = notes;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    private Pitch getPitchByY(int y) {
        int pitch = (int) ((this.getPrefHeight() / GuiConstants.LINE_HEIGHT - 1) - (y / GuiConstants.LINE_HEIGHT));
        return new Pitch(pitch);
    }

    private int getTickByX(int x) {
        double tickWidth = getTickWidth();
        int tick = (int) ((x - KEYBOARD_OFFSET) / tickWidth);
        return tick;
    }

//    private int getXByTick(int tick, int tickWidth) {
//        return tick * tickWidth + KEYBOARD_OFFSET;
//    }

    private int getYByPitch(int midiCode) {
        return (GuiConstants.OCTAVES * 12 - 1 - midiCode) * GuiConstants.LINE_HEIGHT;
    }

    enum EditorModeEnum {
        ADD, DELETE;
    }

}
