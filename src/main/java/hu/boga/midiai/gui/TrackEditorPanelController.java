package hu.boga.midiai.gui;

import hu.boga.midiai.MidiAiApplication;
import hu.boga.midiai.core.boundaries.TrackBoundaryIn;
import hu.boga.midiai.core.boundaries.TrackBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.gui.controls.InstrumentCombo;
import hu.boga.midiai.gui.events.TrackDeleteEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackEditorPanelController implements TrackBoundaryOut {

    @FXML
    public InstrumentCombo instrumentCombo;
    @FXML
    public TitledPane titledPane;
    @FXML
    public Canvas canvas;
    @FXML
    ComboBox<Integer> channelCombo;

    TrackBoundaryIn trackBoundaryIn;

    SequenceEditorPanelController parent;

    String trackId;

    @Inject
    public TrackEditorPanelController(TrackBoundaryIn trackBoundaryIn) {
        this.trackBoundaryIn = trackBoundaryIn;
    }

    public void initialize() {
        channelCombo.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));
        instrumentCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("observable: " + observable + " old: " + oldValue + " new: " + newValue);
        });
    }

    public void setTrackId(String trackId){
        this.trackId = trackId;
        trackBoundaryIn.showTrack(trackId);
    }

    @Override
    public void dispayTrack(TrackDto trackDto) {
        trackId = trackDto.trackId;
        titledPane.setText("ch: " + trackDto.channel + " pr:" + trackDto.program + " notes: " + trackDto.noteCount + " (" + trackId + ")");
        channelCombo.getSelectionModel().select(trackDto.channel);
        instrumentCombo.selectInstrument(trackDto.program);

        displayNotes(trackDto.notes);


    }

    private void displayNotes(NoteDto[] notes){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Arrays.stream(notes).forEach(noteDto -> {
            int tickWidth = 20;
            int rowHeight = 20;
            int szorzo = atomicInteger.getAndIncrement();
            int x = tickWidth * szorzo;
            int y = rowHeight * szorzo;
            gc.fillText("" + noteDto, x, y);
        });

        CanvasPainter canvasPainter = new CanvasPainter(canvas);
        canvasPainter.paintNotes(Arrays.asList(notes));
    }

    public void removeTrack(ActionEvent actionEvent) {
        parent.onTrackDeletedEvent(trackId);
    }

    public void setParent(final SequenceEditorPanelController parent) {
        this.parent = parent;
    }
}
