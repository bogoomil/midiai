package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.SequenceDto;

import javax.inject.Inject;

public class SequenceInteractor implements SequenceBoundaryIn {
    private final SequenceBoundaryOut boundaryOut;

    @Inject
    public SequenceInteractor(SequenceBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void initNewSequence() {
        SequenceDto dto = new SequenceDto();
        dto.division = 1;
        dto.resolution = 2;
        dto.tickLength = 3;
        dto.ticksPerMeasure = 4;
        dto.ticksIn32nds = 5;
        dto.name = "ZergeHere";

        this.boundaryOut.displaySequence(dto);
    }
}
