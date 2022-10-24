package hu.boga.midiai.gui.trackeditor;

public interface TrackEventListener {
    void onAddNoteEvent(AddNoteEvent event);
    void onMoveNoteEvent(MoveNoteEvent event);
    void onDeleteNoteEvent(DeleteNoteEvent event);
}
