package ru.mauveferret;

import ru.mauveferret.Distributions.Dependence;

import java.util.ArrayList;

public class CalculationSummator extends ParticleInMatterCalculator {

    /* used to summary data from several single calculations with common targets
    in order to create model for multi -energy -angle -mass incident beam
     */

    private ArrayList<ParticleInMatterCalculator> calculators;

    public CalculationSummator( String directoryPath, ArrayList<ParticleInMatterCalculator> calculators) {
        super(directoryPath, false);
    }

    @Override
    String initializeModelParameters() {
        return null;
    }

    @Override
    void postProcessCalculatedFiles(ArrayList<Dependence> distributions) {

    }
}
