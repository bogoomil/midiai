module hu.boga.midiai {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.guice;
    requires javax.inject;
    requires java.desktop;
    requires com.google.common;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.slf4j;

    exports hu.boga.midiai;
    exports hu.boga.midiai.core.sequence.interactor;
    exports hu.boga.midiai.core.sequence.modell;
    exports hu.boga.midiai.core.sequence.boundary;
    exports hu.boga.midiai.core.tracks.interactor;
    exports hu.boga.midiai.core.tracks.modell;
    exports hu.boga.midiai.core.tracks.boundary;
    exports hu.boga.midiai.core.util;

    exports hu.boga.midiai.midigateway;

    exports hu.boga.midiai.gui;
    exports hu.boga.midiai.gui.controls;

    opens hu.boga.midiai to javafx.fxml;
    opens hu.boga.midiai.gui to javafx.fxml, com.google.common;
    exports hu.boga.midiai.gui.trackeditor;
    opens hu.boga.midiai.gui.trackeditor to com.google.common, javafx.fxml;
    exports hu.boga.midiai.gui.trackeditor.events;
    opens hu.boga.midiai.gui.trackeditor.events to com.google.common, javafx.fxml;


}