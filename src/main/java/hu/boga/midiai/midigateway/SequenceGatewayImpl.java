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
import java.util.UUID;

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
    public SequenceModell find(String id) {
        Sequence sequence = InMemoryStore.SEQUENCES.get(id);
        return new SequenceToModellConverter(sequence, id).convert();
    }

    @Override
    public SequenceModell create() {
        try {
            String id = UUID.randomUUID().toString();
            Sequence sequence = new Sequence(Sequence.PPQ, Constants.DEFAULT_RESOLUTION);
            SequenceModell sequenceModell = new SequenceModell(id);
            InMemoryStore.SEQUENCES.put(id, sequence);

            return sequenceModell;

        } catch (InvalidMidiDataException e) {
            throw new MidiAiException("Sequence creation failed: " + e.getMessage());
        }
    }
    @Override
    public void play(String id) {
        Sequencer sequencer = InMemoryStore.SEQUENCER;

        sequencer.stop();
        sequencer.setLoopCount(0);
        try {
            sequencer.setSequence(InMemoryStore.SEQUENCES.get(id));
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new MidiAiException("Invalid midi exception: " + e.getMessage());
        }
        sequencer.setTempoFactor(1f);
        sequencer.setTickPosition(0);
//        sequencer.setLoopStartPoint(fromTick);
//        sequencer.setLoopEndPoint(toTick);
        sequencer.start();
    }

    @Override
    public void stop(String id) {
        InMemoryStore.SEQUENCER.stop();
    }

    @Override
    public void save(String projectId, String filePath) {
        File file = new File(filePath);
        try {
            MidiSystem.write(InMemoryStore.SEQUENCES.get(projectId), 1, file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MidiAiException("Saving " + filePath + " failed");
        }
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

    //    public void play() {
//        play(0);
//    }
//
//    public void play(int fromTick) {
//        int toTick = (int) this.sequence.getTickLength();
//        play(fromTick, toTick, 0);
//    }
//
//    public void play(int fromTick, int toTick) {
//        play(fromTick, toTick, 0);
//    }
//
//    private void play(int fromTick, int toTick, int loopCount) {
//    }
//
//
//    public void playLoop(int fromTick, int toTick) {
//        play(fromTick, toTick, Sequencer.LOOP_CONTINUOUSLY);
//    }
//
//    public void stop() {
//        this.sequencer.stop();
//    }

}
