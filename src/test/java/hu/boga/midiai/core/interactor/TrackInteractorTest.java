package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.tracks.boundary.TrackBoundaryOut;
import hu.boga.midiai.core.tracks.boundary.TrackDto;
import hu.boga.midiai.midigateway.InMemoryStore;
import hu.boga.midiai.core.sequence.modell.ProjectModell;
import hu.boga.midiai.core.tracks.interactor.TrackInteractor;
import hu.boga.midiai.core.tracks.modell.MidiTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class TrackInteractorTest {

    private TrackInteractor trackInteractor;
    private MidiTrack track;
    private ArgumentCaptor<TrackDto> trackDtoArgumentCaptor = ArgumentCaptor.forClass(TrackDto.class);
    private TrackBoundaryOut boundaryOut;
    private String projectId;
    private ProjectModell projectModell;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        buildTestContext();
        boundaryOut = Mockito.mock(TrackBoundaryOut.class);
        trackInteractor = new TrackInteractor(boundaryOut);
    }

    @Test
    void showTrack() {
        MidiTrack midiTrack = projectModell.getTracks().get(0);
        trackInteractor.showTrack(midiTrack.getId());
        Mockito.verify(boundaryOut).dispayTrack(trackDtoArgumentCaptor.capture());
        assertNotNull(trackDtoArgumentCaptor.getValue());
        assertEquals(0, trackDtoArgumentCaptor.getValue().channel);
        assertEquals(0, trackDtoArgumentCaptor.getValue().program);
    }

    @Test
    void updateProgramChannel() {
    }

    private void buildTestContext() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(0f, 8);

        Track track = sequence.createTrack();

        projectModell = new ProjectModell(sequence);

        projectModell.getTracks().forEach(midiTrack -> midiTrack.updateProgramChannel(0, 0));
        projectId = projectModell.getId();
        InMemoryStore.addProject(projectModell);
    }
}