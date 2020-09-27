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

    public Distribution(ParticleInMatterCalculator calculator, String sort) {
        this.calculator = calculator;
        this.sort = sort;
        fileSeparator = File.separator;
        pathToLog = calculator.directoryPath+fileSeparator+"ISInCa"+File.separator+calculator.modelingID+"_"+this.getClass().getSimpleName().toUpperCase()+" DISTRIBUTION_"+
        sort ;
        headerComment = "---------------"+" PARTICLE IN MATTER ANALYZER 2020 "+"---------------"+"\n";
        headerComment+= "|  "+"by Mauveferret@gmail.com from Plasma Physics Dep., MEPhI"+"    |"+"\n";
        headerComment+="| Calculated with "+calculator.calculatorType+" calc. ID "+calculator.modelingID+"|"+"\n";
        headerComment+="| Main input parameters: beam of "+calculator.projectileElements+
                " with E0 = "+calculator.projectileMaxEnergy+" at polar angle "+
                calculator.projectileIncidentPolarAngle+" from normal, azimuth angle "+calculator.projectileIncidentAzimuthAngle+
                " with doze "+calculator.projectileAmount+"particles|"+"\n";
        headerComment+="|                      target of "+calculator.targetElements+"                      |"+"\n";
        headerComment+="|----------------------------------------------------------------------|"+"\n";
        type = this.getClass().getSimpleName().toLowerCase();
        //System.out.println(type+"      CHECK");
    }

    public void check(){};
    abstract int[] getSpectrum();
    public abstract boolean logDistribution();
    public abstract boolean visualize();

    public String getType() {
        return type;
    }


    //outputs

    /*
        types:
            B - back scattered
            S - sputtered/recoiled
            I - implanted
            T - transmitted
     */

}
