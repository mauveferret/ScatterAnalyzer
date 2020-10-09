package ru.mauveferret;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.mauveferret.Distributions.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class Console {

    String fullpath;
    File configFile;
    //params
    private double thetaNE, phiNE, phiNTheta, deltaNE, deltaPhiNE, deltaThetaNE, deltaNTheta, deltaPolarMap, deltaCartesianMap, MapSize;
    private String  sortNE, sortNTheta, sortPolarMap, sortCartesianMap, cartesianMapType;
    //Preferences
    private boolean getTXT, getSumary;

    public Console(String args[]) {
        try {

            System.out.println("Hello, world!");

            thetaNE = 70;
            phiNE = 0;
            phiNTheta = 0;
            deltaNE = 100;
            deltaPhiNE = 5;
            deltaThetaNE = 5;
            deltaNTheta = 5;
            deltaPolarMap = 5;
            deltaCartesianMap = 10;
            MapSize = 1000;


            configFile = getConfigFile(args);

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile);
            doc.getDocumentElement().normalize(); //to start from the beginning

            XPathFactory xpathFactory = XPathFactory.newInstance();
            // XPath to find empty text nodes.
            XPathExpression xpathExp = xpathFactory.newXPath().compile(
                    "//text()[normalize-space(.) = '']");
            NodeList emptyTextNodes = (NodeList)
                    xpathExp.evaluate(doc, XPathConstants.NODESET);


            // Remove each empty text node from document.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }

            NodeList root =  doc.getFirstChild().getChildNodes();

            getPref(root.item(0).getChildNodes());

            //getParams(root.item(1).getChildNodes());

            NodeList calcs = root.item(1).getChildNodes();

            System.out.println("Starting postprocessing of files...");
            ArrayList<Thread> threads = new ArrayList<>();

            //create threads
            for (int i=0; i<calcs.getLength(); i++) {
                try {
                    threads.add(runCalc( calcs.item(i), calcs.item(0)));
                }
                catch (Exception e){e.printStackTrace();}
            }

            //run threads
            for (Thread thread: threads){
                thread.start();
                System.out.println("Thread "+thread.getName()+" is STARTED");
            }


            int i=1;
            long start = System.currentTimeMillis();
            for (Thread thread: threads){
                thread.join();
                System.out.println("___________________");
                System.out.println("PROGRESS: "+i*100/threads.size()+"%");
                System.out.println("-------------------");
                System.out.println("time for "+thread.getName()+" : "+(System.currentTimeMillis()-start)/60000+" min");
                System.out.println("----------------------------------");
                i++;
            }
            System.out.println();
            System.out.println("Finished! Good bye, my lord :)");

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    private File getConfigFile(String args[]){


        //it returns *jar path, but we need it directory (parent)
         fullpath = ""+Console.class.getProtectionDomain().getCodeSource().getLocation().getPath();
         File file = new File(fullpath);
         fullpath = file.getParentFile().getAbsolutePath();

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
                    System.out.println("config path: "+configPath);
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

    private void getPref(NodeList prefs){
        for (int i=0; i<prefs.getLength(); i++){
            switch (prefs.item(i).getNodeName().toLowerCase()){
                case "gettxt":  getTXT = prefs.item(i).getTextContent().equals("true");
                break;
                case "getsummary": getSumary = prefs.item(i).getTextContent().equals("true");
                break;
            }
        }
    }




    private Thread runCalc(Node calc, Node zeroCalc) {

        String calcType = "not found";
        String dir = calc.getChildNodes().item(0).getTextContent();
        dir = fullpath + dir;
        ParticleInMatterCalculator yourCalculator = new Scatter(dir, false);
        String initialize = yourCalculator.initializeVariables();
        if (!initialize.equals("OK")) {
            yourCalculator = new TRIM(dir, false);
            initialize = yourCalculator.initializeVariables();
            if (!initialize.equals("OK")) {
                yourCalculator = new SDTrimSP(dir, false);
                initialize = yourCalculator.initializeVariables();
                if (!initialize.equals("OK")) {
                    System.out.println("ERROR: wrong path:" + dir);
                } else calcType = "SDTrimSP";
            } else calcType = "TRIM";
        } else calcType = "SCATTER";

        System.out.println("**********************************************************");
        System.out.println("Thread: "+calc.getChildNodes().item(0).getTextContent()+" calc type "+calcType);
        System.out.println("***********************************************************");

        ArrayList<Distribution> distributions = new ArrayList<>();


        NodeList distrs = (calc.getChildNodes().getLength()>1) ? calc.getChildNodes() : zeroCalc.getChildNodes();

            for (int i = 0; i < distrs.getLength(); i++) {

                NodeList distrPars = distrs.item(i).getChildNodes();

                switch (distrs.item(i).getNodeName()) {

                    case "N_E": {
                        deltaNE = yourCalculator.projectileMaxEnergy / 100;

                        for (int j = 0; j < distrPars.getLength(); j++) {
                            switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                case "sort":
                                    sortNE = distrPars.item(j).getTextContent();
                                    break;
                                case "theta":
                                    thetaNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                                case "phi":
                                    phiNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                                case "delta":
                                    deltaNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                                case "deltatheta":
                                    deltaThetaNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                                case "deltaphi":
                                    deltaPhiNE = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                            }
                        }
                        distributions.add(new Energy(deltaNE, phiNE, deltaPhiNE, thetaNE, deltaThetaNE, sortNE, yourCalculator));
                    }
                    break;
                    case "N_Theta": {
                        for (int j = 0; j < distrPars.getLength(); j++) {
                            switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                case "sort":
                                    sortNTheta = distrPars.item(j).getTextContent();
                                    break;
                                case "theta":
                                    deltaNTheta = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                                case "phi":
                                    phiNTheta = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                                case "delta":
                                    deltaNTheta = Double.parseDouble(distrPars.item(j).getTextContent());
                            }
                        }
                        distributions.add(new Polar(phiNTheta, deltaNTheta, deltaNTheta, sortNTheta, yourCalculator));
                    }
                    break;
                    case "polar_Map": {
                        for (int j = 0; j < distrPars.getLength(); j++) {
                            switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                case "sort":
                                    sortPolarMap = distrPars.item(j).getTextContent();
                                    break;
                                case "theta":
                                    deltaPolarMap = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                            }
                        }
                        distributions.add(new AngleMap(deltaPolarMap, deltaPolarMap, sortPolarMap, yourCalculator));
                    }
                    break;
                    case "cartesianmap": {
                        for (int j = 0; j < distrPars.getLength(); j++) {
                            switch (distrPars.item(j).getNodeName().toLowerCase()) {
                                case "sort":
                                    sortCartesianMap = distrPars.item(j).getTextContent();
                                    break;
                                case "mapsize":
                                    MapSize = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                                case "delta":
                                    deltaCartesianMap = Double.parseDouble(distrPars.item(j).getTextContent());
                                    break;
                                case "cartesianmaptype":
                                    cartesianMapType = distrPars.item(j).getTextContent();
                            }
                        }
                        distributions.add(new CartesianMap(deltaCartesianMap, cartesianMapType, sortCartesianMap, yourCalculator));
                    }
                    break;
                }

                if (getTXT) distributions.add(new getTXT(yourCalculator, ""));
            }

        final ParticleInMatterCalculator calculator = yourCalculator;
        Thread newCalculation = new Thread(() -> {
            calculator.postProcessCalculatedFiles(distributions);
            calculator.printAndVisualizeData(distributions);
        });
        newCalculation.setName(calc.getChildNodes().item(0).getTextContent());
        return newCalculation;
    }


}
