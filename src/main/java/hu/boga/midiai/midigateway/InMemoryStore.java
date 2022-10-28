package hu.boga.midiai.midigateway;

import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.TrackModell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryStore {
    private static final List<SequenceModell> MIDI_PROJECTS = new ArrayList<>();

    public static Optional<SequenceModell> getProjectById(String projectId) {
//        UUID uuid = UUID.fromString(projectId);
        SequenceModell project = null;
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

    public static Optional<TrackModell> getTrackById(String trackId) {
        for (SequenceModell sequenceModell : MIDI_PROJECTS) {
            Optional<TrackModell> midiTrackOpt = sequenceModell.getTrackById(trackId);
            if (sequenceModell.getTrackById(trackId).isPresent()) {
                return midiTrackOpt;
            }
        }
        return Optional.empty();
    }

    public static String addProject(SequenceModell sequenceModell) {
        MIDI_PROJECTS.add(sequenceModell);
        return sequenceModell.getId();
    }

    public static void removeProject(String id) {
        MIDI_PROJECTS.removeIf(projectModell -> projectModell.getId().equals(UUID.fromString(id)));
    }

    public static Optional<SequenceModell> findMidiProjectByTrackId(String trackId){
        for (SequenceModell sequenceModell : MIDI_PROJECTS) {
            Optional<TrackModell> midiTrackOpt = sequenceModell.getTrackById(trackId);
            if (sequenceModell.getTrackById(trackId).isPresent()) {
                return Optional.of(sequenceModell);
            }
        }
        return Optional.empty();

    }
}
