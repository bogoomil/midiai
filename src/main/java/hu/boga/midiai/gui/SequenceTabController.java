package hu.boga.midiai.gui;

import com.google.common.eventbus.Subscribe;
import hu.boga.midiai.MidiAiApplication;
import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.SequenceDto;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.gui.events.ChannelMappingChangeEvent;
import hu.boga.midiai.guice.GuiceModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class SequenceTabController implements SequenceBoundaryOut {

    private final SequenceBoundaryIn boundaryIn;

    public Label division;
    public Label resolution;
    public Label tickLength;
    public Label ticksPerMeasure;
    public Label ticksIn32nds;
    public Label ticksPerSecond;
    public Label tickSize;
    public Label tempo;
    public Tab tab;

    @FXML
    private TextField tfFilename;
    @FXML
    private Accordion accordion;
    @FXML
    private VBox channelsWrapper;

    private String projectId;

    private ChannelToInstrumentMappingPanel channelToInstrumentMappingPanel;

    @Inject
    public SequenceTabController(SequenceBoundaryIn boundaryInProvider) {
        this.boundaryIn = boundaryInProvider;
        MidiAiApplication.EVENT_BUS.register(this);
    }

    public void initialize()  {
//        createChannelMappingsPanel();
    }

    public void saveSequence(ActionEvent actionEvent) {
        String path = new FileChooser().showSaveDialog(null).getAbsolutePath();
        this.boundaryIn.saveSequence(projectId, path);
    }

    public void onPlayCurrentSec(ActionEvent actionEvent) {
        this.boundaryIn.playLoop(projectId, 960, 1920);
    }

    public void stopPlayback(ActionEvent actionEvent) {
        this.boundaryIn.stopPlayBack(projectId);
    }

    @Override
    public void displaySequence(SequenceDto sequenceDto) {
        this.tab.setText(sequenceDto.name);
        this.tfFilename.setText(sequenceDto.name);
        this.division.setText("division: " + sequenceDto.division + "");
        this.resolution.setText("resolution: " + sequenceDto.resolution + "");
        this.tickLength.setText("tick length: " + sequenceDto.tickLength + "");
        this.ticksPerMeasure.setText("ticks / measure: " + sequenceDto.ticksPerMeasure + " (4 * resolution)");
        this.ticksIn32nds.setText("ticks in 32nds: " + sequenceDto.ticksIn32nds + " (ticks per measure / 32)");
        this.ticksPerSecond.setText("ticks / sec: " + sequenceDto.ticksPerSecond + " (resolution * (tempo / 60))");
        this.tickSize.setText("tick size: " + sequenceDto.tickSize + " (1 / ticks per second)");
        this.tempo.setText("tempo: " + sequenceDto.tempo);
        this.projectId = sequenceDto.id;

        initChildren(sequenceDto);

    }

    private void initChildren(SequenceDto sequenceDto) {
        sequenceDto.tracks.forEach(trackDto -> {
            try {
                addTrackPanel(trackDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void initSequence() {
        this.boundaryIn.initNewSequence();
    }

    public void initSequence(File file) {
        this.boundaryIn.openFile(file);
    }

    private void addTrackPanel(String trackId) throws IOException {
        FXMLLoader loader = new FXMLLoader(ChannelToInstrumentMappingPanel.class.getResource("track-editor-panel.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        TitledPane trackEditor =  loader.load();

        TrackEditorPanelController trackEditorPanelController = loader.getController();
        trackEditorPanelController.setTrackId(trackId);

        accordion.getPanes().add(trackEditor);
    }

}
