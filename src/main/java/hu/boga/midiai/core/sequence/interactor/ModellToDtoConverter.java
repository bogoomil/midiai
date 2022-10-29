package hu.boga.midiai.core.sequence.interactor;

import hu.boga.midiai.core.sequence.boundary.SequenceDto;
import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.TrackModell;

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
        dto.tickLength = sequenceModell.tickLength;
        dto.resolution = sequenceModell.resolution;
        dto.division = sequenceModell.division;
        dto.ticksPerSecond = sequenceModell.ticksPerSecond();
        dto.tickSize = sequenceModell.tickSize();
        dto.tempo = sequenceModell.tempo;
        dto.name = sequenceModell.name;
        dto.id = sequenceModell.getId();
        dto.trackCount = sequenceModell.tracks.length;
        return dto;

    }
}
