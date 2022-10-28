package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.sequence.gateway.SequenceGateway;
import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.TrackModell;
import hu.boga.midiai.core.util.Constants;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SequenceGatewayImpl implements SequenceGateway {


    @Override
    public SequenceModell open(String path) {
        try {
            File file = new File(path);
            Sequence sequence = MidiSystem.getSequence(file);
            SequenceModell sequenceModell = new SequenceModell(sequence);
            sequenceModell.name = file.getName();
            InMemoryStore.addProject(sequenceModell);
            return sequenceModell;
        } catch (InvalidMidiDataException | IOException e) {
            throw new MidiAiException(e.getMessage());
        }
    }

    @Override
    public SequenceModell create() {
        try {
            Sequence sequence = new Sequence(Sequence.PPQ, Constants.DEFAULT_RESOLUTION);
            SequenceModell sequenceModell = new SequenceModell(sequence);
            sequenceModell.name = Constants.DEFAULT_NAME;
            InMemoryStore.addProject(sequenceModell);

            return sequenceModell;

        } catch (InvalidMidiDataException e) {
            throw new MidiAiException("Sequence creation failed: " + e.getMessage());
        }
    }
    @Override
    public void play(String id) {
        InMemoryStore.getProjectById(id).ifPresent(SequenceModell::play);
    }

    @Override
    public void stop(String id) {
        InMemoryStore.getProjectById(id).ifPresent(SequenceModell::stop);
    }

    @Override
    public void save(String projectId, String filePath) {
        InMemoryStore.getProjectById(projectId).ifPresent(projectModell -> projectModell.save(filePath));
    }

    @Override
    public TrackModell addTrack(String projectId) {
        TrackModell trackModell = null;
        Optional<SequenceModell> projectModellOpt = InMemoryStore.getProjectById(projectId);
        if(projectModellOpt.isPresent()){
            return projectModellOpt.get().createNewTrack();
        }
        throw new MidiAiException("Track creation failed");
    }

    @Override
    public SequenceModell deleteTrack(String trackId) {
        Optional<SequenceModell> modell = InMemoryStore.findMidiProjectByTrackId(trackId);
        if (modell.isPresent()){
            modell.get().removeTrackById(trackId);
            return modell.get();
        }
        throw new MidiAiException("Track deletion failed");
    }

    @Override
    public void setTempo(String projectId, int tempo) {
        InMemoryStore.getProjectById(projectId).ifPresent(projectModell -> {
            projectModell.setTempo(tempo);
        });
    }

}
