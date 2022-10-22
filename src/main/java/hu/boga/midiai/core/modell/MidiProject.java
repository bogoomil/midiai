package hu.boga.midiai.core.modell;

import com.google.common.base.Objects;
import hu.boga.midiai.core.exceptions.AimidiException;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MidiProject {
    private final Sequence sequence;
    private Sequencer sequencer;
    UUID id = UUID.randomUUID();
    String name;
    private List<MidiTrack> tracks;

    public MidiProject(Sequence sequence) {
        this.sequence = sequence;
        initSequencer(sequence);
        initTracks();
    }

    private void initTracks() {
        tracks = new ArrayList<>();
        Arrays.stream(sequence.getTracks()).forEach(track -> {
            MidiTrack midiTrack = new MidiTrack(track, getResolution());
            tracks.add(midiTrack);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MidiProject that = (MidiProject) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResolution() {
        return this.sequence.getResolution();
    }

    public float getDivision() {
        return this.sequence.getDivisionType();
    }

    public long getTickLength() {
        return this.sequence.getTickLength();
    }

    public int getTicksPerMeasure() {
        return 4 * sequence.getResolution();
    }

    public int getTicksIn32nds() {
        return getTicksPerMeasure() / 32;
    }

    public float ticksPerSecond() {
        return this.sequence.getResolution() * (sequencer.getTempoInBPM() / 60);
    }

    public float tickSize() {
        return 1 / ticksPerSecond();
    }

    private void initSequencer(Sequence sequence) {
        try {
            this.sequencer = MidiSystem.getSequencer();
//            this.sequencer.addControllerEventListener(shortMessage -> System.out.println("tick position: " + sequencer.getTickPosition()), new int[]{ShortMessage.NOTE_ON});
            this.sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            throw new AimidiException("Midi sequencer unavailable: " + e.getMessage());
        }
    }

    public void play() {
        play(0);
    }

    public void play(int fromTick) {
        int toTick = (int) this.sequence.getTickLength();
        play(fromTick, toTick, 1);
    }

    public void play(int fromTick, int toTick) {
        play(fromTick, toTick, 1);
    }

    private void play(int fromTick, int toTick, int loopCount) {
        this.sequencer.stop();
        this.sequencer.setLoopCount(loopCount);
        try {
            this.sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new AimidiException("Invalid midi exception: " + e.getMessage());
        }
        this.sequencer.setTempoFactor(1f);
        this.sequencer.setTickPosition(fromTick);
        this.sequencer.setLoopStartPoint(fromTick);
        this.sequencer.setLoopEndPoint(toTick);
        this.sequencer.start();
    }


    public void playLoop(int fromTick, int toTick) {
        play(fromTick, toTick, Sequencer.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        this.sequencer.stop();
    }

    public String getId() {
        return this.id.toString();
    }

    public float getTempo() {
        List<MidiEvent> tempoEvents = getMetaEventsByType(Constants.MIDIMESSAGE_SET_TEMPO_TYPE);
        if (tempoEvents.size() == 0) {
            return 0;
        } else {
            MidiEvent event = tempoEvents.get(0);
            byte[] msg = event.getMessage().getMessage();

            int tempo = getTempoInBPM((MetaMessage) event.getMessage());

            return tempo;
        }
    }

    public void setTempo(float tempo) {
//        this.sequencer.setTempoInBPM(tempo);
        this.getMetaEventsByType(Constants.MIDIMESSAGE_SET_TEMPO_TYPE);
        this.getTracks().forEach(track -> {
            List<MidiEvent> tempoEvents = track.getMetaEventsByType(Constants.MIDIMESSAGE_SET_TEMPO_TYPE);
            track.removeEvents(tempoEvents);
        });
        tracks.get(0).addTempoEvent(0L, (long) tempo);
    }

    public void setTempoFactor(float tempoFactor) {
        this.sequencer.setTempoFactor(tempoFactor);
    }

    public void save(String filePath) {
        File file = new File(filePath);
        try {
            MidiSystem.write(sequence, 1, file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AimidiException("Saving " + filePath + " failed");
        }
    }

    public List<MidiTrack> getTracks() {
        return tracks;
    }

    public Optional<MidiTrack> getTrackById(String id) {
        UUID uuid = UUID.fromString(id);
        return tracks.stream().filter(midiTrack -> midiTrack.id.equals(uuid)).findFirst();
    }

    public MidiTrack createNewTrack() {
        Track track = sequence.createTrack();
        MidiTrack midiTrack = new MidiTrack(track, getResolution());
        tracks.add(midiTrack);
        return midiTrack;
    }

    public void removeTrackById(String trackId) {
        getTrackById(trackId).ifPresent(midiTrack -> {
            sequence.deleteTrack(midiTrack.getTrack());
            this.tracks.remove(midiTrack);
        });
    }

    public List<MidiEvent> getMetaEventsByType(int type) {
        return tracks.stream()
                .flatMap(midiTrack -> midiTrack.getMetaEventsByType(type).stream())
                .collect(Collectors.toList());
    }

    public static int getTempoInBPM(MetaMessage mm) {
        byte[] data = mm.getData();
        if (mm.getType() != 81 || data.length != 3) {
            throw new IllegalArgumentException("mm=" + mm);
        }
        int mspq = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
        int tempo = Math.round(60000001f / mspq);
        return tempo;
    }
}
