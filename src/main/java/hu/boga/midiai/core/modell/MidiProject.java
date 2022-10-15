package hu.boga.midiai.core.modell;

import com.google.common.base.Objects;
import hu.boga.midiai.core.exceptions.AimidiException;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.UUID;

public class MidiProject {
    private final Sequence sequence;
    private Sequencer sequencer;
    UUID id = UUID.randomUUID();
    String name;

    public MidiProject(Sequence sequence) {
        this.sequence = sequence;
        initSequencer(sequence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MidiProject that = (MidiProject) o;
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

    public float tickSize(){
        return 1 / ticksPerSecond();
    }

    private void initSequencer(Sequence sequence) {
        try {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.addControllerEventListener(shortMessage -> System.out.println("tick position: " + sequencer.getTickPosition()), new int[]{ShortMessage.NOTE_ON});
            this.sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            throw new AimidiException("Midi sequencer unavailable: " + e.getMessage());
        }
    }

    public void play() {
        play(0);
    }

    public void play(int fromTick) {
        int toTick = (int) this.sequence.getTickLength();
        play(fromTick, toTick, 1);
    }

    public void play(int fromTick, int toTick) {
        play(fromTick, toTick, 1);
    }

    private void play(int fromTick, int toTick, int loopCount) {
        this.sequencer.stop();
        this.sequencer.setLoopCount(loopCount);
        try {
            this.sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new AimidiException("Invalid midi exception: " + e.getMessage());
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

    public float getTempo(){
        return this.sequencer.getTempoInBPM();
    }

    public void setTempo(float tempo) {
        this.sequencer.setTempoInBPM(tempo);
    }

    public void setTempoFactor(float tempoFactor) {
        this.sequencer.setTempoFactor(tempoFactor);
    }
}