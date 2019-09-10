package ScatterAnalyzer;

import javafx.application.Platform;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Stack;


public class Analyzer  {

    private int  threadCount=6;
    private Stack<Thread> threadStack = new Stack<>();
    private Stack<Integer> filesPartsStack = new Stack<>();
    private long countLines, scatteredEnergy=0;
    private double thetaNE, dThetaNE, phiNE, dPhiNE; //for energy spectrum
    private int dE=6; //step for energy spectrum
    private double dThetaNTheta,phiNTheta,dPhiNTheta; //for polar spectrum
    private double NdThetaPhi=5, NThetadPhi=5; //for surface distribution
    private  boolean NER, NEY, NThetaR, NThetaY, NThetaPhiR, NThetaPhiY, getTXT; // buttons for calculating spectrum
    private boolean isScatter;
    private int energySpectrum[] = new int[50000];  //forms energyspectra
    private int polarAngleSpectrum[] = new int[50000]; // forms polar spectra
    private double SurfaceDistribution[][] = new double[1000][1000]; //forms surface spectra i - phi, j - theta
    private double count=0, scattered=0, projected=0, sputtered=0; //scattering constants
    private String allParticlesData;

    //i hate this shit and don't want to continue developing it, so now its pornsolutions time
    private int colorMapParticlesCount;


    int stringCountPerCycle; // amount of strings readed per cycle in ScatterLogStringsAnalyzer

    public double[] calculateSpectra(String file, int E0, int dE,  double thetaNE1, double dThetaNE1, double phiNE1, double dPhiNE1, int threadCount,
                                     double dThetaNTheta, double phiNTheta1, double dPhiNTheta1, boolean NER, boolean NEY,
                                     boolean NThetaR, boolean NThetaY, int StringCount, boolean NThetaPhiR, boolean NThetaPhiY, double NThetadPhi, double NdThetaPhi, boolean getTXT, boolean isScatter)
    {
        //start counting time
        long t=System.currentTimeMillis();
        try {
            stringCountPerCycle = StringCount;
            this.thetaNE =thetaNE1/57.2958;
            this.dThetaNE=dThetaNE1/57.2958;
            this.phiNE = phiNE1/57.2958;
            this.dPhiNE=dPhiNE1/57.2958;
            this.dE=dE;
            this.threadCount=threadCount;
            this.dThetaNTheta=dThetaNTheta;  // in degress not rads!
            this.phiNTheta=phiNTheta1/57.2958;
            this.dPhiNTheta = dPhiNTheta1/57.2958;
            this.NThetaPhiR=NThetaPhiR;
            this.NThetaPhiY=NThetaPhiY;
            this.NER = NER;
            this.NEY=NEY;
            this.NThetaR=NThetaR;
            this.NThetaY=NThetaY;
            this.NdThetaPhi=NdThetaPhi;
            this.NThetadPhi=NThetadPhi;
            this.getTXT=getTXT;
            countLines = this.CountLines(file);
            this.isScatter=isScatter;
            //create several threads for parallel file reading
            //it doesn't actually work, so thread is set to 1.
            for (int i=0;i<threadCount;i++) {
                filesPartsStack.push(threadCount-1-i);
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (getTXT) Analyzer.this.CreateTXT(file);
                         if (isScatter) Analyzer.this.ScatterLogStringsAnalyzer(file);
                         else Analyzer.this.TRIMLogStringsAnalyzer(file);
                    }
                });
                threadStack.push(thread);
                thread.start();
            }
            while (!threadStack.empty()) // wait while threads will end their job
            {
                threadStack.pop().join();
            }

            //logging energy spectrum
            String type = "";
            if (NER||NEY) {
                String pathToOutputFileForEnergySpectrum = file.substring(0, file.length() - 4);
                if (NER) type="R";
                if (NEY) type="Y";
                if (NER&&NEY) type="RY";
                pathToOutputFileForEnergySpectrum = pathToOutputFileForEnergySpectrum + "_ENERGY_E0_type_"+type+"_" + E0 + "_angle_" + thetaNE1 + "_resolution_" + dE + "_delta_" + dThetaNE1 + ".txt";
                FileOutputStream energyWriter = new FileOutputStream(pathToOutputFileForEnergySpectrum);

                for (int i = 0; i <= (int) Math.round(E0 / dE); i++) {   //log spectrum
                    String stroka = i * dE + " " + ((double) energySpectrum[i])/dE + "\n";
                    energyWriter.write(stroka.getBytes());
                }
                energyWriter.close();
            }

            //logging theta(phi) surface distribution of particles
            if (NThetaPhiR||NThetaPhiY)
            {
                String pathToOutputFileForEnergySpectrum = file.substring(0, file.length() - 4);

                pathToOutputFileForEnergySpectrum = pathToOutputFileForEnergySpectrum + "_SURFACE_" + "_dTheta_" + NdThetaPhi + "_dPhi_" + NThetadPhi +".txt";
                FileOutputStream surfaceWriter = new FileOutputStream(pathToOutputFileForEnergySpectrum);
                String stroka="Phi";
                for (int i = 0; i <=(int) Math.round(90 / NdThetaPhi); i++)
                {
                    stroka=stroka+" "+(int) (i*NdThetaPhi);
                }
                stroka=stroka+"\n";
                surfaceWriter.write(stroka.getBytes());
                double coefficientForColorMap=0;
                if (NThetaPhiR) coefficientForColorMap += scattered/colorMapParticlesCount;
                if (NThetaPhiY) coefficientForColorMap+=sputtered/colorMapParticlesCount;
                for (int i = 0; i <=(int) Math.round(360 / NThetadPhi); i++) {
                    stroka=(int) (i*NThetadPhi)+" ";
                    for (int j = 0; j <= (int) Math.round(90 / NdThetaPhi); j++) {
                        SurfaceDistribution[i][j] = SurfaceDistribution[i][j]*coefficientForColorMap;
                        if (i<(int) Math.round(360 / NdThetaPhi)) stroka = stroka + SurfaceDistribution[i][j] + " ";
                        else  stroka=stroka+SurfaceDistribution[i-1][j]+" ";
                    }
                    stroka = stroka + "\n";
                    surfaceWriter.write(stroka.getBytes());
                    stroka = "";
                }
                surfaceWriter.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new ScatterColorMap("ScatterAnalyzer", SurfaceDistribution, NThetadPhi, NdThetaPhi);
                    }
                });
            }
            //logging polar angle spectrum

            if (NThetaR||NThetaY) {

                if (NThetaR) type="R";
                if (NThetaY) type="Y";
                if (NThetaR&&NThetaY) type="RY";
                //File for logging polarAngle spectrum
                String pathToOutputFileForPolarSpectrum = file.substring(0, file.length() - 4);
                pathToOutputFileForPolarSpectrum = pathToOutputFileForPolarSpectrum + "_POLARANGLE_type_"+type+" " + "_phi_" + phiNTheta1 + "_deltaPhi_ "+dPhiNTheta1+ "_deltaTheta_" + dThetaNTheta + ".txt";
                FileOutputStream polarWriter = new FileOutputStream(pathToOutputFileForPolarSpectrum);

                for (int i = 0; i <= (int) Math.round(360 / dThetaNTheta); i++) {   //log spectrum

                    String stroka = i * dThetaNTheta + " " + polarAngleSpectrum[i] + "\n";
                    polarWriter.write(stroka.getBytes());
                }
                polarWriter.close();
            }

            //show some spectra

            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    if (NER||NEY) new Main().showGraph(energySpectrum, E0, dE, "Энергетический спектр, phi = "+phiNE1+" theta = "+thetaNE1);
                    if (NThetaR||NThetaY) new Main().showGraph(polarAngleSpectrum,360, dThetaNTheta, "угловой спектр, phi = "+phiNTheta1);
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        t=System.currentTimeMillis()-t;
        double[] returnArray = new double[6];
        returnArray[0]=t/((double) 1000);
        returnArray[1] = count;
        returnArray[2] = scattered;
        returnArray [3] = sputtered;
        returnArray [4] = projected;
        returnArray [5] = scatteredEnergy;
        return returnArray;
    }



    public long CountLines(String filename) {

        try {
            File fl = new File(filename);
            long t = fl.length();
            //System.out.println("length "+t/18);
            return t / 18;
        }
        catch (Exception e)
        {

        }
        return 0;
    }


    //creating *.txt file from *.dat
    private void  CreateTXT(String path) {
        try {

            float sort, en = 0, cosx, cosy, cosz;

                String pathToOutputFileForEnergySpectrum = path.substring(0, path.length() - 4);
                pathToOutputFileForEnergySpectrum = pathToOutputFileForEnergySpectrum + ".txt";
                FileOutputStream energyWriter = new FileOutputStream(pathToOutputFileForEnergySpectrum);


            DataInputStream reader = new DataInputStream(new FileInputStream(path));
            byte[] buf = new byte[stringCountPerCycle * 18];
            //int filePart = filesPartsStack.pop();
            // bytes+=(18* (int)(countLines/threadCount*filePart));
          //  reader.skipBytes(18 * (int) (countLines / threadCount * filePart));


            while (reader.available() > stringCountPerCycle * 18)  {

                reader.read(buf);
                int shift = 0; // movement through buf for 'stringCountPerCycle' times
                for (int j = 1; j <= stringCountPerCycle; j++) {
                    sort = buf[shift];
                    //second byte of buf has  unknown purpose

                    en = ByteBuffer.wrap(ArraySubPart(buf, 2 + shift, 5 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosx = ByteBuffer.wrap(ArraySubPart(buf, 6 + shift, 9 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosy = ByteBuffer.wrap(ArraySubPart(buf, 10 + shift, 13 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    cosz = ByteBuffer.wrap(ArraySubPart(buf, 14 + shift, 17 + shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    allParticlesData = (int) (sort) + " " + en + " " + cosx + " " + cosy + " " + cosz + "\n";
                    energyWriter.write(allParticlesData.getBytes());

                    shift += 18;
                }


            }
            energyWriter.close();
        }
        catch (Exception e)
        {

        }
    }
    private void  ScatterLogStringsAnalyzer(String path) {
        try {

            float sort, en=0, cosx, cosy,cosz;

            DataInputStream reader = new DataInputStream(new FileInputStream(path));
            byte[] buf = new byte[stringCountPerCycle*18];
            int filePart=filesPartsStack.pop();
            // bytes+=(18* (int)(countLines/threadCount*filePart));
            reader.skipBytes(18* (int)(countLines/threadCount*filePart));


            while (reader.available() > stringCountPerCycle*18*  (countLines*(threadCount-filePart-1)/threadCount)+17) {

                    reader.read(buf);
                    int shift =0; // movement through buf for 'stringCountPerCycle' times
                    for (int j=1;j<=stringCountPerCycle;j++) {
                       sort = buf[shift];
                       //second byte of buf has  unknown purpose

                        en = ByteBuffer.wrap(ArraySubPart(buf, 2+shift, 5+shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        cosx = ByteBuffer.wrap(ArraySubPart(buf, 6+shift, 9+shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        cosy = ByteBuffer.wrap(ArraySubPart(buf, 10+shift, 13+shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        cosz = ByteBuffer.wrap(ArraySubPart(buf, 14+shift, 17+shift)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        //allParticlesData+=sort+" "+en+" "+cosx+" "+cosy+" "+cosz+"\n";

                        //Here is several spectra calculators
                        if (NER||NEY) DoesTheParticleMatchToEnergySpectrum(en, sort,cosx,cosy,cosz);
                        if (NThetaY||NThetaR) DoesTheParticleMatchToPolarAngleSpectrum(en, sort,cosx,cosy,cosz);
                        if (NThetaPhiR || NThetaPhiY) DoesTheParticleMatchToSurfaceSpectrum(sort, cosx, cosy,cosz);
                        //calculate some scattering constants
                        count++;
                        if ((sort<-0.5)&&(cosz>0))
                        {
                            scattered++;
                            scatteredEnergy+=en;
                        }
                        if (cosz<0.0) projected++;
                        if ((sort>-0.5) && (cosz>0)) sputtered++;
                        shift+=18;
                    }


            }
        }

        catch (Exception e)
        {
            e.printStackTrace();

        }
    }

    private void  TRIMLogStringsAnalyzer(String path) {
        try {

            float sort = -1, en = 0, cosx, cosy, cosz;
            BufferedReader br = new BufferedReader(new FileReader(path));
            //rubbish lines
            String line=br.readLine();
            while (!line.contains("TRIM Calc.")) line=br.readLine();
            //two rubbish lines must be excluded
            br.readLine();
            br.readLine();
            //now lets sort

            while (br.ready()) {
                line = br.readLine();
                if (line.startsWith("B")) sort = -1;
                else if (line.startsWith("S")) sort = 0;
                else if (line.startsWith("T")) sort = 1;
                line=line.substring(line.indexOf(","));
                en=Float.parseFloat(line.substring(0,line.indexOf(" ")).replace(",","0."));
                //find "cosz" column
                if (line.endsWith(" ")) line = line.substring(0, line.length() - 1);
                cosy = Float.parseFloat(line.substring(line.lastIndexOf(" ") + 1).replace(",", "0."));
                //find "cosy" column
                line = line.substring(0, line.lastIndexOf(" "));
                if (line.endsWith(" ")) line = line.substring(0, line.length() - 1);
                cosx = Float.parseFloat(line.substring(line.lastIndexOf(" ") + 1).replace(",", "0."));
                //find "cosx" column
                line = line.substring(0, line.lastIndexOf(" "));
                if (line.endsWith(" ")) line = line.substring(0, line.length() - 1);
                cosz = -1* Float.parseFloat(line.substring(line.lastIndexOf(" ") + 1).replace(",", "0."));

                //отвергаем "оборванную" строку
                if (cosx > 1 || cosy > 1 || cosz > 1) {
                    cosx = 0;
                    cosy = 0;
                    cosz = 0;  //одна битая точка никому не повредит)
                }

                //Here is several spectra calculators
                if (NER || NEY) DoesTheParticleMatchToEnergySpectrum(en, sort, cosx, cosy, cosz);
                if (NThetaY || NThetaR) DoesTheParticleMatchToPolarAngleSpectrum(en, sort, cosx, cosy, cosz);
                if (NThetaPhiR || NThetaPhiY) DoesTheParticleMatchToSurfaceSpectrum(sort, cosx, cosy, cosz);
                //calculate some scattering constants
                count++;
                if ((sort < -0.5) && (cosz > 0)) {
                    scattered++;
                    scatteredEnergy += en;
                }
                if (cosz < 0.0) projected++;
                if ((sort > -0.5) && (cosz > 0)) sputtered++;

            }
            br.close();
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

    private  synchronized  void DoesTheParticleMatchToEnergySpectrum (float en, float sort, double x, double y, double z )
    {


        if (((x>0)&&(y>0&&(z>0))&&(Math.abs(Math.atan(y / x) - phiNE) < dPhiNE))||
                ((x>0)&&(y<0)&&(z>0)&&(Math.abs(-1*Math.atan(y / x) - phiNE) < dPhiNE))||
                ((x<0)&&(z>0)&&(Math.abs(3.14+Math.atan(y / x) - phiNE) < dPhiNE)))

        {
            if ((Math.abs(Math.atan(Math.sqrt(x * x + y * y) /z) - thetaNE) < dThetaNE) )
            {

                if ((NER && !NEY&&(sort<-0.5))||(!NER && NEY&&(sort>-0.5))||(NER&&NEY))
                    AddParticleToEnergySpectrum(en);
            }
        }


    }

    //calculate angle spectrum for theta angle
    private synchronized void DoesTheParticleMatchToPolarAngleSpectrum (float en, float sort,  double x, double y, double z )
    {

        if (((x>0)&&(y>0)&&((Math.abs(Math.atan(y / x) - phiNTheta) < dPhiNTheta)))||
                ((x<0)&&(y>0)&&((3.14+Math.abs(Math.atan(y / x) - phiNTheta) < dPhiNTheta))))
        {
            float local=0;
            if (z>=0)
                local= (float) (57.2958*Math.atan(z/Math.sqrt(x * x + y * y)));
            else
                local=(float) (360-57.2958*Math.atan(-1*z/Math.sqrt(x * x + y * y)));
            if ((NThetaR && !NThetaY&&(sort<-0.5))||(!NThetaR && NThetaY&&(sort>-0.5))||(NThetaR&&NThetaY))
                AddParticleToPolarAngleSpectrum( local);
        }

        if (((x<0)&&(y<0)&&((Math.abs(Math.atan(y / x) - phiNTheta) < dPhiNTheta)))||
                ((x>0)&&(y<0)&&((3.14+Math.abs(Math.atan(y / x) - phiNTheta) < dPhiNTheta))))
        {
            float local=0;
            if (z>=0)
                local= (float) (180-57.2958*Math.atan(z/Math.sqrt(x * x + y * y)));
            else
                local=(float) (180+57.2958*Math.atan(-1*z/Math.sqrt(x * x + y * y)));

            if ((NThetaR && !NThetaY&&(sort<-0.5))||(!NThetaR && NThetaY&&(sort>-0.5))||(NThetaR&&NThetaY))
                AddParticleToPolarAngleSpectrum( local);
        }
    }

    //calculate distribution of particles on phi(Theta) surface
    private synchronized void DoesTheParticleMatchToSurfaceSpectrum(double sort, double x, double y, double z)
    {
        double localTheta = 57.2958 * Math.atan(Math.sqrt(x * x + y * y) / Math.abs(z));
        if ((x >= 0) && (y > 0) && (z > 0))
        {
            if ((NThetaPhiR && !NThetaPhiY&&(sort<-0.5))||(!NThetaPhiR && NThetaPhiY&&(sort>-0.5))||(NThetaPhiR&&NThetaPhiY))
            {
                SurfaceDistribution[(int) ((57.2958 * Math.atan(y / x) / NThetadPhi))][(int) (Math.round(localTheta / NdThetaPhi))]++;
                colorMapParticlesCount++;
            }
        }
        if ((x < 0) && (z > 0))
        {
            if ((NThetaPhiR && !NThetaPhiY&&(sort<-0.5))||(!NThetaPhiR && NThetaPhiY&&(sort>-0.5))||(NThetaPhiR&&NThetaPhiY))
            {
                SurfaceDistribution[(int) (((180 + 57.2958 * Math.atan(y / x)) / NThetadPhi))][(int) (Math.round(localTheta / NdThetaPhi))]++;
                colorMapParticlesCount++;
            }
        }
        if ((x > 0) && (y < 0) && (z > 0))
        {
            if ((NThetaPhiR && !NThetaPhiY&&(sort<-0.5))||(!NThetaPhiR && NThetaPhiY&&(sort>-0.5))||(NThetaPhiR&&NThetaPhiY))
            {
                SurfaceDistribution[(int) ((360 + 57.2958 * Math.atan(y / x)) / NThetadPhi)][(int) (Math.round(localTheta / NdThetaPhi))]++;
                colorMapParticlesCount++;
            }
        }

    }

    //Energy spectrum logger
    public  synchronized  void  AddParticleToEnergySpectrum(float en1) {
        energySpectrum[Math.round(en1 / dE)]++;
    }
    //spectrum logger
    public  synchronized  void  AddParticleToPolarAngleSpectrum(float polarAngle) {
        //360- что бы за вылетевших отвечали углы 0-180
        //ты добавил int, может, не стоило?
        polarAngleSpectrum[Math.round((int) ((polarAngle) / (dThetaNTheta)))]++;
    }
}

