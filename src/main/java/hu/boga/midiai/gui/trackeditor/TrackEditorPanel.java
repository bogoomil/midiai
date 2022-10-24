package hu.boga.midiai.gui.trackeditor;

import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import hu.boga.midiai.core.musictheory.Pitch;
import hu.boga.midiai.core.musictheory.enums.NoteLength;
import hu.boga.midiai.core.musictheory.enums.NoteName;
import hu.boga.midiai.gui.GuiConstants;
import hu.boga.midiai.gui.trackeditor.events.AddNoteEvent;
import hu.boga.midiai.gui.trackeditor.events.DeleteNoteEvent;
import hu.boga.midiai.gui.trackeditor.events.MoveNoteEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class TrackEditorPanel extends Pane {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorPanel.class);

    private static final int KEYBOARD_OFFSET = 0;
    public static final Color DEFAULT_VERTICAL_LINE_COLOR = Color.LIME;
    public static final Color DEFAULT_HORIZONTAL_LINE_COLOR = Color.LIME;
    public static final Paint TEXT_COLOR = Color.WHITE;
    int measureNum = 100;
    private int resolution;
    private float zoomFactor = 1f;
    private List<NoteDto> notes;
    private ContextMenu contextMenu;

    private EditorModeEnum currentMode = EditorModeEnum.ADD;
    private final List<TrackEventListener> trackEventListeners = new ArrayList<>();
    private NoteLength noteLength = NoteLength.HARMICKETTED;

    public TrackEditorPanel() {
        this.createContextMenu();

        this.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
            }
        });

        this.setOnMouseClicked(event -> {
            this.handleMouseClick(event);
        });
    }

    public void addTrackEventListener(final TrackEventListener trackEventListener) {
        trackEventListeners.add(trackEventListener);
    }

    private void createContextMenu() {
        RadioMenuItem[] radioMenuItems = createNoteLengthMenuItem();

        Menu modeMenu = new Menu("Mode", null,
                new Menu("Length", null,
                        createNoteLengthMenuItem()
                        ),
                new Menu("Chords", null,
                        new RadioMenuItem("single note"),

                        new RadioMenuItem("single note"))
                );
//                new MenuItem("Chords"));


        this.contextMenu = new ContextMenu();

        this.contextMenu.getItems().add(modeMenu);

    }

    private RadioMenuItem[] createNoteLengthMenuItem() {
        final ToggleGroup toggleGroupLength = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[NoteLength.values().length];
        for(int i = 0; i < NoteLength.values().length; i++){

            NoteLength currLength = NoteLength.values()[i];

            RadioMenuItem menuItem = new RadioMenuItem(currLength.name());
            menuItem.setToggleGroup(toggleGroupLength);
            int finalI = i;
            menuItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    noteLength = currLength;
                }
            });
            items[i] = menuItem;
        }
        return items;
    }

    private void handleMouseClick(final MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            this.contextMenu.show(this, event.getScreenX(), event.getScreenY());
        } else if (event.getButton() == MouseButton.PRIMARY) {
            final AddNoteEvent addNoteEvent = new AddNoteEvent(this.getTickByX((int) event.getX()), this.getPitchByY((int) event.getY()).getMidiCode(), noteLength.getErtek());
            trackEventListeners.forEach(trackEventListener -> trackEventListener.onAddNoteEvent(addNoteEvent));
        }
    }

    Optional<Node> getChild(final Point2D point) {
        return getChildren().filtered(node -> node.contains(point)).stream().findFirst();
    }


    public void paintNotes() {
        getChildren().clear();
        this.initializeCanvas();
        this.paintVerticalLines();
        this.paintHorizontalLines();
        this.paintKeyboard();
        this.notes.forEach(noteDto -> {
            this.paintNote(noteDto);
        });
    }

    private void paintKeyboard() {
        final List<String> noteNames = Arrays.stream(NoteName.values())
                .sorted(Comparator.comparingInt(NoteName::ordinal).reversed())
                .map(noteName -> noteName.name()).collect(Collectors.toList());
        final int increment = this.getPitchHeight();
        int y = increment;
        for (int i = 0; i <= GuiConstants.OCTAVES; i++) {
            for (int j = 0; j < 12; j++) {
                final Text text = new Text(noteNames.get(j) + " " + (GuiConstants.OCTAVES - i));
                text.setX(5);
                text.setY(y - 5);
                text.setStroke(TrackEditorPanel.TEXT_COLOR);
                y += increment;
                getChildren().add(text);
            }
        }
    }

    private void paintNote(final NoteDto noteDto) {
        try {
            final Rectangle rect = this.createNoteRectangle(noteDto);
            getChildren().add(rect);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private Rectangle createNoteRectangle(final NoteDto noteDto) {

        final int x = (int) (noteDto.tick * this.getTickWidth());

        final NoteRectangle noteRectangle = new NoteRectangle(x, (int) noteDto.midiCode);
        noteRectangle.setX(x);
        noteRectangle.setY(this.getYByPitch((int) noteDto.midiCode));
        noteRectangle.setWidth(getTickWidth() * noteDto.lengthInTicks);
        noteRectangle.setHeight(this.getPitchHeight());

        noteRectangle.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                TrackEditorPanel.this.trackEventListeners.forEach(trackEventListener -> {
                    if (noteRectangle.isDragging()) {
                        trackEventListener.onMoveNoteEvent(new MoveNoteEvent((int) noteDto.tick, (int) noteDto.midiCode, TrackEditorPanel.this.getTickByX((int) noteRectangle.getX())));
                    }
                    noteRectangle.setDragging(false);
                });
            }
        });

        noteRectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                if (event.getClickCount() == 2) {
                    TrackEditorPanel.this.trackEventListeners.forEach(trackEventListener -> {
                        trackEventListener.onDeleteNoteEvent(new DeleteNoteEvent(noteDto.tick, noteDto.midiCode));
                    });
                }
            }
        });

        this.trackEventListeners.forEach(trackEventListener -> {
            noteRectangle.addTrackEventListener(trackEventListener);
        });
        return noteRectangle;
    }

    private void initializeCanvas() {
        setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        setPrefWidth(this.getWorkingWidth());
        setPrefHeight(this.getWorkingHeight());
        getChildren().removeAll();
    }

    private void paintVerticalLines() {
        final int w32nds = this.get32ndsWidth();
        int counter = 0;
        for (int x = 0; x < this.getWorkingWidth(); x += w32nds) {
            final Line line = new Line();
            line.setStartX(x);
            line.setStartY(0);
            line.setEndX(x);
            line.setEndY(getPrefHeight());
            if (counter % 32 == 0) {

                line.setStrokeWidth(3);
                line.setStroke(TrackEditorPanel.DEFAULT_VERTICAL_LINE_COLOR);
            } else {
                line.setStroke(Color.LIME);
            }
            counter++;
            getChildren().addAll(line);
        }
    }

    private void paintHorizontalLines() {
        for (int y = 0; y < getPrefHeight(); y += this.getPitchHeight()) {
            final Line line = new Line();
            line.setStroke(TrackEditorPanel.DEFAULT_HORIZONTAL_LINE_COLOR);
            line.setStartX(0);
            line.setStartY(y);
            line.setEndX(getPrefWidth());
            line.setEndY(y);
            getChildren().add(line);
        }
    }


    private int getPitchHeight() {
        return (int) (getPrefHeight() / (GuiConstants.OCTAVES * 12));
    }

    private int get32ndsWidth() {
        return (int) (((this.resolution * 4) / 32) * this.zoomFactor);
    }

    private int getWorkingWidth() {
        return (int) (this.getMeasureWidth() * this.measureNum);
    }

    private double getMeasureWidth() {
        return this.get32ndsWidth() * 32;
    }

    private int getWorkingHeight() {
        return GuiConstants.OCTAVES * GuiConstants.LINE_HEIGHT * 12;
    }

    private double getTickWidth() {
        return this.getMeasureWidth() / (this.resolution * 4);
    }

    public void setZoomFactor(final float zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public void setNotes(final List<NoteDto> notes) {
        this.notes = notes;
    }

    public void setResolution(final int resolution) {
        this.resolution = resolution;
    }

    private Pitch getPitchByY(final int y) {
        final int pitch = (int) ((getPrefHeight() / GuiConstants.LINE_HEIGHT - 1) - (y / GuiConstants.LINE_HEIGHT));
        return new Pitch(pitch);
    }

    private int getTickByX(final int x) {
        final double tickWidth = this.getTickWidth();
        final int tick = (int) ((x - TrackEditorPanel.KEYBOARD_OFFSET) / tickWidth);
        return tick;
    }

//    private int getXByTick(int tick, int tickWidth) {
//        return tick * tickWidth + KEYBOARD_OFFSET;
//    }

    private int getYByPitch(final int midiCode) {
        return (GuiConstants.OCTAVES * 12 - 1 - midiCode) * GuiConstants.LINE_HEIGHT;
    }

    enum EditorModeEnum {
        ADD, DELETE
    }

}
