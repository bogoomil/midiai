package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;

import javax.inject.Inject;

public class SequenceInteractor implements SequenceBoundaryIn {
    private SequenceBoundaryOut boundaryOut;

    @Inject
    public SequenceInteractor(SequenceBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void loadProperties() {

    }
}
