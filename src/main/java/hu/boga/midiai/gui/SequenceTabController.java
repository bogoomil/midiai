package hu.boga.midiai.gui;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import javafx.event.ActionEvent;

import javax.inject.Inject;

public class SequenceTabController implements SequenceBoundaryOut {

    private SequenceBoundaryIn boundaryIn;

    @Inject
    public SequenceTabController(SequenceBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    public void saveSequence(ActionEvent actionEvent) {
    }

    public void onPlayCurrentSec(ActionEvent actionEvent) {
    }

    public void stopPlayback(ActionEvent actionEvent) {
    }

    @Override
    public void displayProperties(String properties) {

    }
}
