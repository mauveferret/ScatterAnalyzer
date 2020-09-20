package ru.mauveferret.Distributions;

import javafx.application.Platform;
import ru.mauveferret.GUI;
import ru.mauveferret.ParticleInMatterCalculator;

import java.io.FileOutputStream;

public class Polar extends Distribution {

    private final double phi;
    private final double dPhi;
    private final double dTheta;

    private int polarAngleSpectrum[];

    public Polar(double phi, double dPhi, double dTheta, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.phi = phi;
        this.dPhi = dPhi;
        this.dTheta = dTheta;
        //TODO 180 or 360?
        polarAngleSpectrum = new int[(int) Math.ceil(360/dTheta)+1];
        pathToLog+="_phi "+phi+"_dphi "+dPhi+"_dTheta"+dTheta+"_time "+ ((int ) (Math.random()*100))+".txt";
        headerComment+="| phi "+phi+" dPhi "+dPhi+"dTheta "+dTheta+"            |"+"\n";
        headerComment+="|----------------------------------------------------------------------|"+"\n";
    }

    public void check (double x, double y, double z, String someSort)
    {
        PolarAngles angles = new PolarAngles(x,y,z);
        if (angles.doesAngleMatch(phi, dPhi, false))
            if (sort.contains(someSort))
            {
                polarAngleSpectrum[(int) Math.round(angles.getPolar() / dTheta)]++;
            }
    }

    public int[] getSpectrum() {
        return polarAngleSpectrum;
    }

    @Override
    public boolean logDistribution() {
        try {
            FileOutputStream polarWriter = new FileOutputStream(pathToLog);
            String stroka;
            polarWriter.write(headerComment.getBytes());
            for (int i = 0; i <= (int) Math.round(360 / dTheta); i++) {
                stroka = i * dTheta + " " + polarAngleSpectrum[i] + "\n";
                polarWriter.write(stroka.getBytes());
            }
            polarWriter.close();
            return  true;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return  false;
        }
    }

    @Override
    public boolean visualize() {
        Platform.runLater(() -> {

            if (!sort.equals("")) new GUI().showGraph(polarAngleSpectrum, 360, dTheta,  "Угловой спектр "+
                    calculator.projectileElements+" --> "+calculator.targetElements+" phi = "+phi+" dtheta = "+dTheta);
        });
        return  true;
    }
}