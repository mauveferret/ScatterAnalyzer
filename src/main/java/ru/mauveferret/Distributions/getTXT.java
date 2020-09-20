package ru.mauveferret.Distributions;

import ru.mauveferret.ParticleInMatterCalculator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class getTXT extends Distribution {

    FileOutputStream energyWriter;

    public getTXT(ParticleInMatterCalculator calculator, String sort) {
        super(calculator, sort);
        sort = "null";
        pathToLog+=".txt";
        try {
          energyWriter = new FileOutputStream(pathToLog);
        }
        catch (Exception e){e.printStackTrace();}
    }



    @Override
    int[] getSpectrum() { return null;}

    @Override
    public boolean logDistribution() { return true; }

    @Override
    public boolean visualize() {
        try {
            energyWriter.close();
        }
        catch (Exception e){e.printStackTrace();}
        return true;
    }

    public void check(double x, double y, double z, String someSort, double E) {
        try {


            PolarAngles angles = new PolarAngles(x,y,z);
            energyWriter.write((E+" "+angles.getAzimuth()+" "+angles.getPolar()).getBytes());
            System.out.println(E+" "+angles.getAzimuth()+" "+angles.getPolar());

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
