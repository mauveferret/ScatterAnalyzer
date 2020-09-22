package ru.mauveferret;

import ru.mauveferret.Distributions.Distribution;

import java.util.ArrayList;

public abstract class ParticleInMatterCalculator {

    public String directoryPath;

    //like SC100432, H_W and etc.
    public String modelingID;

    //like SCATTER, TRIM, SDTrimSP
    public String calculatorType;

    //how mich lines per FileRead can be processed
    public int stringCountPerCycle = 2000;
    double time;


    //primary beam

    public double projectileMaxEnergy;
    public double projectileIncidentPolarAngle;
    public double projectileIncidentAzimuthAngle;

    public int projectileAmount;
    public String projectileElements;

    // target

    public String targetElements;

    //some scattering variables

    int particleCount, scattered, sputtered, implanted, transmitted;
    double energyRecoil;

    ParticleInMatterCalculator(String directoryPath) {
        particleCount = 0;
        scattered = 0;
        sputtered = 0;
        implanted = 0;
        transmitted = 0;
        energyRecoil = 0;

        targetElements = "no elements";
        projectileElements = "no elements";
        modelingID = "no ID";
        this.directoryPath = directoryPath;
    }

    abstract String  initializeVariables();
    abstract void postProcessCalculatedFiles(ArrayList<Distribution> distributions);

    public void printAndVisualizeData(ArrayList<Distribution> distributions){
        for (Distribution distr: distributions){
            if (!distr.logDistribution())  System.out.println("ERROR during logging "+distr.getType());
            if (!distr.visualize())  System.out.println("ERROR during plotting "+distr.getType());

        }
    }

}
