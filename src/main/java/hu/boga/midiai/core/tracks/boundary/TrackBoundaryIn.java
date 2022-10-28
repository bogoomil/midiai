package hu.boga.midiai.core.tracks.boundary;

import hu.boga.midiai.core.musictheory.enums.ChordType;

public interface TrackBoundaryIn {
    void showTrack(String trackId);
    void updateProgramChannel(String trackId, int channel, int program);
    void updateTrackName(String trackId, String name);
    void addNote(String trackId, int tick, int pitch, int length);
    void addChord(String trackId, int tick, int pitch, int length, ChordType chordType);
    void noteMoved(String trackId, int tick, int pitch, int newTick);
    void deleteNote(String trackId, NoteDto... notes);
}
