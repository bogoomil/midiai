package hu.boga.midiai.core.tracks.modell;

import com.google.common.base.Objects;
import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.musictheory.Chord;
import hu.boga.midiai.core.musictheory.Pitch;
import hu.boga.midiai.core.musictheory.enums.ChordType;
import hu.boga.midiai.core.util.Constants;
import hu.boga.midiai.core.util.MidiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class MidiTrack {

    private static final Logger LOG = LoggerFactory.getLogger(MidiTrack.class);

    UUID id = UUID.randomUUID();

    private final Track track;
    private final int resolution;

    private MidiTrack(final Track track, int resolution) {
        this.track = track;
        this.resolution = resolution;
    }

    public static MidiTrack createMidiTrack(final Track track, int resolution) {
        return new MidiTrack(track, resolution);
    }

    public Optional<Integer> getChannel() {
        List<MidiMessage> programChanges = getMidiMessagesByCommand(ShortMessage.PROGRAM_CHANGE);
        if (programChanges.size() == 1) {
            ShortMessage shortMessage = (ShortMessage) programChanges.get(0);
            return Optional.of(shortMessage.getChannel());
        } else if (programChanges.size() > 1) {
            throw new MidiAiException("Multiple programchanges found in track: " + id);
        }
        return Optional.empty();
    }

    public Optional<Integer> getProgram() {
        List<MidiMessage> programChanges = getMidiMessagesByCommand(ShortMessage.PROGRAM_CHANGE);
        if (programChanges.size() == 1) {
            ShortMessage shortMessage = (ShortMessage) programChanges.get(0);
            return Optional.of(shortMessage.getData1());
        } else if (programChanges.size() > 1) {
            throw new MidiAiException("Multiple programchanges found in track: " + id);
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

    public String getId() {
        return this.id.toString();
    }

    public Track getTrack() {
        return track;
    }

    public int getResolution() {
        return resolution;
    }

    public List<Note> getNotes() {
        return MidiUtil.getNotesFromTrack(this);
    }

    public int getNoteCount() {
        return getNotes().size();
    }

    public void updateProgramChannel(int channel, int program) {
        LOG.debug("channel: " + channel + ", program: " + program);
        removeEventsByCommand(ShortMessage.PROGRAM_CHANGE);
        addProgramChangeEvent(channel, program, 0);
    }

    public void removeEvents(List<MidiEvent> events) {
        events.forEach(this.track::remove);
    }

    public void updateTempo(long tick, long tempo) {
        List<MidiEvent> tempoEvents = getMetaEventsByType(Constants.METAMESSAGE_SET_TEMPO);
        removeEvents(tempoEvents);
        long microSecsPerQuarterNote = Constants.MICROSECONDS_IN_MINUTE / tempo;
        byte[] array = new byte[]{0, 0, 0};
        for (int i = 0; i < 3; i++) {
            int shift = (3 - 1 - i) * 8;
            array[i] = (byte) (microSecsPerQuarterNote >> shift);
        }
        track.add(createMetaEvent(0, Constants.METAMESSAGE_SET_TEMPO, array));
    }

    public List<MidiEvent> getMetaEventsByType(int type) {
        List<MidiEvent> events = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof MetaMessage && ((MetaMessage) event.getMessage()).getType() == type) {
                events.add(event);
            }
        }
        return events;
    }

    public Optional<String> getTrackName() {
        List<MidiEvent> trackNameEvents = getMetaEventsByType(Constants.METAMESSAGE_SET_NAME);
        if (trackNameEvents.size() > 1) {
            throw new MidiAiException("Multiple name found for track: " + id.toString());
        } else if (trackNameEvents.size() == 1) {
            MetaMessage metaMessage = (MetaMessage) trackNameEvents.get(0).getMessage();
            String name = new String(metaMessage.getData());
            return Optional.of(name);
        }
        return Optional.empty();
    }

    public void updateTrackName(String name) {
        List<MidiEvent> tempoEvents = getMetaEventsByType(Constants.METAMESSAGE_SET_NAME);
        removeEvents(tempoEvents);

        MidiEvent event = createMetaEvent(0, Constants.METAMESSAGE_SET_NAME, name.getBytes(StandardCharsets.UTF_8));
        track.add(event);

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

    private MidiEvent createMetaEvent(long tick, int type, byte[] array) {
        MetaMessage metaMessage = new MetaMessage();
        try {
            metaMessage.setMessage(type, array, array.length);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new MidiEvent(metaMessage, tick);
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

    private void addProgramChangeEvent(int channel, int program, int tick) {
        try {
            addShortMessage(tick, ShortMessage.PROGRAM_CHANGE, channel, program, 0);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new MidiAiException("update programchange event failed");
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

    private void removeEventsByCommand(int command) {
        this.getEventsByCommand(command).forEach(midiEvent -> this.track.remove(midiEvent));
    }


    public void addNote(int tick, int pitch, int length) {
        try {
            addShortMessage(tick, ShortMessage.NOTE_ON, getChannel().get(), pitch, 100);
            addShortMessage(tick + length, ShortMessage.NOTE_ON, getChannel().get(), pitch, 0);
        } catch (InvalidMidiDataException e) {
            throw new MidiAiException(e.getMessage());
        }
    }

    public void moveNote(int tick, int pitch, int newTick) {
        int index = indexOfNoteOnEvent(tick, pitch);
        MidiEvent noteOn = track.get(index);
        MidiEvent noteOff = MidiUtil.findMatchingNoteOff(track, index, noteOn);
        long length = noteOff.getTick() - noteOn.getTick();
        ShortMessage shortMessage = (ShortMessage) noteOn.getMessage();
        try {
            addShortMessage(newTick, ShortMessage.NOTE_ON, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
            addShortMessage((int) (newTick + length), ShortMessage.NOTE_OFF, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        track.remove(noteOn);
        track.remove(noteOff);
    }

    private int indexOfNoteOnEvent(int tick, int pitch){
        int index = 0;
        for (int i = 0; i < track.size(); i++){
            MidiEvent event = track.get(i);
            if(event.getTick() == tick){
                if(event.getMessage() instanceof ShortMessage){
                    ShortMessage shortMessage = (ShortMessage) event.getMessage();
                    if(shortMessage.getCommand() == ShortMessage.NOTE_ON && shortMessage.getData1() == pitch) {
                        index = i;
                    }
                }
            }
        }
        LOG.debug("index of noteOn: " + index);
        return index;
    }

    public void deleteNote(int tick, int pitch) {
        int index = indexOfNoteOnEvent(tick, pitch);
        MidiEvent noteOn = track.get(index);
        MidiEvent noteOff = MidiUtil.findMatchingNoteOff(track, index, noteOn);
        track.remove(noteOn);
        track.remove(noteOff);
        LOG.debug("Note on: " + noteOn + ", note off: " + noteOff);

    }

    public void addChord(int tick, int pitch, int length, ChordType chordType) {
        Chord chord = Chord.getChord(new Pitch(pitch), chordType);
        Arrays.stream(chord.getPitches()).forEach(pitch1 -> {
            addNote(tick, pitch1.getMidiCode(), length);
        });
    }


}
