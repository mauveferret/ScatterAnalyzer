package ru.mauveferret.Distributions;

import ru.mauveferret.ParticleInMatterCalculator;

import java.io.FileOutputStream;
import java.util.Map;

public class CartesianMap extends Distribution{


    private double cartesianMap[][];
    String mapType;
    String typeOfX, typeOfY;
    private final double delta;
    int size = 500; //Angstrom
    int center;

    //max needs to cut from matrix zeros
    double max;
    //double xMax, yMax;

    public CartesianMap(double delta, String mapType, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.mapType = mapType;
        typeOfX = mapType.charAt(0)+"";
        typeOfY = mapType.charAt(1)+"";
        this.delta = delta;
        center = (int) (size/(2*delta));
        max = delta;
        pathToLog+="MAP OF "+mapType+" for "+sort+" delta "+delta+".txt";
        headerComment+=" delta "+delta+"sort "+sort+"type "+type+"            |"+"\n";
        headerComment+="|----------------------------------------------------------------------|"+"\n";
        cartesianMap = new double[(int) Math.ceil(size/delta)+10][(int) Math.ceil(size/delta)+10];
    }

    public CartesianMap(double delta, int size, String mapType, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.mapType = mapType;
        typeOfX = mapType.charAt(0)+"";
        typeOfY = mapType.charAt(1)+"";
        this.delta = delta;
        this.size = size;
        center = (int) (size/(2*delta));
        max = delta;
        pathToLog+="MAP OF "+mapType+" for "+sort+" delta "+delta+".txt";
        headerComment+=" delta "+delta+"sort "+sort+"type "+type+"            |"+"\n";
        headerComment+="|----------------------------------------------------------------------|"+"\n";
        cartesianMap = new double[(int) Math.ceil(size/delta)+10][(int) Math.ceil(size/delta)+10];
    }


    //not real X,Y but some two Axis, specified in mapType variable
    public  void check (double X, double Y, String someSort)
    {
        //only for backscattered and sputtered!
        if (sort.contains(someSort)){
            if (X<size*delta/2 & Y<size*delta/2) {
                if (X > 0 && Y > 0)
                    cartesianMap[center + (int) (Math.round(X / delta))][center + (int) (Math.round(Y / delta))]++;
                else if (X > 0 && Y < 0)
                    cartesianMap[center + (int) (Math.round(X / delta))][center - (int) (Math.round(Y / delta))]++;
                else if (X < 0 && Y > 0)
                    cartesianMap[center - (int) (Math.round(X / delta))][center + (int) (Math.round(Y / delta))]++;
                else
                    cartesianMap[center - (int) (Math.round(X / delta))][center - (int) (Math.round(Y / delta))]++;

                X = Math.abs(X);
                Y = Math.abs(Y);
                if (X > max) max = X;
                if (Y > max) max = Y;
            }
            /*if (X>max) {
                max = X;
                xMax = X;
                yMax = Y;
            }
            if (Y>max) {
                max = Y;
                xMax = X;
                yMax = Y;
            }

             */
        }
    }

    @Override
    double[] getSpectrum() {
        return new double[0];
    }

    @Override
    public boolean logDistribution() {
        try {
            FileOutputStream surfaceWriter = new FileOutputStream(pathToLog);
            String stroka =typeOfX+": ";
            surfaceWriter.write(headerComment.getBytes());

            int leftEdge = center-((int) (max/delta))-2;
            int rightEdge = center+ ((int) (max/delta))+2;


            /*check whether is possible to mak edge less

            for (int i = rightEdge; i>center; i--){
                double sum = 0;
                for (int j=center; j<rightEdge;j++) sum+=cartesianMap[((int) (xMax/delta))][j];
                if (sum<1000) rightEdge--;
            }

            for (int i = leftEdge; i<center; i++){
                double sum = 0;
                for (int j=center; j>leftEdge;j--) sum+=cartesianMap[((int) (xMax/delta))][j];
                if (sum<1000) leftEdge--;
            }

            System.out.println("after min"+center+" "+leftEdge+" "+rightEdge);

             */


            for (int i = leftEdge ; i <=rightEdge; i++)
            {
                stroka=stroka+" "+(int) ((i-center)*delta);
            }
            stroka=stroka+"\n";
            surfaceWriter.write(stroka.getBytes());

            for (int i = leftEdge; i <=rightEdge; i++) {
                stroka=(int) ((i-center)*delta)+" ";
                for (int j = leftEdge; j <= rightEdge; j++) {
                    stroka=stroka+cartesianMap[i][j] +" ";
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
        return false;
    }
}
