package hu.boga.midiai.core.modell;

import com.google.common.base.Objects;
import hu.boga.midiai.core.exceptions.AimidiException;
import hu.boga.midiai.core.util.TrackNotesRetriever;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MidiTrack {

    UUID id = UUID.randomUUID();

    private final Track track;
    private final int resolution;

    public MidiTrack(Track track, int resolution) {
        this.track = track;
        this.resolution = resolution;
    }

    public Optional<Integer> getChannel(){
        List<MidiMessage> programChanges = getMidiMessagesByCommand(ShortMessage.PROGRAM_CHANGE);
        if(programChanges.size() == 1){
            ShortMessage shortMessage = (ShortMessage) programChanges.get(0);
            return Optional.of(shortMessage.getChannel());
        } else if (programChanges.size() > 1){
            throw new AimidiException("Multiple programchanges found in track: " + id);
        }
        return Optional.empty();
    }

    public Optional<Integer> getProgram(){
        List<MidiMessage> programChanges = getMidiMessagesByCommand(ShortMessage.PROGRAM_CHANGE);
        if(programChanges.size() == 1){
            ShortMessage shortMessage = (ShortMessage) programChanges.get(0);
            return Optional.of(shortMessage.getData1());
        } else if (programChanges.size() > 1){
            throw new AimidiException("Multiple programchanges found in track: " + id);
        }
        return Optional.empty();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MidiTrack midiTrack = (MidiTrack) o;
        return Objects.equal(id, midiTrack.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private List<MidiMessage> getMidiMessagesByCommand(int command) {
        return getMidiEventsByCommand(command).stream().map(MidiEvent::getMessage).collect(Collectors.toList());
    }

    private List<MidiEvent> getMidiEventsByCommand(int command) {
        List<MidiEvent> retVal = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage) {
                ShortMessage msg = (ShortMessage) event.getMessage();
                if (msg.getCommand() == command) {
                    retVal.add(event);
                }
            }
        }
        return retVal;
    }

    public String getId() {
        return this.id.toString();
    }

    public Track getTrack(){
        return track;
    }

    public int getResolution(){
        return resolution;
    }

    public List<Note> getNotes() {
        return TrackNotesRetriever.getNotesFromTrack(this);
    }

    public int getNoteCount() {
        return getNotes().size();
    }

}
