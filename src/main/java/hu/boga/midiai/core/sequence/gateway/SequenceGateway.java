package hu.boga.midiai.core.sequence.gateway;

import hu.boga.midiai.core.sequence.modell.SequenceModell;

public interface SequenceGateway {

    SequenceModell create();
    SequenceModell open(String path);
    SequenceModell find(String id);

    void play(String id);
    void stop(String id);
    void save(String projectId, String filePath);

    SequenceModell addTrack(String projectId);
    SequenceModell deleteTrack(String seqId, int trackIndex);

    void setTempo(String projectId, int tempo);

}
