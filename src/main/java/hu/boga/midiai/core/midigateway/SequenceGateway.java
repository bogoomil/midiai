package hu.boga.midiai.core.midigateway;

import hu.boga.midiai.core.modell.AISequence;

public interface SequenceGateway {
    AISequence initNewSequence();
    AISequence openFile(String path);
}
