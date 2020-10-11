package ru.mauveferret;

import ru.mauveferret.Distributions.Distribution;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public abstract class ParticleInMatterCalculator{

    public String directoryPath;

    //for console mode we don't need it
    public boolean doVizualization;

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

    double particleCount, scattered, sputtered, implanted, transmitted, displaced;
    double energyRecoil;

    ParticleInMatterCalculator(String directoryPath, boolean doVizualization) {
        this.doVizualization = doVizualization;
        particleCount = 0;
        scattered = 0;
        sputtered = 0;
        implanted = 0;
        transmitted = 0;
        displaced = 0;
        energyRecoil = 0;

        targetElements = "no elements";
        projectileElements = "no elements";
        modelingID = "no ID";
        this.directoryPath = directoryPath;
        try{
            new File(directoryPath+File.separator+"ISInCa").mkdir();
        }catch (Exception ignored){}
    }

    abstract String  initializeVariables();

    abstract void postProcessCalculatedFiles(ArrayList<Distribution> distributions);

    public void printAndVisualizeData(ArrayList<Distribution> distributions){
        for (Distribution distr: distributions){
            if (!distr.logDistribution())  System.out.println("ERROR during logging "+distr.getType());
            if (!logAdditionalData()) System.out.println("ERROR during logging summary");
            if (doVizualization) {
              if (!distr.visualize())  System.out.println("ERROR during plotting "+distr.getType());
          }

        }
    }

    private boolean logAdditionalData(){

        String pathToLog = directoryPath + File.separator+ "ISInCa"+File.separator+modelingID+"_summary.txt";
        try {
            FileOutputStream summary = new FileOutputStream(pathToLog);
            summary.write(("Monte-Carlo model: "+calculatorType+"\n").getBytes());
            summary.write(("modeling ID: "+modelingID+"\n").getBytes());
            summary.write(("Particle count: "+particleCount+"\n").getBytes());
            summary.write(("backscattered: "+ new BigDecimal(scattered).setScale(4, RoundingMode.UP)+"\n").getBytes());
            summary.write(("sputtered: "+ new BigDecimal(sputtered).setScale(4, RoundingMode.UP)+"\n").getBytes());
            summary.write(("implanted: "+new BigDecimal(implanted).setScale(4, RoundingMode.UP)+"\n").getBytes());
            summary.write(("transmitted: "+ new BigDecimal( transmitted).setScale(4, RoundingMode.UP)+"\n").getBytes());
            summary.write(("displaced: "+ new BigDecimal( displaced).setScale(4, RoundingMode.UP)+"\n").getBytes());
            summary.write(("energy recoil: "+new BigDecimal(energyRecoil).setScale(4, RoundingMode.UP)+"\n").getBytes());
            summary.write(("ISInCa calculation time, min: "+new BigDecimal(time).setScale(4, RoundingMode.UP)).getBytes());
            summary.close();
        }
        catch (Exception e){
            e.printStackTrace();
            return  false;
        }
        return true;
    }

}
