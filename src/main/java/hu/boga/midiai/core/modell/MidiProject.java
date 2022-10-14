package hu.boga.midiai.core.modell;

import com.google.common.base.Objects;

import javax.sound.midi.*;
import java.util.UUID;

public class MidiProject {
    private Sequence sequence;
    private Sequencer sequencer;
    UUID id  = UUID.randomUUID();
    String name;
    private int tempo = 120;
    private int tempoFactor = 1;

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

    private void initSequencer(Sequence sequence) {
        try {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
            this.sequencer.setSequence(sequence);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        play(0);
    }

    public void play(int fromTick){
        int toTick = (int) this.sequence.getTickLength();
        play(fromTick, toTick, 1);
    }

    public void play(int fromTick, int toTick, int loopCount){
        this.sequencer.setTempoInBPM(tempo);
        this.sequencer.setTempoFactor(tempoFactor);
        this.sequencer.setLoopStartPoint(fromTick);
        this.sequencer.setLoopEndPoint(toTick);
        this.sequencer.setLoopCount(loopCount);
        this.sequencer.start();
    }

    public void stop(){
        this.sequencer.stop();
    }

    public String getId(){
        return this.id.toString();
    }
}
