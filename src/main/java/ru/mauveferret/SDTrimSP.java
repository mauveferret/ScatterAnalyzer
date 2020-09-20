package ru.mauveferret;

import ru.mauveferret.Distributions.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class SDTrimSP extends ParticleInMatterCalculator{

    String dataPath;

    SDTrimSP(String directoryPath) {
        super(directoryPath);
        dataPath = "";
    }

    @Override
    String initializeVariables() {
        calculatorType = "SDTrimSP";
        File dataDirectory = new File(directoryPath);
        if (dataDirectory.isDirectory()){
            String tscConfig = "";

            //get info from *.tsc file

            try {
                for (File file:  dataDirectory.listFiles()){
                    if (file.getName().matches("SC\\d+.tsc")){
                        modelingID = file.getName();
                        tscConfig = file.getAbsolutePath();
                    }
                    if (file.getName().matches("SC\\d+.dat")){
                        modelingID = file.getName();
                        dataPath = file.getAbsolutePath();
                    }
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tscConfig)));
                String line;
                String previousLine = "";
                String someParameter = "";
                while (reader.ready()){
                    line = reader.readLine();
                    if (line.contains("=")) someParameter = line.substring(line.indexOf("="+1)).trim();
                    if (line.contains("Atom") && previousLine.contains("Projectile")) projectileElements= someParameter;
                    if (line.contains("Atom") && !previousLine.contains("Projectile")) targetElements += someParameter+" ";
                    if (line.contains("StartEnergy")) projectileMaxEnergy = Double.parseDouble(someParameter);
                    if (line.contains("StartAngle")) projectileIncidentPolarAngle = Double.parseDouble(someParameter);
                    if (line.contains("StartPhi")) projectileIncidentAzimuthAngle = Double.parseDouble(someParameter);
                    if (line.contains("Number")) projectileIncidentPolarAngle = Integer.parseInt(someParameter);
                    previousLine = line;
                }
                reader.close();

            }
            catch (FileNotFoundException ex){
                return "\"SC******.tsc\" config is not found";
            }
            catch (IOException ex){
                return "File "+tscConfig+" is damaged";
            }

            //check whether the *.dat file exist

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath)));
                reader.close();
            }
            catch (Exception e){
                return "data file wasn't found";
            }

        }
        else return dataDirectory.getName()+" is not a directory";

        return "OK";
    }

    @Override
    void postProcessCalculatedFiles(ArrayList<Distribution> distributions) {

        time = System.currentTimeMillis();

        //find all SCATTER-related distributions

        float  floatSort, en = 0, cosx, cosy, cosz;
        String sort = "";

        try {
            DataInputStream reader = new DataInputStream(new FileInputStream(dataPath));
            byte[] buf = new byte[18];

            //TODO +17
            while (reader.available() >  stringCountPerCycle*18) {

                reader.read(buf);
                int shift = 0;

                // movement through buf for 'stringCountPerCycle' times
                for (int j = 1; j <= stringCountPerCycle; j++) {

                    floatSort = buf[shift];

                    if (floatSort<0) sort = "B";
                    else if (floatSort == 0) sort = "S";
                    else if (floatSort>0) sort = "I";

                    //second byte of buf has  unknown purpose

                    en = ByteBuffer.wrap(ArraySubPart(buf, 2 + shift, 5 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosx = ByteBuffer.wrap(ArraySubPart(buf, 6 + shift, 9 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosy = ByteBuffer.wrap(ArraySubPart(buf, 10 + shift, 13 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosz = ByteBuffer.wrap(ArraySubPart(buf, 14 + shift, 17 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    //allParticlesData+=sort+" "+en+" "+cosx+" "+cosy+" "+cosz+"\n";

                    //Here is several spectra calculators

                    for (Distribution distr: distributions){
                        switch (distr.getType())
                        {
                            case "energy": ((Energy) distr).check(cosx,cosy,cosz,sort,en);
                                break;
                            case "polar": ((Polar) distr).check(cosx,cosy,cosz,sort);
                                break;
                            case "anglemap": ((AngleMap) distr).check(cosx,cosy,cosz,sort);
                                break;
                            case "gettxt": ((getTXT) distr).check(sort+" "+en+" "+cosx+" "+cosy+" "+cosz+"\n");
                        }
                    }

                    //calculate some scattering constants
                    particleCount++;

                    if (sort.equals("B")) scattered++;
                    else if (sort.equals("S")) sputtered++;
                    else if (sort.equals("I")) implanted++;

                    shift += 18;
                }
            }
            reader.close();
            time=System.currentTimeMillis()-time;
            time=time/1000;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private  byte[] ArraySubPart (byte[] array,int a, int b) //part of one array into another
    {
        byte[] subArray = new byte[b-a+1];
        for (int i=a;i<=b;i++)
        {
            subArray[i-a]=array[i];
        }
        return subArray;
    }

}
