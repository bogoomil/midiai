package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.tracks.modell.TrackModell;

import javax.sound.midi.Track;

public class TrackToModellConverter {
    private String id;
    private Track track;

    public TrackToModellConverter(final String id, final Track track) {
        this.id = id;
        this.track = track;
    }

    public TrackModell convert(){
        TrackModell trackModell = new TrackModell(id);
        trackModell.channel = 0;
        return trackModell;
    }
}
