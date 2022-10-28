package hu.boga.midiai.core.sequence.interactor;

import hu.boga.midiai.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.midiai.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.midiai.core.sequence.gateway.SequenceGateway;
import hu.boga.midiai.core.sequence.modell.SequenceModell;
import hu.boga.midiai.core.tracks.modell.MidiTrack;

import javax.inject.Inject;

public class SequenceInteractor implements SequenceBoundaryIn {
    private final SequenceBoundaryOut boundaryOut;
    private final SequenceGateway gateway;

    @Inject
    public SequenceInteractor(SequenceBoundaryOut boundaryOut, SequenceGateway gateway) {
        this.boundaryOut = boundaryOut;
        this.gateway = gateway;
    }

//    @Override
//    public void playSequence(String id, int fromTick, int toTick) {
//        InMemoryStore.getProjectById(id).ifPresent(projectModell -> projectModell.play(fromTick, toTick));
//    }
//    @Override
//    public void playSequence(String id, int fromTick) {
//        InMemoryStore.getProjectById(id).ifPresent(projectModell -> projectModell.play(fromTick));
//    }
//    @Override
//    public void playLoop(String projectId, int fromTick, int toTick) {
//        InMemoryStore.getProjectById(projectId).ifPresent(projectModell -> projectModell.playLoop(fromTick, toTick));
//    }

    @Override
    public void play(String id) {
        this.gateway.play(id);
    }

    @Override
    public void stop(String id) {
        this.gateway.stop(id);

    }

    @Override
    public void save(String projectId, String filePath) {
        this.gateway.save(projectId, filePath);
    }

    @Override
    public void addTrack(String projectId) {
        MidiTrack midiTrack = this.gateway.addTrack(projectId);
        boundaryOut.addTrack(midiTrack.getId());
    }

    @Override
    public void removeTrack(String trackId) {
        SequenceModell modell = this.gateway.deleteTrack(trackId);
        boundaryOut.displaySequence(new ModellToDtoConverter(modell).convert());
    }

    @Override
    public void setTempo(String projectId, int tempo) {
        this.gateway.setTempo(projectId, tempo);
    }

    @Override
    public void create() {
        SequenceModell modell = this.gateway.create();
        this.boundaryOut.displaySequence(new ModellToDtoConverter(modell).convert());
    }

    @Override
    public void open(String path) {
        SequenceModell modell = this.gateway.open(path);
        this.boundaryOut.displaySequence(new ModellToDtoConverter(modell).convert());
    }
}
