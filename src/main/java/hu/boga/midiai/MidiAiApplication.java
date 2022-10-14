package hu.boga.midiai;

import hu.boga.midiai.gui.MainController;
import hu.boga.midiai.guice.GuiceModule;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.inject.Inject;
import java.io.IOException;

public class MidiAiApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("midiai");
        stage.setScene(scene);
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}