package hu.boga.midiai.core.sequence.modell;

import com.google.common.base.Objects;
import hu.boga.midiai.core.tracks.modell.TrackModell;

import java.util.Arrays;

public class SequenceModell {
    private  final String id;
    public String name;
    public TrackModell[] tracks;
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

    public void setTempo(float tempo) {
        this.tempo = tempo;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "SequenceModell{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tracks=" + Arrays.toString(tracks) +
                ", resolution=" + resolution +
                ", division=" + division +
                ", tickLength=" + tickLength +
                ", tempo=" + tempo +
                '}';
    }
}
