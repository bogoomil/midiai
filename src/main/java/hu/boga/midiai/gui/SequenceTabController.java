package hu.boga.midiai.gui;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.SequenceDto;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import javax.inject.Inject;
import javax.inject.Provider;

public class SequenceTabController implements SequenceBoundaryOut {

    public Label division;
    public Label resolution;
    public Label tickLength;
    public Label ticksPerMeasure;
    public Label ticksIn32nds;
    public Tab tab;

    private final SequenceBoundaryIn boundaryIn;

    @Inject
    public SequenceTabController(Provider<SequenceBoundaryIn> boundaryInProvider) {
        this.boundaryIn = boundaryInProvider.get();
    }

    public void initialize(){
        this.boundaryIn.initNewSequence();
    }

    public void saveSequence(ActionEvent actionEvent) {
    }

    public void onPlayCurrentSec(ActionEvent actionEvent) {
    }

    public void stopPlayback(ActionEvent actionEvent) {
    }

    @Override
    public void displaySequence(SequenceDto sequenceDto) {
        this.tab.setText(sequenceDto.name);
        this.division.setText(sequenceDto.division + "");
        this.resolution.setText(sequenceDto.resolution + "");
        this.tickLength.setText(sequenceDto.tickLength + "");
        this.ticksPerMeasure.setText(sequenceDto.ticksPerMeasure + "");
        this.ticksIn32nds.setText(sequenceDto.ticksIn32nds + "");
    }
}
