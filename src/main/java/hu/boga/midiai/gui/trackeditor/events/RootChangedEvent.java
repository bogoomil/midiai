package hu.boga.midiai.gui.trackeditor.events;

import hu.boga.midiai.core.musictheory.enums.NoteName;

public class RootChangedEvent {
    NoteName noteName;

    public RootChangedEvent(final NoteName noteName) {
        this.noteName = noteName;
    }

    public NoteName getNoteName() {
        return this.noteName;
    }
}
