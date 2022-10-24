package hu.boga.midiai.gui.trackeditor;

public class MoveNoteEvent {
    int tick;
    int pitch;
    int newTick;

    public MoveNoteEvent(final int tick, final int pitch, int newTick) {
        this.tick = tick;
        this.pitch = pitch;
        this.newTick = newTick;
    }

    public int getNewTick() {
        return this.newTick;
    }

    public int getTick() {
        return this.tick;
    }

    public int getPitch() {
        return this.pitch;
    }
}
