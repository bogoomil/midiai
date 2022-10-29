package hu.boga.midiai.core.tracks.interactor;

import hu.boga.midiai.core.tracks.boundary.TrackBoundaryIn;
import hu.boga.midiai.core.tracks.boundary.TrackBoundaryOut;
import hu.boga.midiai.core.tracks.boundary.NoteDto;
import hu.boga.midiai.core.tracks.boundary.TrackDto;
import hu.boga.midiai.core.tracks.gateway.TrackGateway;
import hu.boga.midiai.core.tracks.modell.TrackModell;
import hu.boga.midiai.core.tracks.modell.NoteModell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class TrackInteractor implements TrackBoundaryIn {

    private static final Logger LOG = LoggerFactory.getLogger(TrackInteractor.class);

    TrackBoundaryOut boundaryOut;
    TrackGateway gateway;

    @Inject
    public TrackInteractor(final TrackBoundaryOut boundaryOut, final TrackGateway trackGateway) {
        this.boundaryOut = boundaryOut;
        this.gateway = trackGateway;
    }

    @Override
    public void showTrack(String sequenceId, int trackIndex) {
        TrackModell trackModell = gateway.getTrackModell(sequenceId, trackIndex);
        boundaryOut.dispayTrack(convertTrackToTrackDto(trackModell));
    }

    @Override
    public void updateProgramChannel(String sequenceId, int trackIndex, int channel, int program) {
        TrackModell trackModell = gateway.getTrackModell(sequenceId, trackIndex);
        trackModell.program = program;
        trackModell.channel = channel;


    }

//    @Override
//    public void updateProgramChannel(String sequencId, int trackIndex, int channel, int program) {
//
//        retreivMidiTrack(trackId).updateProgramChannel(channel, program);
//    }
//
//    @Override
//    public void updateTrackName(String trackId, String name) {
//        retreivMidiTrack(trackId).updateTrackName(name);
//    }
//
//    @Override
//    public void addNote(String trackId, int tick, int pitch, int length) {
//        InMemoryStore.findMidiProjectByTrackId(trackId).ifPresent(midiProject -> {
//            int ticksIn32nds = midiProject.getTicksIn32nds();
//            midiProject.getTrackById(trackId).ifPresent(midiTrack -> {
//                midiTrack.addNote(tick, pitch, length * ticksIn32nds);
//                boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
//            });
//        });
//    }
//
//    @Override
//    public void addChord(String trackId, int tick, int pitch, int length, ChordType chordType) {
//        InMemoryStore.findMidiProjectByTrackId(trackId).ifPresent(midiProject -> {
//            int ticksIn32nds = midiProject.getTicksIn32nds();
//            midiProject.getTrackById(trackId).ifPresent(midiTrack -> {
//                midiTrack.addChord(tick, pitch, length * ticksIn32nds, chordType);
//                boundaryOut.dispayTrack(convertTrackToTrackDto(midiTrack));
//            });
//        });
//    }
//
//    @Override
//    public void noteMoved(String trackId, int tick, int pitch, int newTick) {
//        TrackModell trackModell = retreivMidiTrack(trackId);
//        trackModell.moveNote(tick, pitch, newTick);
//        boundaryOut.dispayTrack(convertTrackToTrackDto(trackModell));
//    }
//
//    @Override
//    public void deleteNote(String trackId, NoteDto... notes) {
//        LOG.debug("deleting notes: " + Arrays.asList(notes));
//        TrackModell trackModell = retreivMidiTrack(trackId);
//        Arrays.stream(notes).forEach(noteDto -> {
//            trackModell.deleteNote((int) noteDto.tick, (int) noteDto.midiCode);
//
//        });
//        boundaryOut.dispayTrack(convertTrackToTrackDto(trackModell));
//
//    }

    private TrackDto convertTrackToTrackDto(TrackModell trackModell) {
        TrackDto dto = new TrackDto();
        dto.channel = trackModell.channel;
        dto.name = trackModell.name;
        dto.trackIndex = trackModell.getIndex();
        dto.program = trackModell.program;
        dto.noteCount = trackModell.notes.size();
        dto.resolution = trackModell.getResolution();
        dto.notes = convertNotesToNoteDtos(trackModell.notes);
        return dto;
    }

    private NoteDto[] convertNotesToNoteDtos(List<NoteModell> noteModells) {
        return noteModells.stream()
                .map(noteModell -> new NoteDto(noteModell.noteValue, noteModell.tick, noteModell.length))
                .collect(Collectors.toList())
                .toArray(new NoteDto[]{});
    }

//    private TrackModell retreivMidiTrack(final String trackId){
//        return InMemoryStore.getTrackById(trackId).orElseThrow();
//    }
}
