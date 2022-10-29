package hu.boga.midiai.core.tracks.modell;

import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TrackModell {

    private static final Logger LOG = LoggerFactory.getLogger(TrackModell.class);

    private final int index;
    public int resolution;
    public int channel;
    public int program;
    public String name;
    public final List<NoteModell> notes = new ArrayList<>();


    public TrackModell(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackModell trackModell = (TrackModell) o;
        return Objects.equal(index, trackModell.index);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(index);
    }

    public int getIndex() {
        return this.index;
    }

    public int getResolution() {
        return resolution;
    }

    @Override
    public String toString() {
        return "TrackModell{" +
                "index=" + index +
                ", resolution=" + resolution +
                ", channel=" + channel +
                ", program=" + program +
                ", name='" + name + '\'' +
                ", notes=" + notes +
                '}';
    }
}
