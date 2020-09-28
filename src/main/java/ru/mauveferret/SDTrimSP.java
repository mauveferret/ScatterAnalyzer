package ru.mauveferret;

import ru.mauveferret.Distributions.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class SDTrimSP extends ParticleInMatterCalculator{

    ArrayList<String> dataPath;

    SDTrimSP(String directoryPath, boolean doVizualization) {
        super(directoryPath, doVizualization);
        dataPath = new ArrayList<>();

        projectileIncidentAzimuthAngle = 0;
        projectileIncidentPolarAngle = 0;
        projectileAmount = -1;
    }

    @Override
    String initializeVariables() {
        calculatorType = "SDTrimSP";
        File dataDirectory = new File(directoryPath);
        if (dataDirectory.isDirectory()){
            String tscConfig = "";

            //get info from tri.inp file

            try {
                for (File file:  dataDirectory.listFiles()){
                    if (file.getName().contains("tri.inp")){
                        modelingID = "SDTrimSP"+((int) (Math.random()*1000));
                        tscConfig = file.getAbsolutePath();
                    }
                    if (file.getName().contains("partic")){
                        dataPath.add(file.getAbsolutePath());
                    }
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tscConfig)));
                String line;
                String someParameter = "";


                while (reader.ready()){
                    line = reader.readLine();

                    if (line.contains("=")) {
                        someParameter = line.substring(line.indexOf("=")+1).trim();
                    }

                    if (line.contains("symbol")) {
                        projectileElements = someParameter;
                        targetElements = "";
                    }

                    if (line.contains("e0")&& !line.contains("case"))
                    {
                        if (someParameter.contains(",")) {
                            projectileMaxEnergy = Double.parseDouble(someParameter.substring(0, someParameter.indexOf(",")).trim());
                        }
                        else projectileMaxEnergy = Double.parseDouble(someParameter.trim());
                    }

                    if (line.contains("alpha0")) {
                        projectileIncidentPolarAngle = Double.parseDouble(someParameter);
                        projectileIncidentAzimuthAngle = 0;
                    }
                    //we multiply on 10 because there is a bug in SDTrimSP 6_02: it counts 10 time more particles,
                    //than in "nh" field FIXME
                    if (line.contains("nh")) projectileAmount = Integer.parseInt(someParameter); //*10;
                }
                reader.close();

            }
            catch (FileNotFoundException ex){
                ex.printStackTrace();
                return "\"tri.inp\" config is not found";
            }
            catch (IOException ex){
                ex.printStackTrace();
                return "File "+tscConfig+" is damaged";
            }

            //check whether the *.dat file exist

            try {
                for (String someDataFilePath: dataPath ) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(someDataFilePath)));
                    reader.close();
                }
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

        //find all TRIM-related distributions

        double en = 0, collisionsAmount=0, fluence=0,  xEnd=0, yEnd=0, zEnd=0, cosP=0,cosA=0, path=0;
        String sort;

        try {

            for (String someDataFilePath: dataPath){

            BufferedReader br = new BufferedReader(new FileReader(someDataFilePath));

            //rubbish lines
            String line = br.readLine();
                br.readLine();
                br.readLine();
                br.readLine();
                br.readLine();
            //now lets sort

                String[] datas;


                String particlesType = someDataFilePath.substring(someDataFilePath.indexOf(File.separator));
                particlesType = particlesType.substring(particlesType.indexOf("_"),particlesType.lastIndexOf("."));

                sort = "";
                if (particlesType.contains("back_p")) sort = "B";
                else if (particlesType.contains("back_r")) sort = "S";
                else if (particlesType.contains("stop_p")) sort = "I";
                else if (particlesType.contains("stop_r")) sort = "D"; //displaces FIXME
                else if (particlesType.contains("tran")) sort = "T";


            while (br.ready()) {
                line = br.readLine();
                if (!line.contains("end")) {

                   //line.replaceAll("\\\\s+"," ");
                 //  line.replaceAll("\\\\u0020'", " ");
                 // line.replaceAll("\\\\u0020\\\\u0020"," ");
               //  line.replaceAll("[^\\\\u0009\\\\u000a\\\\u000d\\\\u0020-\\\\uD7FF\\\\uE000-\\\\uFFFD]", "");

                    datas = line.split(" ");

                    int ien=0, ix=0,iy=0,iz=0;
                    int column=0;
                    double value=0;
                    for (int i=0; i<datas.length;i++)
                    {
                        try {
                            value = Float.parseFloat(datas[i]);
                            column++;
                        }
                        catch (Exception e)
                        {
                            //failed to find correct delimiter
                        }
                        switch (column){
                            case 2: collisionsAmount = value;
                            break;
                            case 3: fluence = value;
                            break;
                            case 4: en = value;
                            break;
                            case 8: xEnd = value;
                            break;
                            case 9: yEnd = value;
                            break;
                            case 10: zEnd = value;
                            break;
                            case 14: cosP = value;
                            break;
                            case 15: cosA = value;
                            break;
                            case 16: path = value;
                            break;
                        }

                    }

                    //Here is several spectra calculators

                    PolarAngles angles = new PolarAngles(cosP, cosA, xEnd, yEnd);

                 //  if (!someDataFilePath.contains("stop")) System.out.println(angles.getPolar()+" "+angles.getAzimuth());

                    for (Distribution distr : distributions) {
                        switch (distr.getType()) {
                            case "energy":
                                ((Energy) distr).check(angles,sort,en);
                                break;
                            case "polar":
                                ((Polar) distr).check(angles, sort);
                                break;
                            case "anglemap":
                                ((AngleMap) distr).check(angles, sort);
                                break;
                            case "gettxt":
                                ((getTXT) distr).check(angles, sort, en);
                        }
                    }

                    //calculate some scattering constants
                    if (!sort.contains("S") && !sort.contains("D")) particleCount++;

                    if (sort.equals("B")) {
                        scattered++;
                        energyRecoil+=en;
                    }
                    else if (sort.equals("S")) sputtered++;
                    else if (sort.equals("I")) implanted++;
                }
            }
                br.close();
            }
            scattered = scattered / particleCount;
            sputtered = sputtered / particleCount;
            implanted = implanted / particleCount;
            transmitted = transmitted / particleCount;
            energyRecoil = energyRecoil/(projectileMaxEnergy*particleCount);
            time=System.currentTimeMillis()-time;
            time=time/((double) 1000);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
