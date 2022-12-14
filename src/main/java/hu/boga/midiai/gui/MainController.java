package hu.boga.midiai.gui;

import hu.boga.midiai.core.util.Constants;
import hu.boga.midiai.guice.GuiceModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    public TabPane mainTab;

    @Inject
    public MainController() {
    }

    public void newProject(ActionEvent actionEvent) throws IOException {
        createNewTab();
    }

    public void openFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            openFile(file);
        }
    }

    private void openFile(File file) throws IOException {
        getSequenceTabController().initSequence(file);
    }

    private void createNewTab() throws IOException {
        getSequenceTabController().initSequence();
    }

    private SequenceEditorPanelController getSequenceTabController() throws IOException {
        FXMLLoader loader = new FXMLLoader(SequenceEditorPanelController.class.getResource("sequence-editor-panel.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        BorderPane sequenceEditorPanel =  loader.load();
        SequenceEditorPanelController controller = loader.getController();
        mainTab.getTabs().add(new Tab(Constants.DEFAULT_NAME, sequenceEditorPanel));
        return controller;
    }
}