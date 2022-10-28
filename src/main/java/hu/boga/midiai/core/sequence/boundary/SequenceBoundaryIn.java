package hu.boga.midiai.core.sequence.boundary;

public interface SequenceBoundaryIn {

    void create();
    void open(String path);
    void save(String projectId, String filePath);

    void play(String projectId);
    void stop(String projectId);

    void addTrack(String projectId);
    void removeTrack(String trackId);

    void setTempo(String projectId, int newValue);
}
