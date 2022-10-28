package hu.boga.midiai.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import hu.boga.midiai.core.sequence.gateway.SequenceGateway;
import hu.boga.midiai.core.sequence.interactor.SequenceInteractor;
import hu.boga.midiai.core.tracks.interactor.TrackInteractor;
import hu.boga.midiai.core.sequence.boundary.SequenceBoundaryIn;
import hu.boga.midiai.core.sequence.boundary.SequenceBoundaryOut;
import hu.boga.midiai.core.tracks.boundary.TrackBoundaryIn;
import hu.boga.midiai.core.tracks.boundary.TrackBoundaryOut;
import hu.boga.midiai.gui.SequenceEditorPanelController;
import hu.boga.midiai.gui.trackeditor.TrackEditorPanelController;
import hu.boga.midiai.midigateway.SequenceGatewayImpl;

public class GuiceModule extends AbstractModule {

    public static final Injector INJECTOR = Guice.createInjector(new GuiceModule());

    @Override
    protected void configure() {

        bind(SequenceGateway.class).to(SequenceGatewayImpl.class);

        bind(SequenceBoundaryIn.class).to(SequenceInteractor.class);
        bind(SequenceBoundaryOut.class).to(SequenceEditorPanelController.class);

        bind(TrackBoundaryIn.class).to(TrackInteractor.class);
        bind(TrackBoundaryOut.class).to(TrackEditorPanelController.class);

    }

}
