package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.sequence.gateway.SequenceGateway;
import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.TrackModell;
import hu.boga.midiai.core.util.Constants;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SequenceGatewayImpl implements SequenceGateway {


    @Override
    public SequenceModell open(String path) {
        try {
            File file = new File(path);
            String id =  UUID.randomUUID().toString();
            Sequence sequence = MidiSystem.getSequence(file);
            InMemoryStore.SEQUENCES.put(id, sequence);

            SequenceModell sequenceModell = new SequenceToModellConverter(sequence, id).convert();
            sequenceModell.name = file.getName();

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
            sequence.createTrack();
            InMemoryStore.SEQUENCES.put(id, sequence);

            return new SequenceToModellConverter(sequence, id).convert();

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
    public SequenceModell addTrack(String sequenceId) {
        Sequence sequence = InMemoryStore.SEQUENCES.get(sequenceId);
        sequence.createTrack();
        return new SequenceToModellConverter(sequence, sequenceId).convert();
    }

    @Override
    public SequenceModell deleteTrack(String seqId, int trackIndex) {
        Sequence sequence = InMemoryStore.SEQUENCES.get(seqId);
        sequence.deleteTrack(sequence.getTracks()[trackIndex]);
        return new SequenceToModellConverter(sequence, seqId).convert();
    }

    @Override
    public void setTempo(String projectId, int tempo) {
        Sequence sequence = InMemoryStore.SEQUENCES.get(projectId);
        Arrays.stream(sequence.getTracks()).forEach(track -> {
            updateTempo(track, tempo);
        });
    }

    public void updateTempo(Track track, final long tempo) {
        final List<MidiEvent> tempoEvents = MidiUtils.getMetaEventsByType(track, Constants.METAMESSAGE_SET_TEMPO);
        tempoEvents.forEach(track::remove);
        final long microSecsPerQuarterNote = Constants.MICROSECONDS_IN_MINUTE / tempo;
        final byte[] array = {0, 0, 0};
        for (int i = 0; i < 3; i++) {
            final int shift = (3 - 1 - i) * 8;
            array[i] = (byte) (microSecsPerQuarterNote >> shift);
        }
        track.add(MidiUtils.createMetaEvent(0, Constants.METAMESSAGE_SET_TEMPO, array));
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
