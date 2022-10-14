package hu.boga.midiai.core.boundaries;

import hu.boga.midiai.core.modell.MidiProject;

import java.io.File;

public interface SequenceBoundaryIn {
    void initNewSequence();
    void openFile(File file);
    void playSequence(String projectId, int fromTick, int toTick);
    void playSequence(String projectId, int fromTick);
    void playSequence(String projectId);
    void stopPlayBack(String projectId);
}
