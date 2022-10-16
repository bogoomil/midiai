package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.SequenceDto;
import hu.boga.midiai.core.boundaries.dtos.TrackDto;
import hu.boga.midiai.core.midigateway.SequenceGateway;
import hu.boga.midiai.core.modell.App;
import hu.boga.midiai.core.modell.MidiProject;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SequenceInteractor implements SequenceBoundaryIn {
    private final SequenceBoundaryOut boundaryOut;
    private final SequenceGateway sequenceGateway;

    @Inject
    public SequenceInteractor(SequenceBoundaryOut boundaryOut, SequenceGateway sequenceGateway) {
        this.boundaryOut = boundaryOut;
        this.sequenceGateway = sequenceGateway;
    }

    @Override
    public void initNewSequence() {
        MidiProject midiProject = this.sequenceGateway.initNewSequence();
        App.addProject(midiProject);
        this.boundaryOut.displaySequence(convertSequenceToDto(midiProject));
    }

    @Override
    public void openFile(File file) {
        MidiProject midiProject = this.sequenceGateway.openFile(file.getAbsolutePath());
        App.addProject(midiProject);
        this.boundaryOut.displaySequence(convertSequenceToDto(midiProject));
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

    @Override
    public void updateChnnellMapping(String projetId, int channel, int program) {
        Optional<MidiProject> opt = App.getProjectById(projetId);
        opt.ifPresent(midiProject -> midiProject.getChannelMapping().put(channel, program));
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
        dto.channelMapping = midiProject.getChannelMapping();

        dto.tracks = getTracks(midiProject);

        return dto;

    }

    private List<TrackDto> getTracks(MidiProject midiProject) {
        List<TrackDto> retVal = new ArrayList<>();
       midiProject.getTracks().forEach(midiTrack -> {
           TrackDto trackDto = new TrackDto();
           trackDto.trackId = midiTrack.getId();
           midiTrack.getChannel().ifPresent(ch -> trackDto.channel = ch);
           midiTrack.getProgram().ifPresent(pr -> trackDto.program = pr);
           retVal.add(trackDto);
       });

        return retVal;
    }


}
