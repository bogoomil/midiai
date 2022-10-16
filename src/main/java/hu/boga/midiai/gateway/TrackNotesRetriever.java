package hu.boga.midiai.gateway;

import hu.boga.midiai.core.modell.Note;

import javax.sound.midi.*;
import java.util.ArrayList;

public class TrackNotesRetriever {

    public static ArrayList<Note> getNotesFromTrack(Track track) {

        ArrayList<MidiEvent> midiEvents = new ArrayList<MidiEvent>();
        ArrayList<Note> notes = new ArrayList<Note>();

        Track tempTrack = cloneTrack(track);

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

    private static Track cloneTrack(Track track) {
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

    /**
     * Return the velocity for a note on or off message.
     */
    private static int getVelocity(MidiMessage noteOnOff) {
        // I can't check for this because isNoteOnMessage calls getVelocity
        //assert isNoteOnMessage(noteOnOff) || isNoteOffMessage(noteOnOff);

        return noteOnOff.getMessage()[2];
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
    private static MidiEvent findMatchingNoteOff(Track track, int noteOnIndex, MidiEvent noteOn) {
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


}
