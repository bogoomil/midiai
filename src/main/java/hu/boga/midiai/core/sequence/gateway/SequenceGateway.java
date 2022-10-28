package hu.boga.midiai.core.sequence.gateway;

import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.TrackModell;

public interface SequenceGateway {

    SequenceModell create();
    SequenceModell open(String path);

    void play(String id);
    void stop(String id);
    void save(String projectId, String filePath);

    TrackModell addTrack(String projectId);
    SequenceModell deleteTrack(String trackId);

    void setTempo(String projectId, int tempo);

}
