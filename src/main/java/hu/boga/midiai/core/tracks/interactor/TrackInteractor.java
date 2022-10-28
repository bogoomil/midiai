package hu.boga.midiai.core.tracks.interactor;

import hu.boga.midiai.core.tracks.boundary.TrackBoundaryIn;
import hu.boga.midiai.core.tracks.boundary.TrackBoundaryOut;
import hu.boga.midiai.core.tracks.boundary.NoteDto;
import hu.boga.midiai.core.tracks.boundary.TrackDto;
import hu.boga.midiai.midigateway.InMemoryStore;
import hu.boga.midiai.core.tracks.modell.TrackModell;
import hu.boga.midiai.core.tracks.modell.NoteModell;
import hu.boga.midiai.core.musictheory.enums.ChordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrackInteractor implements TrackBoundaryIn {

    private static final Logger LOG = LoggerFactory.getLogger(TrackInteractor.class);

    TrackBoundaryOut boundaryOut;

    @Inject
    public TrackInteractor(final TrackBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void showTrack(String trackId) {
        boundaryOut.dispayTrack(convertTrackToTrackDto(retreivMidiTrack(trackId)));
    }

    @Override
    public void updateProgramChannel(String trackId, int channel, int program) {
        retreivMidiTrack(trackId).updateProgramChannel(channel, program);
    }

    @Override
    public void updateTrackName(String trackId, String name) {
        retreivMidiTrack(trackId).updateTrackName(name);
    }

    @Override
    public void addNote(String trackId, int tick, int pitch, int length) {
        InMemoryStore.findMidiProjectByTrackId(trackId).ifPresent(midiProject -> {
            int ticksIn32nds = midiProject.getTicksIn32nds();
            midiProject.getTrackById(trackId).ifPresent(midiTrack -> {
                midiTrack.addNote(tick, pitch, length * ticksIn32nds);
                boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
            });
        });
    }

    @Override
    public void addChord(String trackId, int tick, int pitch, int length, ChordType chordType) {
        InMemoryStore.findMidiProjectByTrackId(trackId).ifPresent(midiProject -> {
            int ticksIn32nds = midiProject.getTicksIn32nds();
            midiProject.getTrackById(trackId).ifPresent(midiTrack -> {
                midiTrack.addChord(tick, pitch, length * ticksIn32nds, chordType);
                boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
            });
        });
    }

    @Override
    public void noteMoved(String trackId, int tick, int pitch, int newTick) {
        TrackModell trackModell = retreivMidiTrack(trackId);
        trackModell.moveNote(tick, pitch, newTick);
        boundaryOut.dispayTrack(convertTrackToTrackDto(trackModell));
    }

    @Override
    public void deleteNote(String trackId, NoteDto... notes) {
        LOG.debug("deleting notes: " + Arrays.asList(notes));
        TrackModell trackModell = retreivMidiTrack(trackId);
        Arrays.stream(notes).forEach(noteDto -> {
            trackModell.deleteNote((int) noteDto.tick, (int) noteDto.midiCode);

        });
        boundaryOut.dispayTrack(convertTrackToTrackDto(trackModell));

    }

    private TrackDto convertTrackToTrackDto(TrackModell trackModell) {
        TrackDto dto = new TrackDto();
        trackModell.getProgram().ifPresent(pr -> dto.program = pr);
        trackModell.getChannel().ifPresent(ch -> dto.channel = ch);
        dto.noteCount = trackModell.getNoteCount();
        dto.trackId = trackModell.getId();
        dto.resolution = trackModell.getResolution();
        dto.notes = convertNotesToNoteDtos(trackModell.getNotes());
        trackModell.getTrackName().ifPresent(name -> dto.name = name);
        return dto;
    }

    private NoteDto[] convertNotesToNoteDtos(List<NoteModell> noteModells) {
        return noteModells.stream()
                .map(noteModell -> new NoteDto(noteModell.noteValue, noteModell.tick, noteModell.length))
                .collect(Collectors.toList())
                .toArray(new NoteDto[]{});
    }

    private TrackModell retreivMidiTrack(final String trackId){
        return InMemoryStore.getTrackById(trackId).orElseThrow();
    }
}
