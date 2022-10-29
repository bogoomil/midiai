package hu.boga.midiai.gui.events;

public class TrackDeleteEvent {
    int index;

    public TrackDeleteEvent(final int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

}
