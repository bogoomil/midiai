package hu.boga.midiai.core.midigateway;

import hu.boga.midiai.core.modell.MidiProject;

public interface SequenceGateway {
    MidiProject initNewSequence();
    MidiProject openFile(String path);
}
