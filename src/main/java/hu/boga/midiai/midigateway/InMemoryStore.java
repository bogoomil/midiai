package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.sequence.modell.ProjectModell;
import hu.boga.midiai.core.tracks.modell.MidiTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryStore {
    private static final List<ProjectModell> MIDI_PROJECTS = new ArrayList<>();

    public static Optional<ProjectModell> getProjectById(String projectId) {
//        UUID uuid = UUID.fromString(projectId);
        ProjectModell project = null;
        for(int i = 0; i < MIDI_PROJECTS.size(); i++){
            if(MIDI_PROJECTS.get(i).getId().toString().equals(projectId)){
                project = MIDI_PROJECTS.get(i);
            }
        }
        if(project != null){
            return Optional.of(project);
        }
        return Optional.empty();
//        return MIDI_PROJECTS.stream().filter(midiProject -> midiProject.getId().equals(uuid)).findFirst();
    }

    public static Optional<MidiTrack> getTrackById(String trackId) {
        for (ProjectModell projectModell : MIDI_PROJECTS) {
            Optional<MidiTrack> midiTrackOpt = projectModell.getTrackById(trackId);
            if (projectModell.getTrackById(trackId).isPresent()) {
                return midiTrackOpt;
            }
        }
        return Optional.empty();
    }

    public static String addProject(ProjectModell projectModell) {
        MIDI_PROJECTS.add(projectModell);
        return projectModell.getId();
    }

    public static void removeProject(String id) {
        MIDI_PROJECTS.removeIf(projectModell -> projectModell.getId().equals(UUID.fromString(id)));
    }

    public static Optional<ProjectModell> findMidiProjectByTrackId(String trackId){
        for (ProjectModell projectModell : MIDI_PROJECTS) {
            Optional<MidiTrack> midiTrackOpt = projectModell.getTrackById(trackId);
            if (projectModell.getTrackById(trackId).isPresent()) {
                return Optional.of(projectModell);
            }
        }
        return Optional.empty();

    }
}
