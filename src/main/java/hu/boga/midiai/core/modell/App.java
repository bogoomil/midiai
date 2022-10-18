package hu.boga.midiai.core.modell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class App {
    private static final List<MidiProject> MIDI_PROJECTS = new ArrayList<>();

    public static Optional<MidiProject> getProjectById(String projectId) {
        UUID uuid = UUID.fromString(projectId);
        return MIDI_PROJECTS.stream().filter(midiProject -> midiProject.id.equals(uuid)).findFirst();
    }

    public static Optional<MidiTrack> getTrackById(String trackId) {
        for (MidiProject midiProject : MIDI_PROJECTS) {
            Optional<MidiTrack> midiTrackOpt = midiProject.getTrackById(trackId);
            if (midiProject.getTrackById(trackId).isPresent()) {
                return midiTrackOpt;
            }
        }
        return Optional.empty();
    }

    public static void addProject(MidiProject midiProject) {
        MIDI_PROJECTS.add(midiProject);
    }

    public static void removeProject(String id) {
        MIDI_PROJECTS.removeIf(midiProject -> midiProject.id.equals(UUID.fromString(id)));
    }

    public static Optional<MidiProject> findMidiProjectByTrackId(String trackId){
        for (MidiProject midiProject : MIDI_PROJECTS) {
            Optional<MidiTrack> midiTrackOpt = midiProject.getTrackById(trackId);
            if (midiProject.getTrackById(trackId).isPresent()) {
                return Optional.of(midiProject);
            }
        }
        return Optional.empty();

    }
}
