package ru.mauveferret.Distributions;

import ru.mauveferret.ParticleInMatterCalculator;
import ru.mauveferret.ScatterColorMap;

import java.awt.*;
import java.io.FileOutputStream;

public class AngleMap extends Distribution{

    private final double dPhi;
    private final double dTheta;

    private double angleMap[][];

    public AngleMap(double dPhi, double dTheta, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.dPhi = dPhi;
        this.dTheta = dTheta;
        angleMap = new double[(int) Math.ceil(360/dPhi)+1][(int) Math.ceil(90/dTheta)+1];
        pathToLog+="_dphi "+dPhi+"_dTheta"+dTheta+".txt";
        String addheaderComment = " dPhi "+dPhi+" degrees dTheta "+dTheta+" degrees ";
        headerComment +=calculator.createLine(addheaderComment)+"*".repeat(calculator.lineLength)+"\n";
    }

    public  void check (PolarAngles angles, String someSort)
    {
        //only for backscattered and sputtered!
        if (sort.contains(someSort))
        {
            angleMap[(int) (Math.round(angles.getAzimuth() / dPhi))][(int) Math.round( angles.getPolar()/ dTheta)]++;
            //FIXME ITS  A TRAP!!!
            angleMap[(int) (Math.round((360-angles.getAzimuth())/ dPhi))][(int) Math.round( angles.getPolar()/ dTheta)]++;

        }
    }

    public double[]getSpectrum() {
        return null;
    }

    public double[][] getMap(){
        return  angleMap;
    }

    @Override
    public boolean logDistribution() {

        //FIXME ITS A TRAP!!!

        for (int i=0; i<(int) Math.ceil(90/dTheta)+1; i++){
            angleMap[0][i] = angleMap[(int) Math.round((360-dPhi)/dPhi)][i];
        }

        // from probability distr. to angle

        for  (int i = 0; i <=(int) Math.round(360 / dPhi); i++){
            for (int j = 1; j <= (int) Math.round(90 / dTheta); j++){
                angleMap[i][j] = angleMap[i][j]/(dPhi*Math.sin(Math.toRadians(Math.abs(j*dTheta))));
            }
        }

        try {
            FileOutputStream surfaceWriter = new FileOutputStream(pathToLog);
            String stroka ="Phi";
            surfaceWriter.write(headerComment.getBytes());
            for (int i = 0; i <=(int) Math.round(90 / dTheta); i++)
            {
                stroka=stroka+columnSeparatorInLog+(int) (i*dTheta);
            }
            stroka=stroka+"\n";
            surfaceWriter.write(stroka.getBytes());

            for (int i = 0; i <=(int) Math.round(360 / dPhi); i++) {
                stroka=(int) (i*dPhi)+columnSeparatorInLog;
                for (int j = 0; j <= (int) Math.round(90 / dTheta); j++) {
                    if (i<(int) Math.round(360 / dTheta)) stroka = stroka + angleMap[i][j] + columnSeparatorInLog;
                    else  stroka=stroka+angleMap[i-1][j]+columnSeparatorInLog;
                }
                stroka = stroka + "\n";
                surfaceWriter.write(stroka.getBytes());
            }
            surfaceWriter.close();
            return  true;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return  false;
        }
    }

    @Override
    public boolean visualize() {
        if (!sort.equals("") && doVisualisation) EventQueue.invokeLater(() -> new ScatterColorMap("ISInCa",  angleMap, dPhi, dTheta));
        return  true;
    }


}

