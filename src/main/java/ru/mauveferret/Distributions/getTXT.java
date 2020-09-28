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
    double[] getSpectrum() { return null;}

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

    public void check(PolarAngles angles, String someSort, double E) {
        try {
            energyWriter.write((E+" "+angles.getAzimuth()+" "+angles.getPolar()).getBytes());
            System.out.println(E+" "+angles.getAzimuth()+" "+angles.getPolar());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
