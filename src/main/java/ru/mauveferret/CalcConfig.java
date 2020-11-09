package ru.mauveferret;

import java.util.ArrayList;

public class CalcConfig {

    // for multiCalcMode
    ArrayList<String> dirs;

    //for singleCalcMode
    String dir;

    //flags
    boolean isMultiCalc = false;
    boolean vizualise = false;
    boolean getSummary = true;

    int stringCountPerCicle = 1000;

    public CalcConfig(ArrayList<String> dirs,  int stringCountPerCicle, boolean getSummary, boolean vizualise) {
        this.dirs = dirs;
        isMultiCalc = true;
        this.vizualise = vizualise;
        this.getSummary = getSummary;
        this.stringCountPerCicle = stringCountPerCicle;
    }

    public CalcConfig(String dir, int stringCountPerCicle, boolean vizualise, boolean getSummary) {
        this.dir = dir;
        isMultiCalc = false;
        this.vizualise = vizualise;
        this.getSummary = getSummary;
        this.stringCountPerCicle = stringCountPerCicle;
    }

    public CalcConfig(ArrayList<String> dirs,  boolean getSummary, boolean vizualise) {
        this.dirs = dirs;
        isMultiCalc = true;
        this.vizualise = vizualise;
        this.getSummary = getSummary;
    }

    public CalcConfig(String dir,  boolean vizualise, boolean getSummary) {
        this.dir = dir;
        isMultiCalc = false;
        this.vizualise = vizualise;
        this.getSummary = getSummary;
    }
    public CalcConfig(ArrayList<String> dirs) {
        this.dirs = dirs;
    }

    public CalcConfig(String dir) {
        this.dir = dir;
    }



}


