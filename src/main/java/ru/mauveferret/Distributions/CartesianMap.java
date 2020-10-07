package ru.mauveferret.Distributions;

import ru.mauveferret.ParticleInMatterCalculator;

import java.io.FileOutputStream;

public class CartesianMap extends Distribution{


    private double cartesianMap[][];
    String mapType;
    String typeOfX, typeOfY;
    private final double delta;
    int size = 10000; //Angstrom

    public CartesianMap(double delta, String mapType, String sort, ParticleInMatterCalculator calculator) {
        super(calculator, sort);
        this.mapType = mapType;
        typeOfX = mapType.charAt(0)+"";
        typeOfY = mapType.charAt(1)+"";
        this.delta = delta;
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
            if (X>0 && Y>0 ) cartesianMap[(int) (Math.round(X / delta))][(int) (Math.round(Y / delta))]++;
            else if (X>0 && Y<0) cartesianMap[(int) (Math.round(X / delta))][(int) (size/(2*delta)) - (int) (Math.round(Y / delta))]++;
            else if (X<0 && Y>0) cartesianMap[(int) (size/(2*delta)) - (int) (Math.round(X / delta))][(int) (Math.round(Y / delta))]++;
            else cartesianMap[(int) (size/(2*delta)) -(int) (Math.round(X / delta))][(int) (size/(2*delta)) -(int) (Math.round(Y / delta))]++;
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
            for (int i = 0; i <=(int) Math.round(size / delta); i++)
            {
                stroka=stroka+" "+(int) ((i*delta)-size/2);
            }
            stroka=stroka+"\n";
            surfaceWriter.write(stroka.getBytes());

            for (int i = 0; i <=(int) Math.round(size / delta); i++) {
                stroka=(int) ((i*delta) - size/2)+" ";
                for (int j = 0; j <= (int) Math.round(size / delta); j++) {
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
