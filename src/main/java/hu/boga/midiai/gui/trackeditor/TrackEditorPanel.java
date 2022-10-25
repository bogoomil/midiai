package hu.boga.midiai.gui.trackeditor;

import com.google.common.eventbus.Subscribe;
import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import hu.boga.midiai.core.musictheory.Pitch;
import hu.boga.midiai.core.musictheory.Scale;
import hu.boga.midiai.core.musictheory.enums.ChordType;
import hu.boga.midiai.core.musictheory.enums.NoteLength;
import hu.boga.midiai.core.musictheory.enums.NoteName;
import hu.boga.midiai.core.musictheory.enums.Tone;
import hu.boga.midiai.gui.GuiConstants;
import hu.boga.midiai.gui.trackeditor.events.*;
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
    public static final Color DISABLED_COLOR = Color.GAINSBORO;
    int measureNum = 100;
    private int resolution;
    private float zoomFactor = 1f;
    private List<NoteDto> notes;
    private ContextMenu contextMenu;

    private EditorModeEnum currentMode = EditorModeEnum.ADD;
    private final List<TrackEventListener> trackEventListeners = new ArrayList<>();
    private NoteLength currentNoteLength = NoteLength.HARMICKETTED;
    private ChordType currentChordType = null;
    private Tone currentTone = null;
    private NoteName currentRoot = NoteName.C;

    private List<Point2D> selectedPoints = new ArrayList<>(0);

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
        this.contextMenu = new ContextMenu();
        Menu creationMenu = new Menu("Creation", null,
                new Menu("Length", null, createNoteLengthMenuItem()),
                new Menu("Chords", null,createChordMenuItems())
        );


        MenuItem selectAllMenu = new MenuItem("Select all");
        selectAllMenu.addEventHandler(ActionEvent.ACTION, event -> selectAllNotes());
        MenuItem deSelectAllMenu = new MenuItem("Deselect all");
        deSelectAllMenu.addEventHandler(ActionEvent.ACTION, event -> deSelectAllNotes());
        Menu selectionMenu = new Menu("Selection", null,
                selectAllMenu,
                deSelectAllMenu
        );

        MenuItem deleteMenu = new MenuItem("selected");
        deSelectAllMenu.addEventHandler(ActionEvent.ACTION, event -> deleteSelectedNotes());
        MenuItem deleteAllMenu = new MenuItem("all");
        deSelectAllMenu.addEventHandler(ActionEvent.ACTION, event -> deleteAllNotes());
        Menu deletionMenu = new Menu("Delete", null,
                deleteMenu,
                deleteAllMenu
        );
        this.contextMenu.getItems().add(creationMenu);
        this.contextMenu.getItems().add(selectionMenu);
        this.contextMenu.getItems().add(deletionMenu);
    }

    private void deleteAllNotes() {
        selectAllNotes();
        deleteSelectedNotes();
    }

    private void deleteSelectedNotes() {
        getAllNoteRectangles().stream().filter(noteRectangle -> noteRectangle.isSelected()).forEach(noteRectangle -> {
            trackEventListeners.forEach(trackEventListener -> {
                trackEventListener.onDeleteNoteEvent(new DeleteNoteEvent(noteRectangle.getTick(), noteRectangle.getPitch()));
            });
        });
    }

    private void selectAllNotes() {
        List<Point2D> allPoints = getAllNoteRectangles().stream().map(noteRectangle -> new Point2D(noteRectangle.getX(), noteRectangle.getY())).collect(Collectors.toList());
        this.selectedPoints.addAll(allPoints);
        paintNotes();
    }

    private void deSelectAllNotes() {
        this.selectedPoints.clear();
        paintNotes();
    }

    private List<NoteRectangle> getAllNoteRectangles() {
        List<NoteRectangle> l = getChildren().stream().filter(node -> node instanceof NoteRectangle).map(node -> (NoteRectangle)node).collect(Collectors.toList());
        LOG.debug("notes " + l);
        return l;
    }

    private RadioMenuItem[] createChordMenuItems() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[ChordType.values().length + 1];
        items[0] = new RadioMenuItem("single note");
        items[0].setToggleGroup(toggleGroup);
        items[0].setSelected(true);
        items[0].addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentChordType = null;
            }
        });
        for (int i = 0; i < ChordType.values().length; i++) {
            ChordType currChordType = ChordType.values()[i];
            RadioMenuItem menuItem = new RadioMenuItem(currChordType.name());
            menuItem.setToggleGroup(toggleGroup);
            int finalI = i;
            menuItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentChordType = currChordType;
                }
            });
            items[i + 1] = menuItem;
        }
        return items;

    }

    private RadioMenuItem[] createNoteLengthMenuItem() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[NoteLength.values().length];
        for (int i = 0; i < NoteLength.values().length; i++) {
            NoteLength currLength = NoteLength.values()[i];
            RadioMenuItem menuItem = new RadioMenuItem(currLength.name());
            menuItem.setToggleGroup(toggleGroup);
            int finalI = i;
            menuItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentNoteLength = currLength;
                }
            });
            items[i] = menuItem;
        }
        items[0].setSelected(true);
        return items;
    }

    private void handleMouseClick(final MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            this.contextMenu.show(this, event.getScreenX(), event.getScreenY());
        } else if (event.getButton() == MouseButton.PRIMARY) {
            final AddNoteEvent addNoteEvent = new AddNoteEvent(this.getTickByX((int) event.getX()), this.getPitchByY((int) event.getY()).getMidiCode(), currentNoteLength.getErtek());
            trackEventListeners.forEach(trackEventListener -> trackEventListener.onAddNoteEvent(addNoteEvent));
        }
    }

    Optional<NoteRectangle> getChildAtPoint(final Point2D point) {
        return getAllNoteRectangles().stream().filter(node -> node.contains(point)).findFirst();
    }

    public void paintNotes() {
        getChildren().clear();
        this.initializeCanvas();
        this.paintVerticalLines();
        this.paintHorizontalLines();
        this.paintKeyboard();
        this.paintDisabled();

        this.notes.forEach(noteDto -> {
            this.paintNote(noteDto);
        });
    }

    private void paintDisabled() {
        if(currentTone != null){
            List<NoteName> scale = Scale.getScale(currentRoot, currentTone);
            for (int y = 0; y < getPrefHeight(); y += this.getPitchHeight()) {
                paintDisabledLines(scale, y);
            }
        }
    }

    private void paintDisabledLines(List<NoteName> scale, int y) {
        NoteName currentNoteName = NoteName.byCode(getPitchByY(y + 5).getMidiCode());
        if(!scale.contains(currentNoteName)){
            paintDisabledRectangle(y);
        }
        if(currentNoteName == currentRoot){
            paintRootMarker(y);
        }
    }

    private void paintRootMarker(int y) {
        Line line = new Line(0, y + getPitchHeight() / 2, getPrefWidth(), y + getPitchHeight() / 2);
        line.setStroke(Color.RED);
        getChildren().add(line);
    }

    private void paintDisabledRectangle(int y) {
        final Rectangle rectangle = new Rectangle();
        rectangle.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        });
        rectangle.setFill(DISABLED_COLOR);
        rectangle.setX(0);
        rectangle.setY(y);
        rectangle.setWidth(getPrefWidth());
        rectangle.setHeight(getPitchHeight());
        getChildren().add(rectangle);
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

    private NoteRectangle createNoteRectangle(final NoteDto noteDto) {

        final int x = (int) (noteDto.tick * this.getTickWidth());
        final NoteRectangle noteRectangle = new NoteRectangle(x, (int) noteDto.midiCode);
        noteRectangle.setX(x);
        noteRectangle.setY(this.getYByPitch((int) noteDto.midiCode));
        noteRectangle.setWidth(getTickWidth() * noteDto.lengthInTicks);
        noteRectangle.setHeight(this.getPitchHeight());

        noteRectangle.setOnMouseReleased(event -> TrackEditorPanel.this.trackEventListeners.forEach(trackEventListener -> {
            if (noteRectangle.isDragging()) {
                trackEventListener.onMoveNoteEvent(new MoveNoteEvent((int) noteDto.tick, (int) noteDto.midiCode, TrackEditorPanel.this.getTickByX((int) noteRectangle.getX())));
            }
            noteRectangle.setDragging(false);
        }));

        noteRectangle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TrackEditorPanel.this.trackEventListeners.forEach(trackEventListener -> {
                    trackEventListener.onDeleteNoteEvent(new DeleteNoteEvent(noteDto.tick, noteDto.midiCode));
                });
            }
        });

        this.trackEventListeners.forEach(trackEventListener -> noteRectangle.addTrackEventListener(trackEventListener));

        if(selectedPoints.contains(new Point2D(noteRectangle.getX(), noteRectangle.getY()))){
            noteRectangle.setSelected(true);
        }

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

    @Subscribe
    void handleRootChangedEvent(RootChangedEvent event){
        this.currentRoot = event.getNoteName();
        LOG.debug("current root: " + currentRoot);
        paintNotes();
    }

    @Subscribe
    void handleModeChangedEvent(ModeChangedEvent event){
        this.currentTone = event.getTone();
        LOG.debug("current tone: " + currentTone);
        paintNotes();
    }

}
