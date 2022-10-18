package hu.boga.midiai.gui.trackeditor;

import hu.boga.midiai.core.boundaries.TrackBoundaryIn;
import hu.boga.midiai.core.boundaries.TrackBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.gui.SequenceEditorPanelController;
import hu.boga.midiai.gui.controls.InstrumentCombo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackEditorPanelController implements TrackBoundaryOut {

    @FXML
    public InstrumentCombo instrumentCombo;
    @FXML
    public TitledPane titledPane;
    @FXML
    public Pane graphicPane;
    @FXML
    public Slider zoomSlider;
    @FXML
    ComboBox<Integer> channelCombo;

    TrackBoundaryIn trackBoundaryIn;

    SequenceEditorPanelController parent;

    String trackId;
    private NotesPainter canvasPainter;

    @Inject
    public TrackEditorPanelController(TrackBoundaryIn trackBoundaryIn) {
        this.trackBoundaryIn = trackBoundaryIn;
    }

    public void initialize() {
        channelCombo.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));
        instrumentCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("observable: " + observable + " old: " + oldValue + " new: " + newValue);
        });
        zoomSlider.setMin(10);
        zoomSlider.setMax(200);
        zoomSlider.adjustValue(100);
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                float zoomFactor = newValue.floatValue() / 100;
                canvasPainter.setZoomFactor(zoomFactor);
                canvasPainter.paintNotes();
            }
        });
        canvasPainter = new NotesPainter(graphicPane);

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

        canvasPainter.setResolution(trackDto.resolution);
        canvasPainter.setNotes(Arrays.asList(trackDto.notes));
        canvasPainter.paintNotes();


    }

    public void removeTrack(ActionEvent actionEvent) {
        parent.onTrackDeletedEvent(trackId);
    }

    public void setParent(final SequenceEditorPanelController parent) {
        this.parent = parent;
    }
}
