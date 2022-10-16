package hu.boga.midiai.gui.events;

public class ChannelMappingChangeEvent {

    final String projectId;
    final int channel;
    final int program;

    public ChannelMappingChangeEvent(String projectId, int channel, int program) {
        this.projectId = projectId;
        this.channel = channel;
        this.program = program;
    }

    @Override
    public String toString() {
        return "ChannelMappingChangeEvent{" +
                "projectId='" + projectId + '\'' +
                ", channel=" + channel +
                ", program=" + program +
                '}';
    }

    public String getProjectId() {
        return projectId;
    }

    public int getChannel() {
        return channel;
    }

    public int getProgram() {
        return program;
    }
}
