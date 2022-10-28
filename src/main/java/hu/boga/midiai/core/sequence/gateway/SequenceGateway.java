package hu.boga.midiai.core.sequence.gateway;

import hu.boga.midiai.core.sequence.modell.ProjectModell;
import hu.boga.midiai.core.tracks.modell.MidiTrack;

public interface SequenceGateway {
    ProjectModell open(String path);
    void play(String id);
    void stop(String id);

    void save(String projectId, String filePath);

    MidiTrack addTrack(String projectId);

    ProjectModell deleteTrack(String trackId);

    void setTempo(String projectId, int tempo);

    ProjectModell create();
}
