package ru.mauveferret.Distributions;

import javafx.application.Platform;
import org.jfree.ui.RefineryUtilities;
import ru.mauveferret.GUI;
import ru.mauveferret.ParticleInMatterCalculator;
import ru.mauveferret.PolarChart;

import java.io.FileOutputStream;

public class Polar extends Distribution {

    private final double phi;
    private final double dPhi;
    private final double dTheta;

    private final double[] polarAngleSpectrum;

    public Polar(double phi, double dPhi, double dTheta, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        //even if user entered phi>180, we will look after the plane with phi<180, which is generally the same
        this.phi = (phi > 180) ? phi-180 : phi;
        this.dPhi = dPhi;
        this.dTheta = dTheta;
        polarAngleSpectrum = new double[(int) Math.ceil(180/dTheta)+1];
        pathToLog+="_phi "+phi+"_dphi "+dPhi+"_dTheta"+dTheta+"_time "+ ((int ) (Math.random()*100))+".txt";
        headerComment+="| phi "+phi+" dPhi "+dPhi+"dTheta "+dTheta+"            |"+"\n";
        headerComment+="|----------------------------------------------------------------------|"+"\n";
    }


    public void check( PolarAngles angles, String someSort ){
       // System.out.println(57.2958*Math.acos(cosa));
        //if (Math.abs(57.2958*Math.acos(cosa)-phi)<dPhi || (57.2958*Math.acos(cosa) > 180- dPhi))
        if (sort.contains(someSort)) {
            if (angles.doesAzimuthAngleMatch(phi,dPhi))
                polarAngleSpectrum[(int) Math.round((90+angles.getPolar()) / dTheta)]++;
            if (angles.doesAzimuthAngleMatch(phi+180,dPhi))
                polarAngleSpectrum[(int) Math.round((90-angles.getPolar()) / dTheta)]++;
            }
    }

    public double[] getSpectrum() {
        return polarAngleSpectrum;
    }

    @Override
    public boolean logDistribution() {
        try {
            FileOutputStream polarWriter = new FileOutputStream(pathToLog);
            String stroka;
            polarWriter.write(headerComment.getBytes());
            for (int i = 0; i <= (int) Math.round(180/ dTheta); i++) {
                stroka = ((i * dTheta)-90) + "" +
                        " " + polarAngleSpectrum[i] + "\n";
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
            String title = calculator.projectileElements+" "+calculator.projectileMaxEnergy+" hits at phi " +
                    calculator.projectileIncidentAzimuthAngle+
                    " --> "+calculator.targetElements;
           new PolarChart(title,polarAngleSpectrum, dTheta);
        });
        return  true;
    }
}