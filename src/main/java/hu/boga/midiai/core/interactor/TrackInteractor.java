package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.TrackBoundaryIn;
import hu.boga.midiai.core.boundaries.TrackBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.core.modell.App;
import hu.boga.midiai.core.modell.MidiTrack;

import javax.inject.Inject;

public class TrackInteractor implements TrackBoundaryIn {

    TrackBoundaryOut boundaryOut;

    @Inject
    public TrackInteractor(TrackBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void showTrackProperties(String trackId) {
        App.getTrackById(trackId).ifPresent(midiTrack -> {
            System.out.println("show track properties: " + trackId);
            boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
        });
    }

    private TrackDto convertTrackToTrackDto(MidiTrack midiTrack){
        TrackDto dto = new TrackDto();
        midiTrack.getProgram().ifPresent(pr -> dto.program = pr);
        midiTrack.getChannel().ifPresent(ch -> dto.channel = ch);
        dto.noteCount = midiTrack.getNoteCount();
        dto.trackId = midiTrack.getId();

        return dto;
    }
}
