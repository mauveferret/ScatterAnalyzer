package ru.mauveferret.Distributions;

import javafx.application.Platform;
import ru.mauveferret.GUI;
import ru.mauveferret.ParticleInMatterCalculator;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class Energy extends Distribution{

    private final double E0;
    private final double theta;
    private final double dTheta;
    private final double phi;
    private final double dPhi;
    private final double dE;

    private double[] energySpectrum;  //forms energyspectra

    public Energy(double dE, double phi, double dPhi, double theta, double dTheta, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.E0 = calculator.projectileMaxEnergy;
        this.theta = theta;
        this.dTheta = dTheta;
        this.phi = phi;
        this.dPhi = dPhi;
        this.dE = dE;
        energySpectrum =  new double[(int) Math.ceil(E0/dE)+1];
        pathToLog+="_theta "+theta+"_phi "+phi+"_dE"+dE+"_time "+ ((int ) (Math.random()*100))+".txt";

        String addheaderComment = " delta E "+dE+" eV theta "+theta+" deg dTheta "+dTheta+" deg phi "+
                phi+" deg dPhi "+dPhi+" deg";
        headerComment +=createLine(addheaderComment)+"*".repeat(lineLength)+"\n";
        headerComment= "Energy particles "+"\n"+"eV  count \n\n"+headerComment+"\n";

    }

    public void check(PolarAngles angles, String someSort, double E ){

        if (sort.contains(someSort)) {

            //if (Math.abs(57.2958*Math.acos(cosa)-phi)<dPhi && Math.abs(57.2958*Math.acos(cosp)-theta)<dTheta)
            if (angles.doesAzimuthAngleMatch(phi, dPhi) && angles.doesPolarAngleMatch(theta, dTheta)) {

                energySpectrum[(int) Math.round(E / dE)]++;
            }
        }
    }

    public double[] getSpectrum() {
        return energySpectrum;
    }

    @Override
    public boolean logDistribution() {

        //I don't know why, but Daniel want spectra to be divided by E
        //TODO check why is it need?
       for (int i=0; i<energySpectrum.length; i++){
           energySpectrum[i]=energySpectrum[i]/((i!=0)? i*dE : dE);
       }

        try {
            FileOutputStream energyWriter = new FileOutputStream(pathToLog);
            String stroka;
            energyWriter.write(headerComment.getBytes());
            for (int i = 0; i <= (int) Math.round(E0 / dE); i++) {
                 stroka = i * dE + columnSeparatorInLog
                         +  new BigDecimal( energySpectrum[i]/dE).setScale(4, RoundingMode.UP) + "\n";
                energyWriter.write(stroka.getBytes());
            }
            energyWriter.close();
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

            if (!sort.equals("") && doVisualisation) new GUI().showGraph(energySpectrum, E0, dE, "Энергетический спектр "+
                    calculator.projectileElements+" --> "+calculator.targetElements+" phi = "+phi+" theta = "+theta);
            //if (NThetaR||NThetaY) new GUI().showGraph(polarAngleSpectrum,360, dThetaNTheta, "угловой спектр, phi = "+phiNTheta1);
        });
        return  true;
    }
}



