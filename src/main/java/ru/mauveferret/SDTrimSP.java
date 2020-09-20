package ru.mauveferret;

import ru.mauveferret.Distributions.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class SDTrimSP extends ParticleInMatterCalculator{

    ArrayList<String> dataPath;

    SDTrimSP(String directoryPath) {
        super(directoryPath);
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
                        projectileMaxEnergy = Double.parseDouble(someParameter.substring(0, someParameter.indexOf(",")).trim());
                    }

                    if (line.contains("alpha0")) {
                        projectileIncidentPolarAngle = Double.parseDouble(someParameter);
                        projectileIncidentAzimuthAngle = 0;
                    }
                    if (line.contains("nh")) projectileAmount = Integer.parseInt(someParameter);
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

        float en = 0, cosx, cosy, cosz;
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
                else if (particlesType.contains("stop")) sort = "I";
                else if (particlesType.contains("tran")) sort = "T";

            while (br.ready()) {
                line = br.readLine();
                if (!line.contains("end")) {

                   // line.replaceAll("\\s+","\t");
                    line.replaceAll("\\\\u0020'", " ");
                  //line.replaceAll("\\u0020\\u0020"," ");
                 //  line.replaceAll("[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]", "");

                    System.out.println(line);
                    datas = line.split(" ");

                    for (int i=0; i<datas.length;i++) System.out.println(i+"  "+datas[i]);


                    en = Float.parseFloat(datas[27]);
                    cosx = Float.parseFloat(datas[33]);
                    cosy = Float.parseFloat(datas[35]);
                    try{
                        cosz = Float.parseFloat(datas[37]);
                    }
                    catch (Exception e)
                    {
                        cosz = Float.parseFloat(datas[38]);
                    }


                    //Here is several spectra calculators

                    for (Distribution distr : distributions) {
                        switch (distr.getType()) {
                            case "energy":
                                ((Energy) distr).check(cosx, cosy, cosz, sort, en);
                                break;
                            case "polar":
                                ((Polar) distr).check(cosx, cosy, cosz, sort);
                                break;
                            case "anglemap":
                                ((AngleMap) distr).check(cosx, cosy, cosz, sort);
                                break;
                            case "gettxt":
                                ((getTXT) distr).check(cosx, cosy, cosz, sort, en);
                        }
                    }

                    //calculate some scattering constants
                    particleCount++;

                    if (sort.equals("B")) scattered++;
                    else if (sort.equals("S")) sputtered++;
                    else if (sort.equals("I")) implanted++;
                }
            }
                br.close();
            }

            time=System.currentTimeMillis()-time;
            time=time/1000;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
