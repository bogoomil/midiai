package hu.boga.midiai.gui;

import com.google.common.eventbus.EventBus;
import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.boundaries.dtos.SequenceDto;
import hu.boga.midiai.core.exceptions.MidiAiException;
import hu.boga.midiai.core.musictheory.enums.NoteName;
import hu.boga.midiai.core.musictheory.enums.Tone;
import hu.boga.midiai.gui.controls.TempoSlider;
import hu.boga.midiai.gui.trackeditor.TrackEditorPanelController;
import hu.boga.midiai.gui.trackeditor.events.ModeChangedEvent;
import hu.boga.midiai.gui.trackeditor.events.RootChangedEvent;
import hu.boga.midiai.guice.GuiceModule;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class SequenceEditorPanelController implements SequenceBoundaryOut {
    private static final String DEFAULT_NAME = "new_project.mid";
    private final SequenceBoundaryIn boundaryIn;

    public Label division;
    public Label resolution;
    public Label tickLength;
    public Label ticksPerMeasure;
    public Label ticksIn32nds;
    public Label ticksPerSecond;
    public Label tickSize;
    public TempoSlider tempoSlider;
    public Label tempoLabel;

    public final EventBus eventBus = new EventBus();
    public Menu rootMenu;
    public Menu modeMenu;

    @FXML
    private TextField tfFilename;
    @FXML
    private Accordion accordion;

    private String projectId;

    @Inject
    public SequenceEditorPanelController(SequenceBoundaryIn boundaryInProvider) {
        this.boundaryIn = boundaryInProvider;
    }

    public void initialize() {
        tempoSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    initTemposSettings(newValue);
                }
        );
        rootMenu.getItems().addAll(createRootMenuItems());
        modeMenu.getItems().addAll(createToneMenuItems());
    }

    private void initTemposSettings(Number newValue) {
        tempoLabel.setText("Tempo: " + newValue.intValue());
        if(projectId != null){
            boundaryIn.setTempo(projectId, newValue.intValue());
        }
    }

    public void saveSequence(ActionEvent actionEvent) {
        String path = new FileChooser().showSaveDialog(null).getAbsolutePath();
        this.boundaryIn.saveSequence(projectId, path);
    }

    public void onPlayCurrentSec(ActionEvent actionEvent) {
        boundaryIn.playSequence(projectId);
        //this.boundaryIn.playLoop(projectId, 960, 1920);
    }

    public void stopPlayback(ActionEvent actionEvent) {
        this.boundaryIn.stopPlayBack(projectId);
    }

    @Override
    public void displaySequence(SequenceDto sequenceDto) {
        this.tfFilename.setText(sequenceDto.name);
        this.division.setText("division: " + sequenceDto.division + "");
        this.resolution.setText("resolution: " + sequenceDto.resolution + "");
        this.tickLength.setText("tick length: " + sequenceDto.tickLength + "");
        this.ticksPerMeasure.setText("ticks / measure: " + sequenceDto.ticksPerMeasure + " (4 * resolution)");
        this.ticksIn32nds.setText("ticks in 32nds: " + sequenceDto.ticksIn32nds + " (ticks per measure / 32)");
        this.ticksPerSecond.setText("ticks / sec: " + sequenceDto.ticksPerSecond + " (resolution * (tempo / 60))");
        this.tickSize.setText("tick size: " + sequenceDto.tickSize + " (1 / ticks per second)");
        this.tempoSlider.adjustValue(sequenceDto.tempo);
        this.tempoLabel.setText("Tempo: " + sequenceDto.tempo);
        this.projectId = sequenceDto.id;

        initChildren(sequenceDto);

    }

    private void initChildren(SequenceDto sequenceDto) {
        accordion.getPanes().remove(1, accordion.getPanes().size());
        sequenceDto.tracks.forEach(trackId -> {
            try {
                addTrackPanel(trackId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void initSequence() {
        this.boundaryIn.initNewSequence();
    }

    public void initSequence(File file) {
        this.boundaryIn.openFile(file);
    }

    private void addTrackPanel(String trackId) throws IOException {
        FXMLLoader loader = new FXMLLoader(TrackEditorPanelController.class.getResource("track-editor-panel.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        TitledPane trackEditor = loader.load();

        TrackEditorPanelController trackEditorPanelController = loader.getController();
        trackEditorPanelController.setTrackId(trackId);
        trackEditorPanelController.setParent(this);

        accordion.getPanes().add(trackEditor);
    }

    public void onNewTrackButtonClicked(ActionEvent actionEvent) {
        this.boundaryIn.addTrack(projectId);
    }

    @Override
    public void addTrack(String id) {
        try {
            addTrackPanel(id);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MidiAiException("Adding new track failed: " + e.getMessage());
        }
    }

    public void onTrackDeletedEvent(String trackId) {
        boundaryIn.removeTrack(trackId);
    }

    private RadioMenuItem[] createToneMenuItems() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[Tone.values().length];
        for (int i = 0; i < Tone.values().length; i++) {
            Tone currTone = Tone.values()[i];
            RadioMenuItem menuItem = new RadioMenuItem(currTone.name());
            menuItem.setToggleGroup(toggleGroup);
            int finalI = i;
            menuItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    eventBus.post(new ModeChangedEvent(currTone));

                }
            });
            items[i] = menuItem;
        }
        items[0].setSelected(true);
        return items;

    }

    private RadioMenuItem[] createRootMenuItems() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] items = new RadioMenuItem[NoteName.values().length];
        for (int i = 0; i < NoteName.values().length; i++) {
            NoteName currRoot = NoteName.values()[i];
            RadioMenuItem menuItem = new RadioMenuItem(currRoot.name());
            menuItem.setToggleGroup(toggleGroup);
            int finalI = i;
            menuItem.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    eventBus.post(new RootChangedEvent(currRoot));
                }
            });
            items[i] = menuItem;
        }
        items[0].setSelected(true);
        return items;

    }


}
