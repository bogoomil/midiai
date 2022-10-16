package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.SequenceDto;
import hu.boga.midiai.core.exceptions.AimidiException;
import hu.boga.midiai.core.midigateway.SequenceGateway;
import hu.boga.midiai.core.modell.App;
import hu.boga.midiai.core.modell.MidiProject;
import hu.boga.midiai.core.modell.MidiTrack;
import hu.boga.midiai.core.util.AppConstants;

import javax.inject.Inject;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class SequenceInteractor implements SequenceBoundaryIn {
    private final SequenceBoundaryOut boundaryOut;

    @Inject
    public SequenceInteractor(SequenceBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void openFile(File file) {
        openFile(file.getAbsolutePath());
    }

    @Override
    public void playSequence(String id, int fromTick, int toTick) {
        App.getProjectById(id).ifPresent(midiProject -> midiProject.play(fromTick, toTick));
    }

    @Override
    public void playSequence(String id, int fromTick) {
        App.getProjectById(id).ifPresent(midiProject -> midiProject.play(fromTick));
    }

    @Override
    public void playSequence(String id) {
        App.getProjectById(id).ifPresent(MidiProject::play);
    }

    @Override
    public void playLoop(String projectId, int fromTick, int toTick) {
        App.getProjectById(projectId).ifPresent(midiProject -> midiProject.playLoop(fromTick, toTick));
    }

    @Override
    public void stopPlayBack(String id) {
        App.getProjectById(id).ifPresent(MidiProject::stop);
    }

    @Override
    public void saveSequence(String projectId, String filePath) {
        App.getProjectById(projectId).ifPresent(midiProject -> midiProject.save(filePath));
    }


    private SequenceDto convertSequenceToDto(MidiProject midiProject) {
        SequenceDto dto = new SequenceDto();
        dto.ticksIn32nds = midiProject.getTicksIn32nds();
        dto.ticksPerMeasure = midiProject.getTicksPerMeasure();
        dto.tickLength = midiProject.getTickLength();
        dto.resolution = midiProject.getResolution();
        dto.division = midiProject.getDivision();
        dto.ticksPerSecond = midiProject.ticksPerSecond();
        dto.tickSize = midiProject.tickSize();
        dto.tempo = midiProject.getTempo();
        dto.name = midiProject.getName();
        dto.id = midiProject.getId();

        dto.tracks = midiProject.getTracks().stream().map(MidiTrack::getId).collect(Collectors.toList());

        return dto;

    }

    public void initNewSequence() {
        try {
            Sequence sequence = new Sequence(Sequence.PPQ, 8);
            MidiProject midiProject = new MidiProject(sequence);
            midiProject.setName(AppConstants.DEFAULT_NAME);
            App.addProject(midiProject);
            this.boundaryOut.displaySequence(convertSequenceToDto(midiProject));

        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new AimidiException("Sequence creation failed: " + e.getMessage());
        }
    }

    public void openFile(String path) {
        try {
            File file = new File(path);
            Sequence sequence = MidiSystem.getSequence(file);
            MidiProject midiProject = new MidiProject(sequence);
            midiProject.setName(file.getName());
            App.addProject(midiProject);
            this.boundaryOut.displaySequence(convertSequenceToDto(midiProject));
        } catch (InvalidMidiDataException | IOException e) {
            throw new AimidiException(e.getMessage());
        }
    }

}
