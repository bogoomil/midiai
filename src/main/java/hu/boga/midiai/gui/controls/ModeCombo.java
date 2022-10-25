package hu.boga.midiai.gui.controls;

import hu.boga.midiai.core.musictheory.enums.Tone;
import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.List;

public class ModeCombo extends ComboBox<Tone> {
    static List<Tone> tones;
    static {
        tones = Arrays.asList(Tone.values());
    }

    public ModeCombo() {
        getItems().addAll(tones);
    }

    public Tone getSelectedTone(){
        return this.getSelectionModel().getSelectedItem();
    }

}
