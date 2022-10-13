module hu.boga.midiai {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    exports hu.boga.midiai;
    exports hu.boga.midiai.core.boundaries;
    exports hu.boga.midiai.core.interactor;
    exports hu.boga.midiai.gateway;
    exports hu.boga.midiai.gui;
    opens hu.boga.midiai to javafx.fxml;
    opens hu.boga.midiai.gui to javafx.fxml;

    requires com.google.guice;
    requires javax.inject;
    requires java.desktop;
}