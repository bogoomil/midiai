package hu.boga.midiai.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import hu.boga.midiai.core.boundaries.SequenceBoundaryIn;
import hu.boga.midiai.core.boundaries.SequenceBoundaryOut;
import hu.boga.midiai.core.interactor.SequenceInteractor;
import hu.boga.midiai.gui.MainController;
import hu.boga.midiai.core.boundaries.PropertiesBoundaryIn;
import hu.boga.midiai.core.boundaries.PropertiesBoundaryOut;
import hu.boga.midiai.core.interactor.PropertiesInteractor;
import hu.boga.midiai.gui.SequenceTabController;

public class GuiceModule extends AbstractModule {

    public static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    @Override
    protected void configure() {

        bind(PropertiesBoundaryIn.class).to(PropertiesInteractor.class);
        bind(PropertiesBoundaryOut.class).to(MainController.class);

        bind(SequenceBoundaryIn.class).to(SequenceInteractor.class);
        bind(SequenceBoundaryOut.class).to(SequenceTabController.class);

    }

}