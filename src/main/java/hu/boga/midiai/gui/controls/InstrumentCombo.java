package hu.boga.midiai.gui.controls;

import javafx.scene.control.ComboBox;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class InstrumentCombo extends ComboBox<Instrument> {

    static List<Instrument> instruments;
    static {
        try {
            instruments = Arrays.asList(MidiSystem.getSynthesizer().getAvailableInstruments());
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public InstrumentCombo() {
        super();
        this.getItems().addAll(instruments);
    }

    public static Optional<Instrument> getInstrumentByData(int instrumentsProgram) {
       return instruments.stream().filter(ins -> ins.getPatch().getProgram() == instrumentsProgram).findFirst();
    }

    public Optional<Integer> getInstrumentIndex(int instrumentProgram){
        Optional<Instrument> opt = getInstrumentByData(instrumentProgram);
        return opt.map(instrument -> this.getItems().indexOf(instrument));
    }

    public void selectInstrument(int instrumentsProgram){
        getInstrumentIndex(instrumentsProgram).ifPresent(indx -> getSelectionModel().select(indx));
    }
}
