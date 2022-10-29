package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.TrackModell;
import hu.boga.midiai.core.util.Constants;
import hu.boga.midiai.core.util.MidiUtil;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SequenceToModellConverter {
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
        modell.tempo = getTempo();
        modell.tracks = initTracks(modell.resolution);
        return modell;
    }

    private List<TrackModell> initTracks(int resolution) {
        List<TrackModell> tracks = new ArrayList<>();
        Arrays.stream(sequence.getTracks()).forEach(track -> {
            TrackModell trackModell = TrackModell.createMidiTrack(track, resolution);
            tracks.add(trackModell);
        });
        return tracks;
    }

    private float getTempo() {
        List<MidiEvent> tempoEvents = getMetaEventsByType(Constants.METAMESSAGE_SET_TEMPO);
        if (tempoEvents.size() == 0) {
            return 0;
        } else {
            return MidiUtil.getTempoInBPM((MetaMessage) tempoEvents.get(0).getMessage());
        }
    }

    public List<MidiEvent> getMetaEventsByType(int type) {
        return Arrays.stream(sequence.getTracks())
                .flatMap(midiTrack -> getMetaEventsByType(midiTrack, type).stream())
                .collect(Collectors.toList());
    }

    public List<MidiEvent> getMetaEventsByType(Track track, int type) {
        List<MidiEvent> events = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof MetaMessage && ((MetaMessage) event.getMessage()).getType() == type) {
                events.add(event);
            }
        }
        return events;
    }



}
