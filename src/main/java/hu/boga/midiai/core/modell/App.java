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
                System.out.println("getTrackById FOUND: " + trackId);
                return midiTrackOpt;
            }
        }
        System.out.println("getTrackById NOT FOUND: " + trackId);
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
            System.out.println("findMidiProjectByTrackId MIDO PROJECT: " + midiProject.id);
            Optional<MidiTrack> midiTrackOpt = midiProject.getTrackById(trackId);
            if (midiProject.getTrackById(trackId).isPresent()) {
                System.out.println("findMidiProjectByTrackId FOUND: " + trackId);
                return Optional.of(midiProject);
            }
        }
        System.out.println("findMidiProjectByTrackId NOT FOUND: " + trackId);
        return Optional.empty();

    }
}
