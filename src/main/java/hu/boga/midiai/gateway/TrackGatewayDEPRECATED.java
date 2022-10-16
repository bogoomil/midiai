package hu.boga.midiai.gateway;


import hu.boga.midiai.core.exceptions.AimidiException;
import hu.boga.midiai.core.modell.ProgramChangeEvent;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrackGatewayDEPRECATED {
    private Track track;
    int resolution;

    public TrackGatewayDEPRECATED(Track track, int resolution) {
        this.track = track;
        this.resolution = resolution;
    }


    public void addNoteToTrack(int tick, int channel, int lengthInTicks, int midiCode, int velocity) throws InvalidMidiDataException {
        int endInTick = tick + lengthInTicks;
        addShortMessage(tick, ShortMessage.NOTE_ON, channel, midiCode, velocity);
        addShortMessage(endInTick, ShortMessage.NOTE_OFF, channel, midiCode, velocity);
    }

    public Optional<ShortMessage> getNoteOnsChannel() {
        List<ShortMessage> noteOns = getShortMessagesByCommand(ShortMessage.NOTE_ON);
        if(noteOns.size() >= 1) return Optional.of(noteOns.get(0));
        return Optional.empty();
    }

    protected void addShortMessage(int tick, int command, int channel, int data1, int data2) throws InvalidMidiDataException {
        ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(command, channel, data1, data2);
        MidiEvent event = new MidiEvent(shortMessage, tick);
        track.add(event);
    }

    protected List<ShortMessage> getShortMessagesByCommand(int command) {
        List<ShortMessage> retVal = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage) {
                ShortMessage msg = (ShortMessage) event.getMessage();
                if (msg.getCommand() == command) {
                    retVal.add(msg);
                }
            }
        }
        return retVal;
    }

    protected List<MidiEvent> getEventsByCommand(int command) {
        List<MidiEvent> retVal = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            if (event.getMessage() instanceof ShortMessage) {
                ShortMessage msg = (ShortMessage) event.getMessage();
                if (msg.getCommand() == command) {
                    retVal.add(event);
                }
            }
        }
        return retVal;
    }

    public List<ProgramChangeEvent> getProgramChangeEvents() {
        List<ProgramChangeEvent> retVal = new ArrayList<>(0);
        this.getEventsByCommand(ShortMessage.PROGRAM_CHANGE).forEach(event -> {
            ShortMessage shortMessage = (ShortMessage) event.getMessage();
            ProgramChangeEvent programChangeEvent = new ProgramChangeEvent(shortMessage.getChannel(), shortMessage.getData1(), (int) event.getTick());
            retVal.add(programChangeEvent);
        });
        return retVal;
    }

    private void removeEventsByCommand(int command){
        this.getEventsByCommand(command).forEach(midiEvent -> this.track.remove(midiEvent));
    }

    public void addProgramChangeEvent(int channel, int program, int tick) {
        removeEventsByCommand(ShortMessage.PROGRAM_CHANGE);
        try {
            System.out.println("channel: " + channel + ", program: " + program);
            addShortMessage(tick, ShortMessage.PROGRAM_CHANGE, channel, program, 0);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            throw new AimidiException("update programchange event failed");
        }
    }
}
