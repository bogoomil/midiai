package hu.boga.midiai.gui.controls;

import hu.boga.midiai.core.musictheory.enums.NoteName;
import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.List;

public class NoteNameCombo extends ComboBox<NoteName> {
    static List<NoteName> tones;
    static {
        tones = Arrays.asList(NoteName.values());
    }

    public NoteNameCombo() {
        getItems().addAll(tones);
    }

    public NoteName getSelectedNoteName(){
        return this.getSelectionModel().getSelectedItem();
    }

}
