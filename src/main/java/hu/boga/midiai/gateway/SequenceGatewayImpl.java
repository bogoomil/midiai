package hu.boga.midiai.gateway;

import hu.boga.midiai.core.exceptions.AimidiException;
import hu.boga.midiai.core.midigateway.SequenceGateway;
import hu.boga.midiai.core.modell.AISequence;
import javafx.event.ActionEvent;

import javax.inject.Inject;
import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SequenceGatewayImpl implements SequenceGateway
{
    private static final String DEFAULT_NAME = "new_midi.mid";
    private Sequencer sequencer;
    public List<TrackGatewayImpl> trackAdapters;

    @Inject
    public SequenceGatewayImpl() {
//        fileOptional.ifPresentOrElse(file -> {
//            processFile(fileOptional.get());
//        }, () -> {
//            int resolution = 8;
//            try {
//                this.sequence = new Sequence(Sequence.PPQ, resolution);
//            } catch (InvalidMidiDataException e) {
//                e.printStackTrace();
//                throw new AimidiException("Sequence creation failed: " + e.getMessage());
//            }
//
//        });
//        this.trackAdapters = new ArrayList<>();
//        initSequencer(sequence);
//
//        Arrays.stream(sequence.getTracks()).forEach(track -> trackAdapters.add(new TrackAdapter(track, sequence.getResolution())));
    }

    public Map<Integer, Integer> getChannelMapping(){
        Map<Integer, Integer> retVal = new HashMap<>();
        trackAdapters.forEach(trackAdapter -> {
            List<ShortMessage> programChanges = trackAdapter.getShortMessagesByCommand(ShortMessage.PROGRAM_CHANGE);
            programChanges.forEach(shortMessage -> {
                retVal.put(shortMessage.getChannel(), shortMessage.getData1());
            });
        });
        return retVal;
    }

    public void onPlayCurrentSec(ActionEvent actionEvent)  {
        sequencer.setTickPosition(0);
        sequencer.start();
    }

    public void stopPlayback(ActionEvent actionEvent)  {
        sequencer.stop();
    }

    public void saveSequence(ActionEvent actionEvent) {
        throw new UnsupportedOperationException("Még nincs kész, de már majdnem elkezdtem...");
//        File file = new File("./" + tfFileName.getText());
//        System.out.println("file created: " + file.getAbsolutePath());
//        MidiSystem.write(this.sequenceAdapter.sequence, 1, file);
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

    @Override
    public AISequence initNewSequence() {
        try {
            Sequence sequence = new Sequence(Sequence.PPQ, 8);
            return convertSequenceToAISequence(sequence,DEFAULT_NAME);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new AimidiException("Sequence creation failed: " + e.getMessage());
        }
    }

    private AISequence convertSequenceToAISequence(Sequence sequence, String name) throws InvalidMidiDataException {
        AISequence retVal = new AISequence();
        retVal.setSequence(sequence);
        retVal.setDivision(sequence.getDivisionType());
        retVal.setName(name);
        retVal.setResolution(sequence.getResolution());
        retVal.setTickLength(sequence.getTickLength());

        int tckpm = 4 * sequence.getResolution();
        retVal.setTicksPerMeasure(tckpm);
        retVal.setTicksIn32nds(tckpm / 32);

        return retVal;
    }

    @Override
    public AISequence openFile(String path) {
        try {
            File file = new File(path);
            Sequence sequence = MidiSystem.getSequence(file);
            return convertSequenceToAISequence(sequence, file.getName());
        } catch (InvalidMidiDataException | IOException e) {
            throw new AimidiException(e.getMessage());
        }
    }
}