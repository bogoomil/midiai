package hu.boga.midiai.gateway;


import hu.boga.midiai.core.exceptions.AimidiException;
import hu.boga.midiai.core.modell.Note;
import hu.boga.midiai.core.modell.ProgramChangeEvent;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrackGatewayImpl {
    private Track track;
    int resolution;

    public TrackGatewayImpl(Track track, int resolution) {
        this.track = track;
        this.resolution = resolution;
    }

    public ArrayList<Note> getNotesFromTrack() {

        ArrayList<MidiEvent> midiEvents = new ArrayList<MidiEvent>();
        ArrayList<Note> notes = new ArrayList<Note>();

        Track tempTrack = cloneTrack();

        int index = 0;

        while (noteOnOffsInTrack(tempTrack) > 0) {
            MidiEvent event = tempTrack.get(index);
            // If this is a note on event, we want to remove it, and the
            // cooresponding note off from the track.
            if (isNoteOnEvent(event)) {
                ShortMessage noteOnMessage = (ShortMessage) event.getMessage();
                MidiEvent noteOff = findMatchingNoteOff(tempTrack, 0, event);

                long length = noteOff.getTick() - event.getTick();

                notes.add(new Note(getNoteValue(event), event.getTick(), length, getVelocity(event), noteOnMessage.getChannel()));

                boolean removeNoteOn = tempTrack.remove(event);
                boolean removeNoteOff = tempTrack.remove(noteOff);

                if (!removeNoteOn) {
                    System.out.println("Could not remove note on event"
                            + " in MidiHelper.getNotesFromTrack()");
                    System.exit(1);
                }
                if (!removeNoteOff) {
                    System.out.println("Could not remove note off event"
                            + " in MidiHelper.getNotesFromTrack()");
                    System.exit(1);
                }

            }
            // We shouldn't ever get to a note off event
            // (because we should have removed it when we found it's note
            // on event).
            else if (isNoteOffEvent(event)) {
                System.out.println("Accidentally got to a note off event"
                        + " in MidiHelper.getNotesFromTrack()");
                System.exit(1);
            }
            // since we got to something that is neither a note on or a note off,
            // we have to increase our index and look at the next note.
            // Otherwise we will have an infinite loop.
            else {
                index++;
            }

        }

        return notes;

    }

    public void addNoteToTrack(int tick, int channel, int lengthInTicks, int midiCode, int velocity) throws InvalidMidiDataException {
        int endInTick = tick + lengthInTicks;
        addShortMessage(tick, ShortMessage.NOTE_ON, channel, midiCode, velocity);
        addShortMessage(endInTick, ShortMessage.NOTE_OFF, channel, midiCode, velocity);
    }

    public Optional<ShortMessage> getNoteOnsChannel() {
        List<ShortMessage> noteOns = getShortMessagesByCommand(ShortMessage.NOTE_ON);
        if(noteOns.size() >= 1) return Optional.of(noteOns.get(0));
        return Optional.empty();
    }

    protected void addShortMessage(int tick, int command, int channel, int data1, int data2) throws InvalidMidiDataException {
        ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(command, channel, data1, data2);
        MidiEvent event = new MidiEvent(shortMessage, tick);
        track.add(event);
    }

    protected List<ShortMessage> getShortMessagesByCommand(int command) {
        List<ShortMessage> retVal = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage) {
                ShortMessage msg = (ShortMessage) event.getMessage();
                if (msg.getCommand() == command) {
                    retVal.add(msg);
                }
            }
        }
        return retVal;
    }

    protected List<MidiEvent> getEventsByCommand(int command) {
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

    /**
     * Return the velocity for a note on or off message.
     */
    private static int getVelocity(MidiMessage noteOnOff) {
        // I can't check for this because isNoteOnMessage calls getVelocity
        //assert isNoteOnMessage(noteOnOff) || isNoteOffMessage(noteOnOff);

        return noteOnOff.getMessage()[2];
    }

    /**
     * Return true if event is a Note Off event.
     */
    private static boolean isNoteOffEvent(MidiEvent event) {
        return isNoteOffMessage(event.getMessage());
    }

    /**
     * Return true if message is a Note On message.
     */
    private static boolean isNoteOnMessage(MidiMessage message) {
        // The status can be a range of values, depending on what channel it is on.
        // Also, the velocity cannot be zero, otherwise it is a note off message.
        return message.getStatus() >= 144 && message.getStatus() < 160
                && getVelocity(message) > 0;
    }

    /**
     * Return true if message is a Note Off message.
     */
    private static boolean isNoteOffMessage(MidiMessage message) {
        // It is a note off event if the status indicates it is a
        // note off message.  Or, if it is a note on message and
        // the velocity is zero.
        return (message.getStatus() >= 128 && message.getStatus() < 144)
                || (message.getStatus() >= 144 && message.getStatus() < 160 && getVelocity(message) == 0);
    }

    /**
     * Return the note value for a note on or note off event.
     */
    private static int getNoteValue(MidiEvent noteOnOff) {
        assert isNoteOnEvent(noteOnOff) || isNoteOffEvent(noteOnOff);

        return getNoteValue(noteOnOff.getMessage());
    }

    /**
     * Return the note value for a note on or off message.
     */
    private static int getNoteValue(MidiMessage noteOnOff) {
        assert isNoteOnMessage(noteOnOff) || isNoteOffMessage(noteOnOff);

        return noteOnOff.getMessage()[1];
    }

    /**
     * Return the velocity for a note on or note off event.
     */
    private static int getVelocity(MidiEvent noteOnOff) {
        // I can't check for this because isNoteOnMessage calls getVelocity
        //assert isNoteOnEvent(noteOnOff) || isNoteOffEvent(noteOnOff);

        return getVelocity(noteOnOff.getMessage());
    }

    private Track cloneTrack() {
        Track newTrack = null;
        int resolution = 8;
        try {
            Sequence seq = new Sequence(Sequence.PPQ, 8);
            newTrack = seq.createTrack();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        assert newTrack != null;

        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            newTrack.add(event);
        }
        assert track.size() == newTrack.size();
        return newTrack;

    }

    /**
     * Return the number of note on and note off events in track.
     */
    private static int noteOnOffsInTrack(Track track) {
        int count = 0;
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (isNoteOnEvent(event) || isNoteOffEvent(event)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Return true if event is a Note On event.
     */
    private static boolean isNoteOnEvent(MidiEvent event) {
        return isNoteOnMessage(event.getMessage());
    }

    /**
     * Take the NOTE_ON event noteOn (which is event i in track), and
     * find the matching NOTE_OFF event.
     */
    private static MidiEvent findMatchingNoteOff(Track track,
                                                 int noteOnIndex, MidiEvent noteOn) {
        assert isNoteOnEvent(noteOn);

        for (int i = noteOnIndex; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (isNoteOffEvent(event)
                    && (getNoteValue(noteOn) == getNoteValue(event))) {
                return event;
            }
        }
        System.exit(1);
        return null;
    }

    public List<ProgramChangeEvent> getProgramChangeEvents() {
        List<ProgramChangeEvent> retVal = new ArrayList<>(0);
        this.getEventsByCommand(ShortMessage.PROGRAM_CHANGE).forEach(event -> {
            ShortMessage shortMessage = (ShortMessage) event.getMessage();
            ProgramChangeEvent programChangeEvent = new ProgramChangeEvent(shortMessage.getChannel(), shortMessage.getData1(), (int) event.getTick());
            retVal.add(programChangeEvent);
        });
        return retVal;
    }

    private void removeEventsByCommand(int command){
        this.getEventsByCommand(command).forEach(midiEvent -> this.track.remove(midiEvent));
    }

    public void addProgramChangeEvent(int channel, int program, int tick) {
        removeEventsByCommand(ShortMessage.PROGRAM_CHANGE);
        try {
            System.out.println("channel: " + channel + ", program: " + program);
            addShortMessage(tick, ShortMessage.PROGRAM_CHANGE, channel, program, 0);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new AimidiException("update programchange event failed");
        }
    }
}
