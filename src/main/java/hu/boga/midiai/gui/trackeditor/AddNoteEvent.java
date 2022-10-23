package hu.boga.midiai.gui.trackeditor;

public class AddNoteEvent {
    int tick;
    int pitch;

    public AddNoteEvent(final int tick, final int pitch) {
        this.tick = tick;
        this.pitch = pitch;
    }

    public int getTick() {
        return this.tick;
    }

    public int getPitch() {
        return this.pitch;
    }
}
