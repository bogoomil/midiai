package hu.boga.midiai.midigateway;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class MidiUtils {
    public static List<MidiEvent> getMetaEventsByType(Track track, int type) {
        List<MidiEvent> events = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof MetaMessage && ((MetaMessage) event.getMessage()).getType() == type) {
                events.add(event);
            }
        }
        return events;
    }

    public static Integer getTempoInBPM(MetaMessage mm) {
        byte[] data = mm.getData();
        if (mm.getType() != 81 || data.length != 3) {
            throw new IllegalArgumentException("mm=" + mm);
        }
        int mspq = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
        int tempo = Math.round(60000001f / mspq);
        return tempo;
    }

    public static MidiEvent createMetaEvent(final long tick, final int type, final byte[] array) {
        final MetaMessage metaMessage = new MetaMessage();
        try {
            metaMessage.setMessage(type, array, array.length);
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new MidiEvent(metaMessage, tick);
    }

    public static MidiEvent addShortMessage(final int tick, final int command, final int channel, final int data1, final int data2) throws InvalidMidiDataException {
        final ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(command, channel, data1, data2);
        final MidiEvent event = new MidiEvent(shortMessage, tick);
        return event;
    }

    public static List<MidiEvent> getEventsByCommand(Track track , final int command) {
        final List<MidiEvent> retVal = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            final MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage) {
                final ShortMessage msg = (ShortMessage) event.getMessage();
                if (msg.getCommand() == command) {
                    retVal.add(event);
                }
            }
        }
        return retVal;
    }

    public static void removeEventsByCommand(Track track, final int command) {
        getEventsByCommand(track, command).forEach(midiEvent -> track.remove(midiEvent));
    }



}
