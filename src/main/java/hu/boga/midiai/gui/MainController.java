package hu.boga.midiai.gui;

import hu.boga.midiai.core.boundaries.PropertiesBoundaryIn;
import hu.boga.midiai.core.boundaries.PropertiesBoundaryOut;
import hu.boga.midiai.guice.GuiceModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Callback;

import javax.inject.Inject;
import java.io.IOException;

public class MainController implements PropertiesBoundaryOut {

    @FXML
    public TabPane mainTab;

    private final PropertiesBoundaryIn boundaryIn;

    @Inject
    public MainController(PropertiesBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    @Override
    public void displayProperties(String property) {
    }

    public void newProject(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(SequenceTabController.class.getResource("sequence-editor-tab.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        Tab sequenceEditorTab =  loader.load();

        mainTab.getTabs().add(sequenceEditorTab);
    }

}