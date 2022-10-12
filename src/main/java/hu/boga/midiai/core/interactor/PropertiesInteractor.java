package hu.boga.midiai.core.interactor;

import hu.boga.midiai.core.boundaries.PropertiesBoundaryIn;
import hu.boga.midiai.core.boundaries.PropertiesBoundaryOut;

import javax.inject.Inject;

public class PropertiesInteractor implements PropertiesBoundaryIn {

    PropertiesBoundaryOut boundaryOut;

    @Inject
    public PropertiesInteractor(PropertiesBoundaryOut boundaryOut) {
        this.boundaryOut = boundaryOut;
    }

    @Override
    public void loadProperties() {
        boundaryOut.displayProperties("zergefasz");
    }

}
