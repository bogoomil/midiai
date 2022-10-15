package hu.boga.midiai.gui;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.SequenceDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;

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

    private String midiProjectId;

    @Inject
    public SequenceTabController(SequenceBoundaryIn boundaryInProvider) {
        this.boundaryIn = boundaryInProvider;
    }

    public void initialize(){
    }

    public void saveSequence(ActionEvent actionEvent) {
    }

    public void onPlayCurrentSec(ActionEvent actionEvent) {
        this.boundaryIn.playLoop(midiProjectId, 960, 1920);
    }

    public void stopPlayback(ActionEvent actionEvent) {
        this.boundaryIn.stopPlayBack(midiProjectId);
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
        this.midiProjectId = sequenceDto.id;

    }

    public void initSequence() {
        this.boundaryIn.initNewSequence();
    }

    public void initSequence(File file) {
        this.boundaryIn.openFile(file);
    }
}
