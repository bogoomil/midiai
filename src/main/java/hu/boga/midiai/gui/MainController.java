package hu.boga.midiai.gui;

import hu.boga.midiai.core.boundaries.PropertiesBoundaryIn;
import hu.boga.midiai.core.boundaries.PropertiesBoundaryOut;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.inject.Inject;

public class MainController implements PropertiesBoundaryOut {
    @FXML
    private Label propertyText;

    private PropertiesBoundaryIn boundaryIn;


    @Inject
    public MainController(PropertiesBoundaryIn boundaryIn) {
        this.boundaryIn = boundaryIn;
    }

    @FXML
    protected void onHelloButtonClick() {

    }

    @Override
    public void displayProperties(String property) {
        propertyText.setText(property);
    }

    public void newProject(ActionEvent actionEvent) {
        boundaryIn.loadProperties();
        System.out.println("boundaryId: " + this.boundaryIn);
    }

}