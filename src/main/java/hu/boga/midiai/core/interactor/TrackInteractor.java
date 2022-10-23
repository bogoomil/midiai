package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.TrackBoundaryIn;
import hu.boga.midiai.core.boundaries.TrackBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.core.modell.App;
import hu.boga.midiai.core.modell.MidiProject;
import hu.boga.midiai.core.modell.MidiTrack;
import hu.boga.midiai.core.modell.Note;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class TrackInteractor implements TrackBoundaryIn {

    TrackBoundaryOut boundaryOut;

    @Inject
    public TrackInteractor(TrackBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void showTrack(String trackId) {
        App.getTrackById(trackId).ifPresent(midiTrack -> {
            boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
        });
    }

    @Override
    public void updateProgramChannel(String trackId, int channel, int program) {
        App.getTrackById(trackId).ifPresent(midiTrack -> {
            midiTrack.updateProgramChannel(channel, program);
        });
    }

    @Override
    public void updateTrackName(String trackId, String name) {
        App.getTrackById(trackId).ifPresent(midiTrack -> {
            midiTrack.updateTrackName(name);
        });

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
}
