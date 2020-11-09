package ru.mauveferret.Distributions;

import javafx.application.Platform;
import ru.mauveferret.GUI;
import ru.mauveferret.ParticleInMatterCalculator;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class Energy extends Dependence {

    private final double E0;
    private final double theta;
    private final double dTheta;
    private final double phi;
    private final double dPhi;
    private final double dE;

    public Energy(double dE, double phi, double dPhi, double theta, double dTheta, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.E0 = calculator.projectileMaxEnergy;
        this.theta = theta;
        // Idea is that input delta is absolute values, but we use at as difference |a-b|<delta
        this.dTheta = dTheta/2;
        this.phi = phi;
        this.dPhi = dPhi/2;
        this.dE = dE;

        depType = "distribution";
        distributionSize = (int) Math.ceil(E0/dE)+1;
        endOfPath="_theta "+theta+"_phi "+phi+"_dE"+dE+"_time "+ ((int ) (Math.random()*100))+".txt";
        String addheaderComment = " delta E "+dE+" eV theta "+theta+" deg dTheta "+dTheta+" deg phi "+
                phi+" deg dPhi "+dPhi+" deg";
        headerComment +=calculator.createLine(addheaderComment)+"*".repeat(calculator.lineLength)+"\n";
        headerComment= "Energy particles "+"\n"+"eV  count \n\n"+headerComment+"\n";

    }

    public void check(PolarAngles angles, String someSort, double E, String element){

        if (sort.contains(someSort)) {

            //if (Math.abs(57.2958*Math.acos(cosa)-phi)<dPhi && Math.abs(57.2958*Math.acos(cosp)-theta)<dTheta)
            if (angles.doesAzimuthAngleMatch(phi, dPhi) && angles.doesPolarAngleMatch(theta, dTheta)) {
                distributionArray.get(element)[(int) Math.round(E / dE)]++;
                distributionArray.get("all")[(int) Math.round(E / dE)]++;

            }
        }
    }

    @Override
    public boolean logDependencies() {

        for (String element: elements) {

            for (int i = 0; i < distributionArray.get(element).length; i++) {
                distributionArray.get(element)[i] = distributionArray.get(element)[i] / dE;
            }

            try {
                FileOutputStream energyWriter = new FileOutputStream(pathsToLog.get(element));
                String stroka;
                energyWriter.write(headerComment.getBytes());
                for (int i = 0; i <= (int) Math.round(E0 / dE); i++) {
                    stroka = i * dE + columnSeparatorInLog
                            + new BigDecimal(distributionArray.get(element)[i] / dE).
                            setScale(4, RoundingMode.UP) + "\n";
                    energyWriter.write(stroka.getBytes());
                }
                energyWriter.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return  true;
    }

    @Override
    public boolean visualize() {
        Platform.runLater(() -> {

            if (!sort.equals("") && doVisualisation) new GUI().showGraph(distributionArray.get("all"), E0, dE, "Энергетический спектр "+
                    calculator.projectileElements+" --> "+calculator.targetElements+" phi = "+phi+" theta = "+theta);
            //if (NThetaR||NThetaY) new GUI().showGraph(polarAngleSpectrum,360, dThetaNTheta, "угловой спектр, phi = "+phiNTheta1);
        });
        return  true;
    }
}



