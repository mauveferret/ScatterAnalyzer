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
        pathToLog+="_dphi "+dPhi+"_dTheta"+dTheta+"_time "+ ((int ) (Math.random()*100))+".txt";
        headerComment+=" dPhi "+dPhi+"dTheta "+dTheta+"            |"+"\n";
        headerComment+="|----------------------------------------------------------------------|"+"\n";
    }

    public  void check (PolarAngles angles, String someSort)
    {
        //only for backscattered and sputtered!
        if (sort.contains(someSort))
            angleMap[(int) (Math.round(angles.getAzimuth() / dPhi))][(int) Math.round( angles.getPolar()/ dTheta)]++;
    }

    public double[]getSpectrum() {
        return null;
    }

    public double[][] getMap(){
        return  angleMap;
    }

    @Override
    public boolean logDistribution() {
        try {
            FileOutputStream surfaceWriter = new FileOutputStream(pathToLog);
            String stroka ="Phi";
            surfaceWriter.write(headerComment.getBytes());
            for (int i = 0; i <=(int) Math.round(90 / dTheta); i++)
            {
                stroka=stroka+" "+(int) (i*dTheta);
            }
            stroka=stroka+"\n";
            surfaceWriter.write(stroka.getBytes());
            double coefficientForColorMap=0;

            for (int i = 0; i <=(int) Math.round(360 / dPhi); i++) {
                stroka=(int) (i*dPhi)+" ";
                for (int j = 0; j <= (int) Math.round(90 / dTheta); j++) {
                    if (i<(int) Math.round(360 / dTheta)) stroka = stroka + angleMap[i][j] + " ";
                    else  stroka=stroka+angleMap[i-1][j]+" ";
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
        EventQueue.invokeLater(() -> new ScatterColorMap("ScatterAnalyzer",  angleMap, dPhi, dTheta));
        return  true;
    }


}

