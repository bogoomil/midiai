package hu.boga.midiai.core.modell;

public class AISequence {
    private String name;
    private int resolution;
    private float division;
    private long tickLength;
    private int ticksPerMeasure;
    private int ticksIn32nds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public float getDivision() {
        return division;
    }

    public void setDivision(float division) {
        this.division = division;
    }

    public long getTickLength() {
        return tickLength;
    }

    public void setTickLength(long tickLength) {
        this.tickLength = tickLength;
    }

    public int getTicksPerMeasure() {
        return ticksPerMeasure;
    }

    public void setTicksPerMeasure(int ticksPerMeasure) {
        this.ticksPerMeasure = ticksPerMeasure;
    }

    public int getTicksIn32nds() {
        return ticksIn32nds;
    }

    public void setTicksIn32nds(int ticksIn32nds) {
        this.ticksIn32nds = ticksIn32nds;
    }
}
