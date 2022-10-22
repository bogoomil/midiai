package hu.boga.midiai.core.boundaries;

import hu.boga.midiai.core.boundaries.dtos.SequenceDto;

public interface SequenceBoundaryOut {
    void displaySequence(SequenceDto sequenceDto);

    void addTrack(String id);
}
