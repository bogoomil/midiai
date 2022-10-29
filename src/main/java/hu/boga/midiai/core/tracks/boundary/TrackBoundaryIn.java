package hu.boga.midiai.core.tracks.boundary;

import hu.boga.midiai.core.musictheory.enums.ChordType;

public interface TrackBoundaryIn {
    void showTrack(String sequenceId, int trackIndex);
    void updateProgramChannel(String sequenceId, int trackIndex, int channel, int program);
//    void updateProgramChannel(String sequencId, int trackIndex, int channel, int program);
//    void updateTrackName(String trackId, String name);
//    void addNote(String trackId, int tick, int pitch, int length);
//    void addChord(String trackId, int tick, int pitch, int length, ChordType chordType);
//    void noteMoved(String trackId, int tick, int pitch, int newTick);
//    void deleteNote(String trackId, NoteDto... notes);
}
