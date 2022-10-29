package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.gateway.TrackGateway;
import hu.boga.midiai.core.tracks.modell.TrackModell;

public class TrackGatewayImpl implements TrackGateway {
    @Override
    public TrackModell getTrackModell(String sequenceId, int trackIndex) {
        SequenceModell sequenceModell = new SequenceToModellConverter(InMemoryStore.SEQUENCES.get(sequenceId), sequenceId).convert();
        return sequenceModell.tracks[trackIndex];
    }
}
