package hu.boga.midiai.gui;

import hu.boga.midiai.MidiAiApplication;
import hu.boga.midiai.gui.controls.InstrumentCombo;
import hu.boga.midiai.gui.events.ChannelMappingChangeEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.Map;

public class ChannelToInstrumentMappingPanel {
    public static final int DEFAULT_CHANNEL_COUNT = 16;
    @FXML
    VBox vBox;

    InstrumentCombo[] instrumentCombos = new InstrumentCombo[DEFAULT_CHANNEL_COUNT];

    public void initialize(){
        for (int i = 0; i < DEFAULT_CHANNEL_COUNT; i++){
            instrumentCombos[i] = new InstrumentCombo();
            vBox.getChildren().add(instrumentCombos[i]);
        }
    }

    public void setChannelMapping(Map<Integer, Integer> mapping){
        mapping.forEach((channel, instrument) -> instrumentCombos[channel].selectInstrument(instrument));
    }

    public void setProjectId(String projectId) {
        for (int i = 0; i < instrumentCombos.length; i++){
            int finalI = i;
            instrumentCombos[i].addEventHandler(ActionEvent.ACTION, event -> MidiAiApplication.EVENT_BUS.post(new ChannelMappingChangeEvent(projectId, finalI, instrumentCombos[finalI].getSelectedProgram())));
        }
    }
}
