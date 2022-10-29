package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.tracks.modell.TrackModell;
import hu.boga.midiai.core.util.Constants;
import hu.boga.midiai.core.util.MidiUtil;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrackToModellConverter extends MidiUtils{
    Track track;
    int index;
    int resolution;

    public TrackToModellConverter(final Track track, int index, int resolution) {
        this.track = track;
        this.index = index;
        this.resolution = resolution;
    }

    public TrackModell convert() {
        final TrackModell trackModell = new TrackModell(index);
        getChannel().ifPresent(ch -> trackModell.channel = ch);
        getProgram().ifPresent(p -> trackModell.program = p);
        getTrackName().ifPresent(n -> trackModell.name = n);
        trackModell.resolution = resolution;
        trackModell.notes.addAll(MidiUtil.getNotesFromTrack(track, 128));

        return trackModell;
    }

    private Optional<Integer> getChannel() {
        final List<MidiMessage> programChanges = this.getMidiMessagesByCommand(ShortMessage.PROGRAM_CHANGE);
        if (programChanges.size() == 1) {
            final ShortMessage shortMessage = (ShortMessage) programChanges.get(0);
            return Optional.of(shortMessage.getChannel());
        } else if (programChanges.size() > 1) {
            throw new MidiAiException("Multiple programchanges found in track: " + this.index);
        }
        return Optional.empty();
    }

    private Optional<Integer> getProgram() {
        final List<MidiMessage> programChanges = this.getMidiMessagesByCommand(ShortMessage.PROGRAM_CHANGE);
        if (programChanges.size() == 1) {
            final ShortMessage shortMessage = (ShortMessage) programChanges.get(0);
            return Optional.of(shortMessage.getData1());
        } else if (programChanges.size() > 1) {
            throw new MidiAiException("Multiple programchanges found in track: " + this.index);
        }
        return Optional.empty();

    }

    private List<MidiMessage> getMidiMessagesByCommand(final int command) {
        return this.getMidiEventsByCommand(command).stream().map(MidiEvent::getMessage).collect(Collectors.toList());
    }

    private List<MidiEvent> getMidiEventsByCommand(final int command) {
        final List<MidiEvent> retVal = new ArrayList<>();
        for (int i = 0; i < this.track.size(); i++) {
            final MidiEvent event = this.track.get(i);
            if (event.getMessage() instanceof ShortMessage) {
                final ShortMessage msg = (ShortMessage) event.getMessage();
                if (msg.getCommand() == command) {
                    retVal.add(event);
                }
            }
        }
        return retVal;
    }

    private Optional<String> getTrackName() {
        final List<MidiEvent> trackNameEvents = this.getMetaEventsByType(track, Constants.METAMESSAGE_SET_NAME);
        if (trackNameEvents.size() > 1) {
            throw new MidiAiException("Multiple name found for track: " + this.index);
        } else if (trackNameEvents.size() == 1) {
            final MetaMessage metaMessage = (MetaMessage) trackNameEvents.get(0).getMessage();
            final String name = new String(metaMessage.getData());
            return Optional.of(name);
        }
        return Optional.empty();
    }



}
