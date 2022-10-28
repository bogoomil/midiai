package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.sequence.gateway.SequenceGateway;
import hu.boga.midiai.core.sequence.interactor.ModellToDtoConverter;
import hu.boga.midiai.core.sequence.modell.ProjectModell;
import hu.boga.midiai.core.tracks.modell.MidiTrack;
import hu.boga.midiai.core.util.Constants;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SequenceGatewayImpl implements SequenceGateway {

    @Override
    public ProjectModell open(String path) {
        try {
            File file = new File(path);
            Sequence sequence = MidiSystem.getSequence(file);
            ProjectModell projectModell = new ProjectModell(sequence);
            projectModell.setName(file.getName());
            InMemoryStore.addProject(projectModell);
            return projectModell;
        } catch (InvalidMidiDataException | IOException e) {
            throw new MidiAiException(e.getMessage());
        }
    }

    @Override
    public void play(String id) {
        InMemoryStore.getProjectById(id).ifPresent(ProjectModell::play);
    }

    @Override
    public void stop(String id) {
        InMemoryStore.getProjectById(id).ifPresent(ProjectModell::stop);
    }

    @Override
    public void save(String projectId, String filePath) {
        InMemoryStore.getProjectById(projectId).ifPresent(projectModell -> projectModell.save(filePath));
    }

    @Override
    public MidiTrack addTrack(String projectId) {
        MidiTrack midiTrack = null;
        Optional<ProjectModell> projectModellOpt = InMemoryStore.getProjectById(projectId);
        if(projectModellOpt.isPresent()){
            return projectModellOpt.get().createNewTrack();
        }
        throw new MidiAiException("Track creation failed");
    }

    @Override
    public ProjectModell deleteTrack(String trackId) {
        Optional<ProjectModell> modell = InMemoryStore.findMidiProjectByTrackId(trackId);
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

    @Override
    public ProjectModell create() {
        try {
            Sequence sequence = new Sequence(Sequence.PPQ, Constants.DEFAULT_RESOLUTION);
            ProjectModell projectModell = new ProjectModell(sequence);
            projectModell.setName(Constants.DEFAULT_NAME);
            InMemoryStore.addProject(projectModell);

            return projectModell;

        } catch (InvalidMidiDataException e) {
            throw new MidiAiException("Sequence creation failed: " + e.getMessage());
        }
    }
}
