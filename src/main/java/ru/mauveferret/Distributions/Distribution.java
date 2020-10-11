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
    int  lineLength;

    ParticleInMatterCalculator calculator;
    String type;
    boolean doVisualisation;

    public Distribution(ParticleInMatterCalculator calculator, String sort) {

        columnSeparatorInLog = " ";

        this.calculator = calculator;
        this.sort = sort;
        this.doVisualisation = calculator.doVizualization;
        fileSeparator = File.separator;
        pathToLog = calculator.directoryPath+fileSeparator+"ISInCa"+File.separator+this.getClass().getSimpleName().toUpperCase()+"_DISTR_"+
        sort+calculator.projectileMaxEnergy+"eV"+calculator.projectileIncidentPolarAngle+"deg" ;
        createHeader();
        type = this.getClass().getSimpleName().toLowerCase();
    }

    private void createHeader(){
        lineLength = 76;
        String  name = " ISInCa - Ion Surface Interaction Calculator "+ Calendar.getInstance().get(Calendar.YEAR)+" ";
        name = "*".repeat((lineLength-name.length())/2)+name+"*".repeat((lineLength-name.length())/2)+"\n";
        //headerComment = "---------------"+" PARTICLE IN MATTER ANALYZER 2020 "+"---------------"+"\n";
        String author = " by mauveferret@gmail.com from \"Plasma physics\" dep., MEPhI ";
        String calc = "Calculated with "+calculator.calculatorType+" calc. ID "+calculator.modelingID+". Main input parameters:";
        String beam =calculator.projectileElements+" beam with E0 = "+calculator.projectileMaxEnergy+" eV at polar angle "+
                calculator.projectileIncidentPolarAngle+" degrees from normal";
        String beam2 = "azimuth angle "+calculator.projectileIncidentAzimuthAngle+" degrees with doze "+calculator.projectileAmount+" particles";
        String target = "target of "+calculator.targetElements;
        headerComment=name+createLine(author)+"*".repeat(lineLength)+"\n"+createLine(calc);
        headerComment+=createLine(beam)+createLine(beam2)+createLine(target)+"*".repeat(lineLength)+"\n";
    }

    public String createLine(String line){
        int spaces = (lineLength-line.length())/2;
        return "*"+" ".repeat(spaces-1)+line+" ".repeat(spaces-1)+((((lineLength-line.length())%2)==0) ? "" : " ")+"*\n";
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
