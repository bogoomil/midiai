package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.MainBoundaryIn;
import hu.boga.midiai.core.boundaries.MainBoundaryOut;

import javax.inject.Inject;

public class MainInteractor implements MainBoundaryIn {

    private  final MainBoundaryOut boundaryOut;

    @Inject
    public MainInteractor(MainBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

}

