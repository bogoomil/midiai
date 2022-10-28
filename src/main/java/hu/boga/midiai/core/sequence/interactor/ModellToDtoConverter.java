package hu.boga.midiai.core.sequence.interactor;

import hu.boga.midiai.core.sequence.boundary.SequenceDto;
import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.MidiTrack;

import java.util.stream.Collectors;

public class ModellToDtoConverter {
    SequenceModell sequenceModell;

    public ModellToDtoConverter(final SequenceModell modell) {
        this.sequenceModell = modell;
    }

    public SequenceDto convert(){
        SequenceDto dto = new SequenceDto();
        dto.ticksIn32nds = sequenceModell.getTicksIn32nds();
        dto.ticksPerMeasure = sequenceModell.getTicksPerMeasure();
        dto.tickLength = sequenceModell.getTickLength();
        dto.resolution = sequenceModell.getResolution();
        dto.division = sequenceModell.getDivision();
        dto.ticksPerSecond = sequenceModell.ticksPerSecond();
        dto.tickSize = sequenceModell.tickSize();
        dto.tempo = sequenceModell.getTempo();
        dto.name = sequenceModell.getName();
        dto.id = sequenceModell.getId();

        dto.tracks = sequenceModell.getTracks().stream().map(MidiTrack::getId).collect(Collectors.toList());

        return dto;

    }
}
