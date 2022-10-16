module hu.boga.midiai {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.guice;
    requires javax.inject;
    requires java.desktop;
    requires com.google.common;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    exports hu.boga.midiai;
    exports hu.boga.midiai.core.boundaries;
    exports hu.boga.midiai.core.boundaries.dtos;
    exports hu.boga.midiai.core.interactor;
    exports hu.boga.midiai.core.midigateway;
    exports hu.boga.midiai.core.modell;
    exports hu.boga.midiai.core.util;

    exports hu.boga.midiai.gui;
    exports hu.boga.midiai.gui.controls;

    opens hu.boga.midiai to javafx.fxml;
    opens hu.boga.midiai.gui to javafx.fxml, com.google.common;

}