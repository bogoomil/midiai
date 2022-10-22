package hu.boga.midiai.core.boundaries;

public interface TrackBoundaryIn {
    void showTrack(String trackId);
    void updateProgramChannel(String trackId, int channel, int program);
    void updateTrackName(String trackId, String name);
}
