package hu.boga.midiai.core.sequence.interactor;

import hu.boga.midiai.core.sequence.boundary.SequenceDto;
import hu.boga.midiai.core.sequence.
        modell.ProjectModell;
import hu.boga.midiai.core.tracks.modell.MidiTrack;

import java.util.stream.Collectors;

public class ModellToDtoConverter {
    ProjectModell projectModell;

    public ModellToDtoConverter(final ProjectModell modell) {
        this.projectModell = modell;
    }

    public SequenceDto convert(){
        SequenceDto dto = new SequenceDto();
        dto.ticksIn32nds = projectModell.getTicksIn32nds();
        dto.ticksPerMeasure = projectModell.getTicksPerMeasure();
        dto.tickLength = projectModell.getTickLength();
        dto.resolution = projectModell.getResolution();
        dto.division = projectModell.getDivision();
        dto.ticksPerSecond = projectModell.ticksPerSecond();
        dto.tickSize = projectModell.tickSize();
        dto.tempo = projectModell.getTempo();
        dto.name = projectModell.getName();
        dto.id = projectModell.getId();

        dto.tracks = projectModell.getTracks().stream().map(MidiTrack::getId).collect(Collectors.toList());

        return dto;

    }
}
