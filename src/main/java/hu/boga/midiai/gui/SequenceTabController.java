package hu.boga.midiai.gui;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.SequenceDto;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;

public class SequenceTabController implements SequenceBoundaryOut {

    public Label division;
    public Label resolution;
    public Label tickLength;
    public Label ticksPerMeasure;
    public Label ticksIn32nds;
    public Tab tab;

    private String midiProjectId;

    private final SequenceBoundaryIn boundaryIn;

    @Inject
    public SequenceTabController(Provider<SequenceBoundaryIn> boundaryInProvider) {
        this.boundaryIn = boundaryInProvider.get();
    }

    public void initialize(){
    }

    public void saveSequence(ActionEvent actionEvent) {
    }

    public void onPlayCurrentSec(ActionEvent actionEvent) {
        this.boundaryIn.playSequence(midiProjectId, 0);
    }

    public void stopPlayback(ActionEvent actionEvent) {
        this.boundaryIn.stopPlayBack(midiProjectId);
    }

    @Override
    public void displaySequence(SequenceDto sequenceDto) {
        this.tab.setText(sequenceDto.name);

        this.division.setText("division: " + sequenceDto.division + "");
        this.resolution.setText("resolution: " + sequenceDto.resolution + "");
        this.tickLength.setText("tick length: " + sequenceDto.tickLength + "");
        this.ticksPerMeasure.setText("ticks / measure: " + sequenceDto.ticksPerMeasure + "");
        this.ticksIn32nds.setText("ticks in 32nds: " + sequenceDto.ticksIn32nds + "");

        this.midiProjectId = sequenceDto.id;
    }

    public void initSequence() {
        this.boundaryIn.initNewSequence();
    }

    public void initSequence(File file) {
        this.boundaryIn.openFile(file);
    }
}
