package hu.boga.midiai.core.boundaries.dtos;

public class TrackDto {
    public String trackId;
    public int channel;
    public int program;
    public int noteCount;
    public int resolution;
    public NoteDto[] notes;
    public String name;
}
