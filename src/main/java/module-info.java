module hu.boga.midiai {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    exports hu.boga.midiai;
    exports hu.boga.midiai.core.boundaries;
    exports hu.boga.midiai.core.interactor;
    exports hu.boga.midiai.core.modell;
    exports hu.boga.midiai.gateway;
    exports hu.boga.midiai.gui;
    exports hu.boga.midiai.core.midigateway;
    opens hu.boga.midiai to javafx.fxml;
    opens hu.boga.midiai.gui to javafx.fxml, com.google.common;
    exports hu.boga.midiai.core.boundaries.dtos;

    requires com.google.guice;
    requires javax.inject;
    requires java.desktop;
    requires com.google.common;
}