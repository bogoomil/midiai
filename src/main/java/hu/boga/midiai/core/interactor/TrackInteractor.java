package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.TrackBoundaryIn;
import hu.boga.midiai.core.boundaries.TrackBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.core.modell.App;
import hu.boga.midiai.core.modell.MidiTrack;
import hu.boga.midiai.core.modell.Note;
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
        App.findMidiProjectByTrackId(trackId).ifPresent(midiProject -> {
            int ticksIn32nds = midiProject.getTicksIn32nds();
            midiProject.getTrackById(trackId).ifPresent(midiTrack -> {
                midiTrack.addNote(tick, pitch, length * ticksIn32nds);
                boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
            });
        });
    }

    @Override
    public void noteMoved(String trackId, int tick, int pitch, int newTick) {
        MidiTrack midiTrack = retreivMidiTrack(trackId);
        midiTrack.moveNote(tick, pitch, newTick);
        boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
    }

    @Override
    public void deleteNote(String trackId, NoteDto... notes) {
        LOG.debug("deleting notes: " + Arrays.asList(notes));
        MidiTrack midiTrack = retreivMidiTrack(trackId);
        Arrays.stream(notes).forEach(noteDto -> {
            midiTrack.deleteNote((int) noteDto.tick, (int) noteDto.midiCode);

        });
        boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));

    }

    private TrackDto convertTrackToTrackDto(MidiTrack midiTrack) {
        TrackDto dto = new TrackDto();
        midiTrack.getProgram().ifPresent(pr -> dto.program = pr);
        midiTrack.getChannel().ifPresent(ch -> dto.channel = ch);
        dto.noteCount = midiTrack.getNoteCount();
        dto.trackId = midiTrack.getId();
        dto.resolution = midiTrack.getResolution();
        dto.notes = convertNotesToNoteDtos(midiTrack.getNotes());
        midiTrack.getTrackName().ifPresent(name -> dto.name = name);
        return dto;
    }

    private NoteDto[] convertNotesToNoteDtos(List<Note> notes) {
        return notes.stream()
                .map(note -> new NoteDto(note.noteValue, note.tick, note.length))
                .collect(Collectors.toList())
                .toArray(new NoteDto[]{});
    }

    private MidiTrack retreivMidiTrack(final String trackId){
        return App.getTrackById(trackId).orElseThrow();
    }
}
