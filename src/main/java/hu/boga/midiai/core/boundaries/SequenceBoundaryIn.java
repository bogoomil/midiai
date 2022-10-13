package hu.boga.midiai.core.boundaries;

import java.io.File;

public interface SequenceBoundaryIn {
    void initNewSequence();

    void openFile(File file);
}
