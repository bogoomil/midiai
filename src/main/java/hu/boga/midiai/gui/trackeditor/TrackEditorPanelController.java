package hu.boga.midiai.gui.trackeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import hu.boga.midiai.core.tracks.boundary.TrackBoundaryIn;
import hu.boga.midiai.core.tracks.boundary.TrackBoundaryOut;
import hu.boga.midiai.core.tracks.boundary.NoteDto;
import hu.boga.midiai.core.tracks.boundary.TrackDto;
import hu.boga.midiai.gui.controls.InstrumentCombo;
import hu.boga.midiai.gui.events.TrackDeleteEvent;
import hu.boga.midiai.gui.trackeditor.events.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackEditorPanelController implements TrackBoundaryOut {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorPanelController.class);

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

    int trackIndex;

    private EventBus eventBus;

    @Inject
    public TrackEditorPanelController(TrackBoundaryIn trackBoundaryIn) {
        this.trackBoundaryIn = trackBoundaryIn;
    }

    public void initialize() {
        channelCombo.getItems().addAll(IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()));

        channelCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(eventBus != null){
                eventBus.post(new ProgramChangedEvent(trackIndex, instrumentCombo.getSelectedProgram(), channelCombo.getSelectionModel().getSelectedIndex()));
            }
        });

        instrumentCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(eventBus != null){
                eventBus.post(new ProgramChangedEvent(trackIndex, instrumentCombo.getSelectedProgram(), channelCombo.getSelectionModel().getSelectedIndex()));
            }
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
//        trackName.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                trackBoundaryIn.updateTrackName(trackIndex, trackName.getText());
//            }
//        });
    }

    public void setTrackIndex(String seqId, int trackIndex){
        this.trackIndex = trackIndex;
        trackBoundaryIn.showTrack(seqId, this.trackIndex);
    }

    @Override
    public void dispayTrack(TrackDto trackDto) {
        trackIndex = trackDto.trackIndex;
        titledPane.setText("ch: " + trackDto.channel + " pr:" + trackDto.program + " notes: " + trackDto.noteCount + " (" + trackIndex + ")");
        channelCombo.getSelectionModel().select(trackDto.channel);
        instrumentCombo.selectInstrument(trackDto.program);
        trackName.setText(trackDto.name);

        trackEditorPanel.setResolution(trackDto.resolution);
        trackEditorPanel.setNotes(Arrays.asList(trackDto.notes));
        trackEditorPanel.paintNotes();
    }

    public void removeTrack(ActionEvent actionEvent) {
        eventBus.post(new TrackDeleteEvent(trackIndex));
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
        trackEditorPanel.setEventBus(eventBus);
    }


//    @Subscribe
//    public void onAddNoteEvent(AddNoteEvent event) {
//        trackBoundaryIn.addNote(trackIndex, event.getTick(), event.getPitch(), event.getLength());
//    }
//
//    @Subscribe
//    public void onAddChordEvent(AddChordEvent event) {
//        trackBoundaryIn.addChord(trackIndex, event.getTick(), event.getPitch(), event.getLength(), event.getChordType());
//    }
//
//    @Subscribe
//    public void onMoveNoteEvent(MoveNoteEvent event) {
//        trackBoundaryIn.noteMoved(trackIndex, event.getTick(), event.getPitch(), event.getNewTick());
//    }
//
//    @Subscribe
//    public void onDeleteNoteEvent(DeleteNoteEvent... events) {
//        List<NoteDto> dtos = Arrays.stream(events).map(event -> new NoteDto(event.getPitch(), event.getTick(), 0)).collect(Collectors.toList());
//        trackBoundaryIn.deleteNote(trackIndex, dtos.toArray(NoteDto[]::new));
//
//    }
}
