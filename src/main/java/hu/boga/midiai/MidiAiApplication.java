package hu.boga.midiai;

import com.google.common.eventbus.EventBus;
import hu.boga.midiai.gui.MainController;
import hu.boga.midiai.guice.GuiceModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

import static java.lang.System.exit;

public class MidiAiApplication extends Application {

    public static final EventBus EVENT_BUS = new EventBus();

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(GuiceModule.INJECTOR::getInstance);
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("midiai");
        stage.setScene(scene);
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> exit(0));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}