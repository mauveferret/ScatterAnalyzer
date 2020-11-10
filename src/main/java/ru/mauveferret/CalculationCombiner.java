package ru.mauveferret;

import ru.mauveferret.Distributions.Dependence;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class CalculationCombiner extends ParticleInMatterCalculator {

    /* used to summary data from several single calculations with common targets
    in order to create model for multi -energy -angle -mass incident beam
     */



    private final ArrayList<ParticleInMatterCalculator> calculators;

    public CalculationCombiner(String directoryPath, ArrayList<ParticleInMatterCalculator> calculators) {
        super(directoryPath, false);
        this.calculators = calculators;
        elementsList = new ArrayList<>();
        LINE_LENGTH = 170;
    }

    public boolean combine(){
        try{
            combineCalculatorsVariables();
            generateDependencies();
            combineDependencies();
            return  true;
        }
        catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    private void combineCalculatorsVariables (){
        projectileElements = "";
        String sProjectileMaxEnergy = "";
        String sProjectileIncidentPolarAngle = "";
        String sProjectileIncidentAzimuthAngle = "";
        projectileAmount = 0;
        targetElements = "";
        calcTime = 0;
        for (ParticleInMatterCalculator calculator: calculators){
            if (!calculatorType.contains(calculator.calculatorType)) calculatorType+=calculator.calculatorType+";";
            projectileElements+=calculator.projectileElements+";";
            sProjectileMaxEnergy+=calculator.projectileMaxEnergy+";";
            sProjectileIncidentPolarAngle+=calculator.projectileIncidentPolarAngle+";";
            sProjectileIncidentAzimuthAngle+=calculator.projectileIncidentAzimuthAngle+";";
            projectileAmount+=calculator.projectileAmount;
            if (!targetElements.contains(calculator.targetElements)) targetElements+=calculator.targetElements+";";
            for (String element: calculator.elementsList) {
                if (!elementsList.contains(element)) elementsList.add(element);
            }
            calcTime+=calculator.calcTime;
            System.out.println("        [COMBINER] found new calc: "+calculator.modelingID);
        }
        //to remove "," from the end
        calculatorType = calculatorType.substring(0, calculatorType.length()-1);
        projectileElements = projectileElements.substring(0, projectileElements.length()-1);
        sProjectileMaxEnergy =  sProjectileMaxEnergy.substring(0, sProjectileMaxEnergy.length()-1);
        sProjectileIncidentPolarAngle = sProjectileIncidentPolarAngle.
                substring(0, sProjectileIncidentPolarAngle.length()-1);
        sProjectileIncidentAzimuthAngle = sProjectileIncidentAzimuthAngle.
                substring(0,sProjectileIncidentAzimuthAngle.length()-1);
        if (targetElements.contains(";")) targetElements = targetElements.substring(0, targetElements.length()-1);
        initializeCalcVariables();
    }

    private void generateDependencies (){
        //random calculator, because they all should have the same deps parameters
        dependencies = calculators.get(0).dependencies;
        for (Dependence dep: dependencies){
            dep.calculator = this;
            //find the largest arrays
            for (ParticleInMatterCalculator calculator: calculators){
                for (Dependence dependence: calculator.dependencies){
                    if (dep.depName.equals(dependence.depName)){
                        switch (dep.depType){
                            case "distribution":
                                if (dependence.distributionSize>dep.distributionSize){
                                    dep.distributionSize = dependence.distributionSize;
                                }
                                break;
                            case "map":
                                if (dependence.mapArrayXsize>dep.mapArrayXsize){
                                    dep.mapArrayXsize =dependence.mapArrayXsize;
                                }
                                if (dependence.mapArrayYsize>dep.mapArrayYsize){
                                    dep.mapArrayYsize =dependence.mapArrayYsize;
                                }
                                break;
                        }
                    }
                }
            }
            dep.initializeArrays(elementsList);
        }
    }

    private void combineDependencies(){
        for (Dependence dependence: dependencies){
            for (ParticleInMatterCalculator calculator: calculators){
                for (Dependence dep: calculator.dependencies){
                    if (dep.depName.equals(dependence.depName)){
                        switch (dependence.depType){
                            case "distribution":
                                for (int i=0; i<dep.distributionSize; i++){
                                    for (String element: calculator.elementsList){
                                        dependence.distributionArray.get(element)[i]=
                                                dependence.distributionArray.get(element)[i]+
                                        dep.distributionArray.get(element)[i];
                                    }
                                }
                                break;
                            case "map":
                                for (int i=0; i<dep.mapArrayXsize; i++)
                                    for (int j=0; j<dep.mapArrayYsize; j++){
                                    for (String element: calculator.elementsList){
                                        dependence.mapArray.get(element)[i][j]=
                                                dependence.mapArray.get(element)[i][j]+
                                                        dep.mapArray.get(element)[i][j];
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    @Override
    String initializeModelParameters() {
        return null;
    }

    @Override
    void postProcessCalculatedFiles(ArrayList<Dependence> distributions) {

    }
}
