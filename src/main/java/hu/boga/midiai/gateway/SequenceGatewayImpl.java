package hu.boga.midiai.gateway;

import hu.boga.midiai.core.exceptions.AimidiException;
import hu.boga.midiai.core.midigateway.SequenceGateway;
import hu.boga.midiai.core.modell.MidiProject;
import javafx.event.ActionEvent;

import javax.inject.Inject;
import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SequenceGatewayImpl implements SequenceGateway {

    private static final String DEFAULT_NAME = "new_midi.mid";
    public List<TrackGatewayImpl> trackAdapters = new ArrayList<>();

    @Inject
    public SequenceGatewayImpl() {
    }

    public Map<Integer, Integer> getChannelMapping() {
        Map<Integer, Integer> retVal = new HashMap<>();
        trackAdapters.forEach(trackAdapter -> {
            List<ShortMessage> programChanges = trackAdapter.getShortMessagesByCommand(ShortMessage.PROGRAM_CHANGE);
            programChanges.forEach(shortMessage -> retVal.put(shortMessage.getChannel(), shortMessage.getData1()));
        });
        return retVal;
    }


    public void saveSequence(ActionEvent actionEvent) {
        throw new UnsupportedOperationException("Még nincs kész, de már majdnem elkezdtem...");
//        File file = new File("./" + tfFileName.getText());
//        System.out.println("file created: " + file.getAbsolutePath());
//        MidiSystem.write(this.sequenceAdapter.sequence, 1, file);
    }

    @Override
    public MidiProject initNewSequence() {
        try {
            Sequence sequence = new Sequence(Sequence.PPQ, 8);
            MidiProject midiProject = new MidiProject(sequence);
            midiProject.setName(DEFAULT_NAME);
            return midiProject;

        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new AimidiException("Sequence creation failed: " + e.getMessage());
        }
    }

    @Override
    public MidiProject openFile(String path) {
        try {
            File file = new File(path);
            Sequence sequence = MidiSystem.getSequence(file);

            Arrays.stream(sequence.getTracks()).forEach(track -> trackAdapters.add(new TrackGatewayImpl(track, sequence.getResolution())));

            MidiProject midiProject = new MidiProject(sequence);
            midiProject.setName(file.getName());
            midiProject.setChannelMapping(getChannelMapping());
            return midiProject;
        } catch (InvalidMidiDataException | IOException e) {
            throw new AimidiException(e.getMessage());
        }
    }
}