package hu.boga.midiai.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import hu.boga.midiai.core.boundaries.*;
import hu.boga.midiai.core.interactor.SequenceInteractor;
import hu.boga.midiai.core.interactor.TrackInteractor;
import hu.boga.midiai.gui.MainController;
import hu.boga.midiai.core.interactor.MainInteractor;
import hu.boga.midiai.gui.SequenceEditorPanelController;
import hu.boga.midiai.gui.TrackEditorPanelController;

public class GuiceModule extends AbstractModule {

    public static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    @Override
    protected void configure() {

        bind(MainBoundaryIn.class).to(MainInteractor.class);
        bind(MainBoundaryOut.class).to(MainController.class);

        bind(SequenceBoundaryIn.class).to(SequenceInteractor.class);
        bind(SequenceBoundaryOut.class).to(SequenceEditorPanelController.class);

        bind(TrackBoundaryIn.class).to(TrackInteractor.class);
        bind(TrackBoundaryOut.class).to(TrackEditorPanelController.class);

    }

}
