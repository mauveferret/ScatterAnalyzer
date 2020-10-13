package ru.mauveferret;

import ru.mauveferret.Distributions.Distribution;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;

public abstract class ParticleInMatterCalculator{

    public String directoryPath;

    //for console mode we don't need it
    public boolean doVizualization, getSummary;
    public int lineLength;

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

        lineLength = 76;

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
            summary.write((createHeader()+"\n").getBytes());
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

    public String createHeader(){
        String headerComment;
        String  name = " ISInCa - Ion Surface Interaction Calculator "+ Calendar.getInstance().get(Calendar.YEAR)+" ";
        name = "*".repeat((lineLength-name.length())/2)+name+"*".repeat((lineLength-name.length())/2)+"\n";
        //headerComment = "---------------"+" PARTICLE IN MATTER ANALYZER 2020 "+"---------------"+"\n";
        String author = " by mauveferret@gmail.com from \"Plasma physics\" dep., MEPhI ";
        String calc = "Calculated with "+calculatorType+" calc. ID "+modelingID+". Main input parameters:";
        String beam =projectileElements+" beam with E0 = "+projectileMaxEnergy+" eV at polar angle "+
                projectileIncidentPolarAngle+" degrees from normal";
        String beam2 = "azimuth angle "+projectileIncidentAzimuthAngle+" degrees with doze "+projectileAmount+" particles";
        String target = "target of "+targetElements;
        headerComment=name+createLine(author)+"*".repeat(lineLength)+"\n"+createLine(calc);
        headerComment+=createLine(beam)+createLine(beam2)+createLine(target)+"*".repeat(lineLength)+"\n";
        return headerComment;
    }

    public String createLine(String line){
        int spaces = (lineLength-line.length())/2;
        return "*"+" ".repeat(spaces-1)+line+" ".repeat(spaces-1)+((((lineLength-line.length())%2)==0) ? "" : " ")+"*\n";
    }

}
