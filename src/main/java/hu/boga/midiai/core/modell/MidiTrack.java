package hu.boga.midiai.core.modell;

import com.google.common.base.Objects;
import hu.boga.midiai.core.exceptions.AimidiException;
import hu.boga.midiai.core.util.TrackNotesRetriever;

import javax.sound.midi.*;
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

    public void updateProgramChannel(int channel, int program) {
        removeEventsByCommand(ShortMessage.PROGRAM_CHANGE);
        addProgramChangeEvent(channel, program, 0);

        getShortMessagesByCommand(ShortMessage.PROGRAM_CHANGE).forEach(shortMessage -> System.out.println(shortMessage.getChannel() + " - " + shortMessage.getData1()));

    }

    public void removeEvents(List<MidiEvent> events){
        events.forEach(this.track::remove);
    }

    private void removeEventsByCommand(int command){
        this.getEventsByCommand(command).forEach(midiEvent -> this.track.remove(midiEvent));
    }

    private void addProgramChangeEvent(int channel, int program, int tick) {
        try {
            System.out.println("channel: " + channel + ", program: " + program);
            addShortMessage(tick, ShortMessage.PROGRAM_CHANGE, channel, program, 0);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new AimidiException("update programchange event failed");
        }
    }

    private List<MidiEvent> getEventsByCommand(int command) {
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

    private List<ShortMessage> getShortMessagesByCommand(int command) {
        List<ShortMessage> retVal = new ArrayList<>();
        getEventsByCommand(command).forEach(midiEvent -> {
            ShortMessage msg = (ShortMessage) midiEvent.getMessage();
            retVal.add(msg);
        });
        return retVal;
    }


    private void addShortMessage(int tick, int command, int channel, int data1, int data2) throws InvalidMidiDataException {
        ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(command, channel, data1, data2);
        MidiEvent event = new MidiEvent(shortMessage, tick);
        track.add(event);
    }

    public void addTempoEvent(long tick, long tempo) {
        long microSecsPerQuarterNote = Constants.MICROSECONDS_IN_MINUTE / tempo;
        MetaMessage metaMessage = new MetaMessage();
        // create the tempo byte array
        byte[] array = new byte[] { 0, 0, 0 };
        for (int i = 0; i < 3; i++) {
            int shift = (3 - 1 - i) * 8;
            array[i] = (byte) (microSecsPerQuarterNote >> shift);
        }
        // now set the message
        try {
            metaMessage.setMessage(Constants.MIDIMESSAGE_SET_TEMPO_TYPE, array, 3);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        track.add(new MidiEvent(metaMessage, tick));
    }

    public List<MidiEvent> getMetaEventsByType(int type){
        List<MidiEvent> events = new ArrayList<>();
        for (int i = 0; i < track.size(); i++){
            MidiEvent event = track.get(i);
            if(event.getMessage() instanceof MetaMessage && ((MetaMessage) event.getMessage()).getType() == type){
                events.add(event);
            }
        }
        return events;
    }
}
