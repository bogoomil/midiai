package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.SequenceDto;
import hu.boga.midiai.core.midigateway.SequenceGateway;
import hu.boga.midiai.core.modell.AISequence;

import javax.inject.Inject;
import javax.sound.midi.*;
import java.io.File;

public class SequenceInteractor implements SequenceBoundaryIn {
    private final SequenceBoundaryOut boundaryOut;
    private final SequenceGateway sequenceGateway;
    private Sequencer sequencer;

    @Inject
    public SequenceInteractor(SequenceBoundaryOut boundaryOut, SequenceGateway sequenceGateway) {
        this.boundaryOut = boundaryOut;
        this.sequenceGateway = sequenceGateway;
    }

    @Override
    public void initNewSequence() {
        AISequence sequence = this.sequenceGateway.initNewSequence();
        initSequencer(sequence.getSequence());
        this.boundaryOut.displaySequence(convertSequenceToDto(sequence));
    }

    @Override
    public void openFile(File file) {
        System.out.println("Opening file: " + file.getAbsolutePath());
        AISequence sequence = this.sequenceGateway.openFile(file.getAbsolutePath());
        initSequencer(sequence.getSequence());
        SequenceDto dto = convertSequenceToDto(sequence);
        this.boundaryOut.displaySequence(dto);
    }

    @Override
    public void playSequence() {
        sequencer.setTickPosition(0);
        sequencer.start();

    }

    @Override
    public void stopPlayBack() {
        sequencer.stop();
    }

    private void initSequencer(Sequence sequence) {
        try {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
            this.sequencer.setSequence(sequence);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }


    private SequenceDto convertSequenceToDto(AISequence sequence) {
        SequenceDto dto = new SequenceDto();
        dto.ticksIn32nds = sequence.getTicksIn32nds();
        dto.ticksPerMeasure = sequence.getTicksPerMeasure();
        dto.tickLength = sequence.getTickLength();
        dto.resolution = sequence.getResolution();
        dto.division = sequence.getDivision();
        dto.name = sequence.getName();
        return dto;

    }


}
