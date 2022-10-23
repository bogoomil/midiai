package hu.boga.midiai.gui.trackeditor;

import hu.boga.midiai.core.boundaries.TrackBoundaryIn;
import hu.boga.midiai.core.boundaries.TrackBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.gui.SequenceEditorPanelController;
import hu.boga.midiai.gui.controls.InstrumentCombo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackEditorPanelController implements TrackBoundaryOut, TrackEventListener {

    @FXML
    public InstrumentCombo instrumentCombo;
    @FXML
    public TitledPane titledPane;
    @FXML
    public TrackEditorPanel trackEditorPanel;
    @FXML
    public Slider zoomSlider;
    @FXML
    public TextField trackName;
    @FXML
    public Label zoomLabel;
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

        channelCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            trackBoundaryIn.updateProgramChannel(trackId, channelCombo.getSelectionModel().getSelectedIndex(), instrumentCombo.getSelectedProgram());
        });

        instrumentCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            trackBoundaryIn.updateProgramChannel(trackId, channelCombo.getSelectionModel().getSelectedIndex(), instrumentCombo.getSelectedProgram());
        });

        zoomSlider.setMin(10);
        zoomSlider.setMax(400);
        zoomSlider.adjustValue(100);
        zoomLabel.setText("Zoom: 100%");
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                float zoomFactor = newValue.floatValue() / 100;
                trackEditorPanel.setZoomFactor(zoomFactor);
                trackEditorPanel.paintNotes();
                zoomLabel.setText("Zoom: " + newValue.intValue() + "%");

            }
        });
        trackName.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                trackBoundaryIn.updateTrackName(trackId, trackName.getText());
            }
        });

        trackEditorPanel.addTrackEventListener(this);
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
        trackName.setText(trackDto.name);

        trackEditorPanel.setResolution(trackDto.resolution);
        trackEditorPanel.setNotes(Arrays.asList(trackDto.notes));
        trackEditorPanel.paintNotes();
    }

    public void removeTrack(ActionEvent actionEvent) {
        parent.onTrackDeletedEvent(trackId);
    }

    public void setParent(final SequenceEditorPanelController parent) {
        this.parent = parent;
    }


    @Override
    public void onAddNoteEvent(AddNoteEvent event) {
        trackBoundaryIn.addNote(trackId, event.getTick(), event.getPitch(), 1);
    }
}
