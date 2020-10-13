package ru.mauveferret.Distributions;

import javafx.application.Platform;
import org.jfree.ui.RefineryUtilities;
import ru.mauveferret.GUI;
import ru.mauveferret.ParticleInMatterCalculator;
import ru.mauveferret.PolarChart;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
        pathToLog+="_phi "+phi+"_dphi "+dPhi+"_dTheta"+dTheta+".txt";

        String addheaderComment = " phi "+phi+"  degrees dPhi "+dPhi+" degrees dTheta "+dTheta+" degrees ";
        headerComment +=calculator.createLine(addheaderComment)+"*".repeat(calculator.lineLength)+"\n";
        headerComment= "Angle dN/dOmega "+"\n"+"degrees  particles \n\n"+headerComment+"\n";
      //  String longName =" Angle,deg  partCount ";
       // longName = "*".repeat((lineLength-longName.length())/2)+longName+"*".repeat((lineLength-longName.length())/2)+"\n";
       // headerComment= longName+headerComment;
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
                //FIXME Omega angle ?!
                if (i*dTheta!=90) {
                    polarAngleSpectrum[i] = polarAngleSpectrum[i]/(2*Math.toRadians(dPhi)*Math.sin(Math.toRadians(Math.abs(i*dTheta-90))));
                } //we can't divide by zero
                else{
                    //TODO
                    polarAngleSpectrum[i] = polarAngleSpectrum[i]/(2*Math.toRadians(dPhi)*Math.sin(Math.toRadians(dTheta)));
                }
                stroka = ((i * dTheta-90)) + columnSeparatorInLog
                        + new BigDecimal( polarAngleSpectrum[i]).setScale(4, RoundingMode.UP) + "\n";
                //FIXME maybe you shoud write it as it is?!
                if (i*dTheta!=90) polarWriter.write(stroka.getBytes());
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
        if (!sort.equals("") && doVisualisation)
        Platform.runLater(() -> {
            String title = calculator.projectileElements+" with "+calculator.projectileMaxEnergy+" eV hits at polar angle of "+
                    calculator.projectileIncidentPolarAngle +" degrees target of "+
                    calculator.targetElements+".\n Distribution made at phi " +
                    calculator.projectileIncidentAzimuthAngle+" degrees. Delta is "+dTheta+" degrees. Chart is for "+
                    sort+" particles";
           new PolarChart(title,polarAngleSpectrum, dTheta, calculator.projectileIncidentPolarAngle, pathToLog);
        });
        return  true;
    }
}