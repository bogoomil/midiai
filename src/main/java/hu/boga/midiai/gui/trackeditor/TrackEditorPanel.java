package hu.boga.midiai.gui.trackeditor;

import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import hu.boga.midiai.core.musictheory.Pitch;
import hu.boga.midiai.core.musictheory.enums.NoteName;
import hu.boga.midiai.gui.GuiConstants;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TrackEditorPanel extends Pane {
    private static final int KEYBOARD_OFFSET = 0;
    int measureNum = 100;
    private int resolution = 120;
    private float zoomFactor = 1f;

    private List<NoteDto> notes;

    public TrackEditorPanel(){
        addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("mouse moved: " + event.getY() + "-> pitch: " + getPitchByY((int) event.getY()) + " y by pitch: " + getYByPitch(getPitchByY((int) event.getY()).getMidiCode()));
                System.out.println("tick: " + getTickByX((int) event.getX()));
            }
        });
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
        for (int i = 0; i < GuiConstants.OCTAVES; i++) {
            for(int j = 0; j < 12; j++){
                System.out.println("y: " + y);
                Text text = new Text(noteNames.get(j) + " " + i);
                text.setX(5);
                text.setY(y - 5);
               // text.setStroke(new Paint());
                y += increment;
                this.getChildren().add(text);
            }
        }
    }

    private void paintNote(NoteDto noteDto) {
        System.out.println("NOTE: " + noteDto.tick + " - " + noteDto.midiCode);
        try {
            Rectangle rect = getNoteRectangle(noteDto);
            this.getChildren().add(rect);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Rectangle getNoteRectangle(NoteDto noteDto) {
        Rectangle rect = new Rectangle();
        rect.setX((int) (noteDto.tick * getTickWidth()));
        rect.setY(getYByPitch((int) noteDto.midiCode));
        rect.setWidth(this.getTickWidth() * noteDto.lengthInTicks);
        rect.setHeight(getPitchHeight());
        rect.setStroke(Color.ALICEBLUE);
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

}
