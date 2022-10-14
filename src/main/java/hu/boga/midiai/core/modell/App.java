package hu.boga.midiai.core.modell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class App {
    private static final List<MidiProject> MIDI_PROJECTS = new ArrayList<>();

    public static Optional<MidiProject> getProjectById(String id){
        UUID uuid = UUID.fromString(id);
        Optional<MidiProject> project = MIDI_PROJECTS.stream().filter(midiProject -> midiProject.id.equals(uuid)).findFirst();
        return project;
    }

    public static  void addProject(MidiProject midiProject){
        MIDI_PROJECTS.add(midiProject);
    }

    public static void removeProject(String id){
        MIDI_PROJECTS.removeIf(midiProject -> midiProject.id.equals(UUID.fromString(id)));
    }
}
