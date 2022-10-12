package hu.boga.midiai;

import com.google.inject.Guice;
import com.google.inject.Injector;
import hu.boga.midiai.gui.MainController;
import hu.boga.midiai.guice.GuiceModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Injector injector = Guice.createInjector(new GuiceModule());

        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return injector.getInstance(param);
            }
        });
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("midiai");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}