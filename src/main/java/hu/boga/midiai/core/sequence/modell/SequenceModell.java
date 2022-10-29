package hu.boga.midiai.core.sequence.modell;

import com.google.common.base.Objects;
import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.tracks.modell.TrackModell;
import hu.boga.midiai.core.util.Constants;
import hu.boga.midiai.core.util.MidiUtil;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SequenceModell {
    private  final String id;
    public String name;
    public List<TrackModell> tracks;
    public int resolution;
    public float division;
    public long tickLength;
    public float tempo;


    public SequenceModell(final String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequenceModell that = (SequenceModell) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public int getTicksPerMeasure() {
        return 4 * resolution;
    }

    public int getTicksIn32nds() {
        return getTicksPerMeasure() / 32;
    }

    public float ticksPerSecond() {
        return resolution * (tempo / 60);
    }

    public float tickSize() {
        return 1 / ticksPerSecond();
    }

//

    public String getId() {
        return this.id.toString();
    }


    public void setTempo(float tempo) {
        this.tempo = tempo;
        //        this.getTracks().forEach(track -> {
//            track.updateTempo(0L, (long) tempo);
//        });
    }

    public List<TrackModell> getTracks() {
        return tracks;
    }

    public Optional<TrackModell> getTrackById(String id) {
        return tracks.stream().filter(midiTrack -> midiTrack.getId().toString().equals(id)).findFirst();
    }

    public TrackModell createNewTrack() {
        Track track = sequence.createTrack();
        TrackModell trackModell = TrackModell.createMidiTrack(track, getResolution());
        tracks.add(trackModell);
        return trackModell;
    }

    public void removeTrackById(String trackId) {
        getTrackById(trackId).ifPresent(midiTrack -> {
            sequence.deleteTrack(midiTrack.getTrack());
            this.tracks.remove(midiTrack);
        });
    }

}
