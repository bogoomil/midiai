package hu.boga.midiai.gui.events;

public class TrackDeleteEvent {
    String trackId;

    public TrackDeleteEvent(final String trackId) {
        this.trackId = trackId;
    }

    public String getTrackId(){
        return trackId;
    }
}
