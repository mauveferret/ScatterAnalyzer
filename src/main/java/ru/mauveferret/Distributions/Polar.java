package ru.mauveferret.Distributions;

import javafx.application.Platform;
import ru.mauveferret.ParticleInMatterCalculator;
import ru.mauveferret.PolarChart;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Polar extends Dependence {

    private final double phi;
    private final double dPhi;
    private final double dTheta;

    public Polar(double phi, double dPhi, double dTheta, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        //even if user entered phi>180, we will look after the plane with phi<180, which is generally the same
        this.phi = (phi > 180) ? phi-180 : phi;
        // Idea is that input delta is absolute values, but we use at as difference |a-b|<delta
        this.dPhi = dPhi/2;
        this.dTheta = dTheta;

        depType = "distribution";
        distributionSize = (int) Math.ceil(180/dTheta)+1;
        endOfPath="_phi "+phi+"_dphi "+dPhi+"_dTheta"+dTheta+".txt";
        String addheaderComment = " phi "+phi+"  degrees dPhi "+dPhi+" degrees dTheta "+dTheta+" degrees ";
        headerComment +=calculator.createLine(addheaderComment)+"*".repeat(calculator.lineLength)+"\n";
        headerComment= "Angle dN/dOmega "+"\n"+"degrees  particles \n\n"+headerComment+"\n";
    }


    public void check(PolarAngles angles, String someSort, String element){
       // System.out.println(57.2958*Math.acos(cosa));
        //if (Math.abs(57.2958*Math.acos(cosa)-phi)<dPhi || (57.2958*Math.acos(cosa) > 180- dPhi))
        if (sort.contains(someSort)) {
            if (angles.doesAzimuthAngleMatch(phi,dPhi)) {
                distributionArray.get(element)[(int) Math.round((90+angles.getPolar()) / dTheta)]++;
                distributionArray.get("all")[(int) Math.round((90+angles.getPolar()) / dTheta)]++;
            }
            if (angles.doesAzimuthAngleMatch(phi+180,dPhi)) {
                distributionArray.get(element)[(int) Math.round((90-angles.getPolar()) / dTheta)]++;
                distributionArray.get("all")[(int) Math.round((90-angles.getPolar()) / dTheta)]++;
            }
        }
    }

    @Override
    public boolean logDependencies() {

        for (String element: elements) {
            try {
                FileOutputStream polarWriter = new FileOutputStream(pathsToLog.get(element));
                String stroka;
                polarWriter.write(headerComment.getBytes());
                for (int i = 0; i <= (int) Math.round(180 / dTheta); i++) {
                    //FIXME Omega angle ?!
                    if (i * dTheta != 90) {
                        distributionArray.get(element)[i] = distributionArray.get(element)[i] / (Math.toRadians(dPhi) *
                                Math.sin(Math.toRadians(Math.abs(i * dTheta - 90))));
                    } //we can't divide by zero
                    else {
                        //TODO
                        distributionArray.get(element)[i] = distributionArray.get(element)[i] / (Math.toRadians(dPhi) *
                                Math.sin(Math.toRadians(dTheta)));
                    }
                    stroka = ((i * dTheta - 90)) + columnSeparatorInLog
                            + new BigDecimal(distributionArray.get(element)[i]).setScale(4, RoundingMode.UP) + "\n";
                    //FIXME maybe you shoud write it as it is?!
                    if (i * dTheta != 90) polarWriter.write(stroka.getBytes());
                }
                polarWriter.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return  true;
    }

    //only for "all" elements of the target
    @Override
    public boolean visualize() {
        if (!sort.equals("") && doVisualisation)
        Platform.runLater(() -> {
            String title = calculator.projectileElements+" with "+calculator.projectileMaxEnergy+" eV hits at polar angle of "+
                    calculator.projectileIncidentPolarAngle +" degrees target of "+
                    calculator.targetElements+".\n Dependence made at phi " +
                    calculator.projectileIncidentAzimuthAngle+" degrees. Delta is "+dTheta+" degrees. Chart is for "+
                    sort+" particles";
           new PolarChart(title,distributionArray.get("all"), dTheta, calculator.projectileIncidentPolarAngle, pathsToLog.get("all"));
        });
        return  true;
    }
}