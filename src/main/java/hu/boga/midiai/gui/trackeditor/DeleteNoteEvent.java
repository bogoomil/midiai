package hu.boga.midiai.gui.trackeditor;

public class DeleteNoteEvent {
    double tick;
    double pitch;

    public DeleteNoteEvent(final double tick, final double pitch) {
        this.tick = tick;
        this.pitch = pitch;
    }

    public double getTick() {
        return this.tick;
    }

    public double getPitch() {
        return this.pitch;
    }
}
