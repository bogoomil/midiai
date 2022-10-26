package hu.boga.midiai.gui.trackeditor;

import hu.boga.midiai.gui.trackeditor.events.AddNoteEvent;
import hu.boga.midiai.gui.trackeditor.events.DeleteNoteEvent;
import hu.boga.midiai.gui.trackeditor.events.MoveNoteEvent;

public interface TrackEventListener {
    void onAddNoteEvent(AddNoteEvent event);
    void onMoveNoteEvent(MoveNoteEvent event);
    void onDeleteNoteEvent(DeleteNoteEvent... event);
}
