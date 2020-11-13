package ru.mauveferret.Dependencies;

import javafx.application.Platform;
import ru.mauveferret.Calcuators.ParticleInMatterCalculator;
import ru.mauveferret.PolarChart;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Polar extends Dependence {

    public final double phi;
    public final double dPhi;
    public final double dTheta;

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
    }

    @Override
    public void initializeArrays(ArrayList<String> elements) {
        headerComment = calculator.createHeader();
        String addheaderComment = " phi "+phi+"  degrees dPhi "+dPhi+" degrees dTheta "+dTheta+" degrees ";
        headerComment +=calculator.createLine(addheaderComment)+"*".repeat(calculator.LINE_LENGTH)+"\n";
        headerComment= "Angle dN/dOmega "+"\n"+"degrees  particles \n\n"+headerComment+"\n";
        super.initializeArrays(elements);
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
                //System.out.println("erhbrseyhedrtrterh");
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
                //System.out.println(pathsToLog.get(element));
                String stroka;
               polarWriter.write(headerComments.get(element).getBytes());

               double[] newArray = new double[distributionArray.get(element).length];
               for (int j=0; j<newArray.length; j++) newArray[j] = distributionArray.get(element)[j];

                for (int i = 0; i <= (int) Math.round(180 / dTheta); i++) {
                    //FIXME Omega angle ?!
                    if (i * dTheta != 90) {
                        newArray[i] = newArray[i] / (Math.toRadians(dPhi) *
                                Math.sin(Math.toRadians(Math.abs(i * dTheta - 90))));
                    } //we can't divide by zero
                    else {
                        //TODO
                        newArray[i] = newArray[i] / (Math.toRadians(dPhi) *
                                Math.sin(Math.toRadians(dTheta)));
                    }
                    stroka = ((i * dTheta - 90)) + columnSeparatorInLog
                            + new BigDecimal(newArray[i]).setScale(4, RoundingMode.UP) + "\n";
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