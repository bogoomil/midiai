package hu.boga.midiai.core.sequence.modell;

import com.google.common.base.Objects;
import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.tracks.modell.MidiTrack;
import hu.boga.midiai.core.util.Constants;
import hu.boga.midiai.core.util.MidiUtil;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SequenceModell {
    private final Sequence sequence;
    private Sequencer sequencer;
    UUID id = UUID.randomUUID();
    String name;
    private List<MidiTrack> tracks;

    public SequenceModell(Sequence sequence) {
        this.sequence = sequence;
        initSequencer(sequence);
        initTracks();
    }

    private void initTracks() {
        tracks = new ArrayList<>();
        Arrays.stream(sequence.getTracks()).forEach(track -> {
            MidiTrack midiTrack = MidiTrack.createMidiTrack(track, getResolution());
            tracks.add(midiTrack);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequenceModell that = (SequenceModell) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResolution() {
        return this.sequence.getResolution();
    }

    public float getDivision() {
        return this.sequence.getDivisionType();
    }

    public long getTickLength() {
        return this.sequence.getTickLength();
    }

    public int getTicksPerMeasure() {
        return 4 * sequence.getResolution();
    }

    public int getTicksIn32nds() {
        return getTicksPerMeasure() / 32;
    }

    public float ticksPerSecond() {
        return this.sequence.getResolution() * (sequencer.getTempoInBPM() / 60);
    }

    public float tickSize() {
        return 1 / ticksPerSecond();
    }

    private void initSequencer(Sequence sequence) {
        try {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            throw new MidiAiException("Midi sequencer unavailable: " + e.getMessage());
        }
    }

    public void play() {
        play(0);
    }

    public void play(int fromTick) {
        int toTick = (int) this.sequence.getTickLength();
        play(fromTick, toTick, 0);
    }

    public void play(int fromTick, int toTick) {
        play(fromTick, toTick, 0);
    }

    private void play(int fromTick, int toTick, int loopCount) {
        this.sequencer.stop();
        this.sequencer.setLoopCount(loopCount);
        try {
            this.sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new MidiAiException("Invalid midi exception: " + e.getMessage());
        }
        this.sequencer.setTempoFactor(1f);
        this.sequencer.setTickPosition(fromTick);
        this.sequencer.setLoopStartPoint(fromTick);
        this.sequencer.setLoopEndPoint(toTick);
        this.sequencer.start();
    }


    public void playLoop(int fromTick, int toTick) {
        play(fromTick, toTick, Sequencer.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        this.sequencer.stop();
    }

    public String getId() {
        return this.id.toString();
    }

    public float getTempo() {
        List<MidiEvent> tempoEvents = getMetaEventsByType(Constants.METAMESSAGE_SET_TEMPO);
        if (tempoEvents.size() == 0) {
            return 0;
        } else {
            return MidiUtil.getTempoInBPM((MetaMessage) tempoEvents.get(0).getMessage());
        }
    }

    public void setTempo(float tempo) {
        this.getTracks().forEach(track -> {
            track.updateTempo(0L, (long) tempo);
        });
    }

    public void save(String filePath) {
        File file = new File(filePath);
        try {
            MidiSystem.write(sequence, 1, file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MidiAiException("Saving " + filePath + " failed");
        }
    }

    public List<MidiTrack> getTracks() {
        return tracks;
    }

    public Optional<MidiTrack> getTrackById(String id) {
        return tracks.stream().filter(midiTrack -> midiTrack.getId().toString().equals(id)).findFirst();
    }

    public MidiTrack createNewTrack() {
        Track track = sequence.createTrack();
        MidiTrack midiTrack = MidiTrack.createMidiTrack(track, getResolution());
        tracks.add(midiTrack);
        return midiTrack;
    }

    public void removeTrackById(String trackId) {
        getTrackById(trackId).ifPresent(midiTrack -> {
            sequence.deleteTrack(midiTrack.getTrack());
            this.tracks.remove(midiTrack);
        });
    }

    public List<MidiEvent> getMetaEventsByType(int type) {
        return tracks.stream()
                .flatMap(midiTrack -> midiTrack.getMetaEventsByType(type).stream())
                .collect(Collectors.toList());
    }
}
