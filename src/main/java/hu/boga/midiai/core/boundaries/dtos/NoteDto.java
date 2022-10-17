package hu.boga.midiai.core.boundaries.dtos;

public class NoteDto {
    public double midiCode;
    public double lengthInTicks;
    public double tick;

    public NoteDto(final double midiCode, final double tick, final double lengthInTicks) {
        this.midiCode = midiCode;
        this.lengthInTicks = lengthInTicks;
        this.tick = tick;
    }

    @Override
    public String toString() {
        return "NoteDto{" +
                "midiCode=" + midiCode +
                ", lengthInTicks=" + lengthInTicks +
                ", tick=" + tick +
                '}';
    }
}
