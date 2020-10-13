package ru.mauveferret.Distributions;


import ru.mauveferret.ParticleInMatterCalculator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public abstract class Distribution {

    String sort;
    String pathToLog;
    String headerComment;
    String fileSeparator;
    String columnSeparatorInLog;
    ParticleInMatterCalculator calculator;
    String type;
    boolean doVisualisation;

    public Distribution(ParticleInMatterCalculator calculator, String sort) {

        columnSeparatorInLog = " ";

        this.calculator = calculator;
        this.sort = sort;
        this.doVisualisation = calculator.doVizualization;
        fileSeparator = File.separator;
        pathToLog = calculator.directoryPath+fileSeparator+"ISInCa"+File.separator+calculator.modelingID+"_"+this.getClass().getSimpleName().toUpperCase()+"_DISTR_"+
        sort+calculator.projectileMaxEnergy+"eV"+calculator.projectileIncidentPolarAngle+"deg" ;
        headerComment = calculator.createHeader();
        type = this.getClass().getSimpleName().toLowerCase();
    }


    public void check(){};
    abstract double[] getSpectrum();
    public abstract boolean logDistribution();
    public abstract boolean visualize();

    public String getType() {
        return type;
    }

    public String getSort(){return  sort;}

    //outputs

    /*
        types:
            B - back scattered
            S - sputtered/recoiled
            I - implanted
            T - transmitted
     */

}
