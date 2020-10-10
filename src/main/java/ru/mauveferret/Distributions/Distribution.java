package ru.mauveferret.Distributions;


import ru.mauveferret.ParticleInMatterCalculator;

import java.io.File;
import java.io.FileOutputStream;

public abstract class Distribution {

    String sort;
    String pathToLog;
    String headerComment;
    String fileSeparator;

    ParticleInMatterCalculator calculator;
    String type;
    boolean doVisualisation;

    public Distribution(ParticleInMatterCalculator calculator, String sort) {
        this.calculator = calculator;
        this.sort = sort;
        this.doVisualisation = calculator.doVizualization;
        fileSeparator = File.separator;
        pathToLog = calculator.directoryPath+fileSeparator+"ISInCa"+File.separator+this.getClass().getSimpleName().toUpperCase()+"_DISTR_"+
        sort+calculator.projectileMaxEnergy+"eV"+calculator.projectileIncidentPolarAngle+"deg" ;
        //FIXME version 2020 to real year
        headerComment = "|--------ISInCa - Ion Surface Interaction Calculator 2020-----|"+"\n";
        //headerComment = "---------------"+" PARTICLE IN MATTER ANALYZER 2020 "+"---------------"+"\n";
        headerComment+= "|  "+"by mauveferret@gmail.com from plasma physics Dep., MEPhI"+"   |"+"\n";
        headerComment+="| Calculated with "+calculator.calculatorType+" calc. ID "+calculator.modelingID+"               |"+"\n";
        headerComment+="|                        Main input parameters                 |\n" +
                "| beam of "+calculator.projectileElements+
                " with E0 = "+calculator.projectileMaxEnergy+"eV at polar angle "+
                calculator.projectileIncidentPolarAngle+" degrees     |\n| from normal, azimuth angle "+calculator.projectileIncidentAzimuthAngle+
                " with doze "+calculator.projectileAmount+" particles   |"+"\n";
        headerComment+="|                      target of "+calculator.targetElements+"                             |"+"\n";
        headerComment+="|----------------------------------------------------------------------|"+"\n";
        type = this.getClass().getSimpleName().toLowerCase();
        //System.out.println(type+"      CHECK");
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
