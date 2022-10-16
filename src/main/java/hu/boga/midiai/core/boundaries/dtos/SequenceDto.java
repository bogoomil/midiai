package hu.boga.midiai.core.boundaries.dtos;

import java.util.List;
import java.util.Map;

public class SequenceDto {
    public String name;
    public int resolution;
    public float division;
    public long tickLength;
    public int ticksPerMeasure;
    public int ticksIn32nds;
    public String id;
    public float ticksPerSecond;
    public float tickSize;
    public float tempo;
    public Map<Integer, Integer> channelMapping;

    public List<String> tracks;

}
