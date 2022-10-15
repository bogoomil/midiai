package hu.boga.midiai.gui;

import hu.boga.midiai.core.boundaries.MainBoundaryIn;
import hu.boga.midiai.core.boundaries.MainBoundaryOut;
import hu.boga.midiai.guice.GuiceModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class MainController implements MainBoundaryOut {

    @FXML
    public TabPane mainTab;

    @Inject
    public MainController(MainBoundaryIn boundaryIn) {
    }

    @Override
    public void displayProperties(String property) {
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

    private SequenceTabController getSequenceTabController() throws IOException {
        FXMLLoader loader = new FXMLLoader(SequenceTabController.class.getResource("sequence-editor-tab.fxml"));
        loader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        Tab sequenceEditorTab =  loader.load();
        SequenceTabController sequenceTabController = loader.getController();
        mainTab.getTabs().add(sequenceEditorTab);
        return sequenceTabController;
    }
}