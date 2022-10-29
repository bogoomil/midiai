package hu.boga.midiai.core.sequence.boundary;

public interface SequenceBoundaryIn {

    void create();
    void open(String path);
    void save(String projectId, String filePath);

    void play(String projectId);
    void stop(String projectId);

    void addTrack(String projectId);
    void removeTrack(String seqId, int trackIndex);

    void setTempo(String projectId, int newValue);
    void updateTrackProgram(String sequenceId, int trackIndex, int program, int channel);
}
