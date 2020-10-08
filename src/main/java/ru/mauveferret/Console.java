package ru.mauveferret;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.mauveferret.Distributions.Distribution;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class Console {

    String fullpath;
    private double deltaAngle, basePhi, mapsize, deltaPosition;
    private String cartesianMapType;

    public Console(String args[]) {
        try {

            File configFile = getConfigFile(args);

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile);
            doc.getDocumentElement().normalize(); //to start from the beginning

            NodeList root =  doc.getFirstChild().getChildNodes();

            deltaAngle = 5;
            basePhi = 0;
            mapsize = 2000;
            deltaPosition = 10;
            cartesianMapType = "ZY";
            getParams(root.item(0).getChildNodes());

            NodeList calcs = root.item(1).getChildNodes();

            System.out.println("Starting postprocessing of files...");
            for (int i=0; i<calcs.getLength(); i++){
                System.out.println("___________________");
                System.out.println("PROGRESS: "+i*(100/calcs.getLength())+" %");
                System.out.println("-------------------");
                runCalc((Element) calcs.item(i));
            }

            System.out.println("___________________");
            System.out.println("PROGRESS: 100 %");
            System.out.println("-------------------");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    private File getConfigFile(String args[]){

        ClassLoader loader = Console.class.getClassLoader();
         fullpath = ""+loader.getResource("ru.mauveferret/Console.class");
        System.out.println(fullpath);

        // ISInCa -c /case/H_W
        if (args.length>1)
        {
            if (args[0].contains("c"))
            {
                String configPath = args[1];
                File configFile = new File(configPath);
                if (configFile.exists()) return configFile;
                else {
                    configPath = fullpath+configPath;
                    System.out.println(configPath);
                     configFile = new File(configPath);
                     if (configFile.exists()) return configFile;
                     else {
                         System.out.println("ERROR: wrong path");
                         System.exit(-1);
                     }
                }
                //File configFile = new File(configPath);

                //check if the config file exists, otherwise exiting
                if (!configFile.exists()) {
                    System.out.println("ERROR: File " + configPath + " doesn't exist! Turning off...");
                    System.exit(-1);
                }
            }
            else {
                System.out.println("ERROR: wrong parameter: "+args[0]);
                System.exit(-1);
            }
        }
        else {
            System.out.println("ERROR: no paths were found");
            System.exit(-1);
        }
        System.out.println("ZHOPPA");
        return null;
    }

    private void getParams(NodeList baseParams){
        for (int i=0; i<baseParams.getLength();i++){

            switch (baseParams.item(i).getNodeName().toLowerCase()){
                case "deltaangle": deltaAngle=Double.parseDouble(baseParams.item(i).getTextContent());
                    break;
                case "basephi": basePhi = Double.parseDouble(baseParams.item(i).getTextContent());
                    break;
                case "mapsize": mapsize =Double.parseDouble(baseParams.item(i).getTextContent());
                    break;
                case "deltaposition": deltaPosition=Double.parseDouble(baseParams.item(i).getTextContent());
                    break;
                case "cartesianmaptype": cartesianMapType = baseParams.item(i).getTextContent();
                    break;
            }
        }
    }

    private boolean runCalc(Element calc){
        String calcType = "not found";
        String dir = calc.getElementsByTagName("dir").item(0).getTextContent();
        dir = fullpath+dir;
        ParticleInMatterCalculator yourCalculator = new Scatter(dir,true);
        String initialize = yourCalculator.initializeVariables();
        if (!initialize.equals("OK")) {
            yourCalculator = new TRIM(dir,true);
            initialize = yourCalculator.initializeVariables();
            if (!initialize.equals("OK")) {
                yourCalculator = new SDTrimSP(dir, true);
                initialize = yourCalculator.initializeVariables();
                if (!initialize.equals("OK")) {
                    System.out.println("ERROR: wrong path:"+ dir);
                } else calcType = "SDTrimSP";
            } else calcType = "TRIM";
        } else calcType = "SCATTER";

        ArrayList<Distribution> distributions = new ArrayList<>();

        NodeList distrs = calc.getChildNodes();
        for (int i=0; i<distrs.getLength();i++){
            switch (distrs.item(0).getNodeName()){
                case "N_E":{

                }
            }
        }
        return true;
    }
}
