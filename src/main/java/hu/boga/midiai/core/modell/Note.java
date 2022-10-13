package hu.boga.midiai.core.modell;


import hu.boga.midiai.core.musictheory.enums.NoteName;

public class Note {
    int noteValue;
    long tick;
    long length;
    int velocity;
    int channel;

    public Note(int noteValue, long tick, long length, int velocity, int channel) {
        this.noteValue = noteValue;
        this.tick = tick;
        this.length = length;
        this.velocity = velocity;
        this.channel = channel;
    }

    @Override
    public String toString() {
        NoteName noteName = NoteName.byCode(noteValue);
        return "[" + noteName.name() + "" + (noteValue / 12) + ", tick:" + tick + ", length: " + length + "]";
    }
}
