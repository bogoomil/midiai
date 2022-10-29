package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.musictheory.Chord;
import hu.boga.midiai.core.musictheory.Pitch;
import hu.boga.midiai.core.musictheory.enums.ChordType;
import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.TrackModell;
import hu.boga.midiai.core.util.Constants;
import hu.boga.midiai.core.util.MidiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SequenceToModellConverter extends MidiUtils{

    private static final Logger LOG = LoggerFactory.getLogger(SequenceToModellConverter.class);

    private Sequence sequence;
    private String id;


    public SequenceToModellConverter(Sequence sequence, String id) {
        this.id = id;
        this.sequence = sequence;
    }

    public SequenceModell convert() {
        SequenceModell modell = new SequenceModell(id.toString());
        modell.resolution = sequence.getResolution();
        modell.division = sequence.getDivisionType();
        modell.tickLength = sequence.getTickLength();
        getTempo().ifPresent(t -> modell.tempo = t);
        modell.tracks = convertTracks();

        LOG.debug(modell.toString());

        return modell;
    }

    private Optional<Integer> getTempo() {
        List<MidiEvent> tempoEvents = getMetaEventsByType(Constants.METAMESSAGE_SET_TEMPO);
        if (tempoEvents.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(getTempoInBPM((MetaMessage) tempoEvents.get(0).getMessage()));
        }
    }

    private List<MidiEvent> getMetaEventsByType(int type) {
        return Arrays.stream(sequence.getTracks())
                .flatMap(midiTrack -> getMetaEventsByType(midiTrack, type).stream())
                .collect(Collectors.toList());
    }


    public TrackModell[] convertTracks() {
        Track[] tracks = sequence.getTracks();
        TrackModell[] trackModells = new TrackModell[tracks.length];
        for(int i = 0; i < tracks.length; i++){
            Track track = tracks[i];
            trackModells[i] = new TrackToModellConverter(track, i, sequence.getResolution()).convert();

        }
        return trackModells;
    }

//    public void updateProgramChannel(final int channel, final int program) {
//        this.removeEventsByCommand(ShortMessage.PROGRAM_CHANGE);
//        this.addProgramChangeEvent(channel, program, 0);
//    }

//    public void removeEvents(final List<MidiEvent> events) {
//        events.forEach(track::remove);
//    }


//    public void updateTrackName(final String name) {
//        final List<MidiEvent> tempoEvents = this.getMetaEventsByType(Constants.METAMESSAGE_SET_NAME);
//        this.removeEvents(tempoEvents);
//
//        final MidiEvent event = this.createMetaEvent(0, Constants.METAMESSAGE_SET_NAME, name.getBytes(StandardCharsets.UTF_8));
//        this.track.add(event);
//
//    }
//
//    private List<ShortMessage> getShortMessagesByCommand(final int command) {
//        final List<ShortMessage> retVal = new ArrayList<>();
//        this.getEventsByCommand(command).forEach(midiEvent -> {
//            final ShortMessage msg = (ShortMessage) midiEvent.getMessage();
//            retVal.add(msg);
//        });
//        return retVal;
//    }
//
//    private void addShortMessage(final int tick, final int command, final int channel, final int data1, final int data2) throws InvalidMidiDataException {
//        final ShortMessage shortMessage = new ShortMessage();
//        shortMessage.setMessage(command, channel, data1, data2);
//        final MidiEvent event = new MidiEvent(shortMessage, tick);
//        this.track.add(event);
//    }
//
//    private void addProgramChangeEvent(final int channel, final int program, final int tick) {
//        try {
//            this.addShortMessage(tick, ShortMessage.PROGRAM_CHANGE, channel, program, 0);
//        } catch (final InvalidMidiDataException e) {
//            e.printStackTrace();
//            throw new MidiAiException("update programchange event failed");
//        }
//    }
//
//    private List<MidiEvent> getEventsByCommand(final int command) {
//        final List<MidiEvent> retVal = new ArrayList<>();
//        for (int i = 0; i < this.track.size(); i++) {
//            final MidiEvent event = this.track.get(i);
//            if (event.getMessage() instanceof ShortMessage) {
//                final ShortMessage msg = (ShortMessage) event.getMessage();
//                if (msg.getCommand() == command) {
//                    retVal.add(event);
//                }
//            }
//        }
//        return retVal;
//    }
//
//    private void removeEventsByCommand(final int command) {
//        getEventsByCommand(command).forEach(midiEvent -> track.remove(midiEvent));
//    }
//
//
//    public void addNote(final int tick, final int pitch, final int length) {
//        try {
//            this.addShortMessage(tick, ShortMessage.NOTE_ON, this.getChannel().get(), pitch, 100);
//            this.addShortMessage(tick + length, ShortMessage.NOTE_ON, this.getChannel().get(), pitch, 0);
//        } catch (final InvalidMidiDataException e) {
//            throw new MidiAiException(e.getMessage());
//        }
//    }
//
//    public void moveNote(final int tick, final int pitch, final int newTick) {
//        final int index = this.indexOfNoteOnEvent(tick, pitch);
//        final MidiEvent noteOn = this.track.get(index);
//        final MidiEvent noteOff = MidiUtil.findMatchingNoteOff(this.track, index, noteOn);
//        final long length = noteOff.getTick() - noteOn.getTick();
//        final ShortMessage shortMessage = (ShortMessage) noteOn.getMessage();
//        try {
//            this.addShortMessage(newTick, ShortMessage.NOTE_ON, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
//            this.addShortMessage((int) (newTick + length), ShortMessage.NOTE_OFF, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
//        } catch (final InvalidMidiDataException e) {
//            e.printStackTrace();
//        }
//        this.track.remove(noteOn);
//        this.track.remove(noteOff);
//    }
//
//    private int indexOfNoteOnEvent(final int tick, final int pitch) {
//        int index = 0;
//        for (int i = 0; i < this.track.size(); i++) {
//            final MidiEvent event = this.track.get(i);
//            if (event.getTick() == tick) {
//                if (event.getMessage() instanceof ShortMessage) {
//                    final ShortMessage shortMessage = (ShortMessage) event.getMessage();
//                    if (shortMessage.getCommand() == ShortMessage.NOTE_ON && shortMessage.getData1() == pitch) {
//                        index = i;
//                    }
//                }
//            }
//        }
//        return index;
//    }
//
//    public void deleteNote(final int tick, final int pitch) {
//        final int index = this.indexOfNoteOnEvent(tick, pitch);
//        final MidiEvent noteOn = this.track.get(index);
//        final MidiEvent noteOff = MidiUtil.findMatchingNoteOff(this.track, index, noteOn);
//        this.track.remove(noteOn);
//        this.track.remove(noteOff);
//
//    }
//
//    public void addChord(final int tick, final int pitch, final int length, final ChordType chordType) {
//        final Chord chord = Chord.getChord(new Pitch(pitch), chordType);
//        Arrays.stream(chord.getPitches()).forEach(pitch1 -> {
//            this.addNote(tick, pitch1.getMidiCode(), length);
//        });
//    }


}
