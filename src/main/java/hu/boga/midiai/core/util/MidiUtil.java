package hu.boga.midiai.core.util;

import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.modell.MidiTrack;
import hu.boga.midiai.core.modell.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.util.ArrayList;

public class MidiUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MidiUtil.class);

    public static ArrayList<Note> getNotesFromTrack(MidiTrack midiTrack) {


        ArrayList<MidiEvent> midiEvents = new ArrayList<>();
        ArrayList<Note> notes = new ArrayList<>();

        Track tempTrack = cloneTrack(midiTrack.getTrack(), midiTrack.getResolution());

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
                    throw new MidiAiException("INVALID MIDI DATA");
                }
                if (!removeNoteOff) {
                    throw new MidiAiException("INVALID MIDI DATA");
                }

            }
            // We shouldn't ever get to a note off event
            // (because we should have removed it when we found it's note
            // on event).
            else if (isNoteOffEvent(event)) {
                throw new MidiAiException("INVALID MIDI DATA");
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

    private static Track cloneTrack(Track track, int resolution) {
        Track newTrack = null;
        try {
            Sequence seq = new Sequence(Sequence.PPQ, resolution);
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
    public static MidiEvent findMatchingNoteOff(Track track, int noteOnIndex, MidiEvent noteOn) {
        assert isNoteOnEvent(noteOn);

        LOG.debug("Track size:" + track.size());

        for (int i = noteOnIndex; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            LOG.debug("i: " + i + ": " + event);
            if (isNoteOffEvent(event)
                    && (getNoteValue(noteOn) == getNoteValue(event))) {
                return event;
            }
        }
        return null;
    }


    public static int getTempoInBPM(MetaMessage mm) {
        byte[] data = mm.getData();
        if (mm.getType() != 81 || data.length != 3) {
            throw new IllegalArgumentException("mm=" + mm);
        }
        int mspq = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
        int tempo = Math.round(60000001f / mspq);
        return tempo;
    }

}
