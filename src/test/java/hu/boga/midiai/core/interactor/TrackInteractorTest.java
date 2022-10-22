package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.TrackBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.core.modell.App;
import hu.boga.midiai.core.modell.MidiProject;
import hu.boga.midiai.core.modell.MidiTrack;
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
    private MidiProject midiProject;

    @BeforeEach
    void setUp() throws InvalidMidiDataException {
        buildTestContext();
        boundaryOut = Mockito.mock(TrackBoundaryOut.class);
        trackInteractor = new TrackInteractor(boundaryOut);
    }

    @Test
    void showTrack() {
        MidiTrack midiTrack = midiProject.getTracks().get(0);
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

        midiProject = new MidiProject(sequence);

        midiProject.getTracks().forEach(midiTrack -> midiTrack.updateProgramChannel(0, 0));
        projectId = midiProject.getId();
        App.addProject(midiProject);
    }
}