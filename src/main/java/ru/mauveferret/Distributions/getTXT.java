package ru.mauveferret.Distributions;

import ru.mauveferret.ParticleInMatterCalculator;

import java.io.FileOutputStream;

public class getTXT extends Distribution {

    public getTXT(ParticleInMatterCalculator calculator, String sort) {
        super(calculator, sort);
        sort = "null";
    }

    @Override
    int[] getSpectrum() { return null;}

    @Override
    public boolean logDistribution() { return true; }

    @Override
    public boolean visualize() {
        return true;
    }

    public void check(String stroka) {
        try {
            FileOutputStream energyWriter = new FileOutputStream(pathToLog);
            energyWriter.write(stroka.getBytes());
            energyWriter.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
