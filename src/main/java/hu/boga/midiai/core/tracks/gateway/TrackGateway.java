package hu.boga.midiai.core.tracks.gateway;

import hu.boga.midiai.core.tracks.modell.TrackModell;

public interface TrackGateway {
    TrackModell getTrackModell(String sequenceId, int trackIndex);
}
