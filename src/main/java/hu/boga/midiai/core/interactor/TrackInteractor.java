package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.TrackBoundaryIn;
import hu.boga.midiai.core.boundaries.TrackBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.core.modell.App;
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

            System.out.println("show track, midiTrack: " + midiTrack.getId());

            boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
        });
    }

    private TrackDto convertTrackToTrackDto(MidiTrack midiTrack){
        TrackDto dto = new TrackDto();
        midiTrack.getProgram().ifPresent(pr -> dto.program = pr);
        midiTrack.getChannel().ifPresent(ch -> dto.channel = ch);
        dto.noteCount = midiTrack.getNoteCount();
        dto.trackId = midiTrack.getId();
        dto.resolution = midiTrack.getResolution();
        dto.notes = convertNotesToNoteDtos(midiTrack.getNotes());
        return dto;
    }

    private NoteDto[] convertNotesToNoteDtos(List<Note> notes) {
        return notes.stream()
                .map(note -> new NoteDto(note.noteValue, note.tick, note.length))
                .collect(Collectors.toList())
                .toArray(new NoteDto[]{});
    }
}
