package hu.boga.midiai.core.modell;

public class ProgramChangeEvent {
    int channel;
    int program;
    int tick;

    public ProgramChangeEvent(int channel, int program, int tick) {
        this.channel = channel;
        this.program = program;
        this.tick = tick;
    }

    public int getChannel() {
        return channel;
    }

    public int getProgram() {
        return program;
    }

    public int getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return "ProgramChangeEvent{" +
                "channel=" + channel +
                ", program=" + program +
                ", tick=" + tick +
                '}';
    }
}
