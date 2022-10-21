package hu.boga.midiai.core.boundaries;

import java.io.File;

public interface SequenceBoundaryIn {
    void initNewSequence();
    void openFile(File file);
    void playSequence(String projectId, int fromTick, int toTick);
    void playSequence(String projectId, int fromTick);
    void playSequence(String projectId);
    void playLoop(String projectId, int fromTick, int toTick);
    void stopPlayBack(String projectId);

    void saveSequence(String projectId, String filePath);

    void addTrack(String projectId);
    void removeTrack(String trackId);

    void setTempo(String projectId, int newValue);
}
