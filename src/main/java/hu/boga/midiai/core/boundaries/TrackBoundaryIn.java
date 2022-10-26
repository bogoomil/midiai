package hu.boga.midiai.core.boundaries;

import hu.boga.midiai.core.boundaries.dtos.NoteDto;

public interface TrackBoundaryIn {
    void showTrack(String trackId);
    void updateProgramChannel(String trackId, int channel, int program);
    void updateTrackName(String trackId, String name);
    void addNote(String trackId, int tick, int pitch, int length);
    void noteMoved(String trackId, int tick, int pitch, int newTick);
    void deleteNote(String trackId, NoteDto... notes);
}
