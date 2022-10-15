package hu.boga.midiai.gui.events;

public class ChannelMappingChangeEvent {

    int channel, program;

    public ChannelMappingChangeEvent(int channel, int program) {
        this.channel = channel;
        this.program = program;
    }

    @Override
    public String toString() {
        return "ChannelMappingChangeEvent{" +
                "channel=" + channel +
                ", program=" + program +
                '}';
    }
}
