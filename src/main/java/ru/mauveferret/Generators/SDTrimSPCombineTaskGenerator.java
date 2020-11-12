package ru.mauveferret.Generators;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SDTrimSPCombineTaskGenerator {

    public SDTrimSPCombineTaskGenerator() {
        try {
            generate();
        }
        catch (Exception e){e.printStackTrace();}
    }

    private void generate() throws URISyntaxException {
        //String currentJar = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        //String currentDir = new File(currentJar).getParent();

        final String pathToWorkDir ="C:\\Users\\mauve\\Desktop\\test";
        final File workDir = new File( pathToWorkDir);


        File[] matchingDirs = workDir.listFiles((dir, name) ->
                ((name.contains("eV") & dir.isDirectory())));

        //FIXME not work
        //& ((new File(dir.getAbsolutePath()+File.separator+"tri.inp")).exists())

        if (!((matchingDirs != null ? matchingDirs.length : 0) > 0)) {
            System.out.println("no dirs files found");
            System.exit(0);
        }

        final int[] angles = {0,20,30,45,50,60,70,80,88};
        ArrayList<String> calcs = new ArrayList<>();

        for (File dir: matchingDirs){

            calcs.add(dir.getName());

            for (int angle: angles) {
                try {
                    int i= dir.getName().lastIndexOf("V")+1;
                    int j = dir.getName().lastIndexOf("d");
                    String newName = dir.getParent()+File.separator+dir.getName().substring(0,i)+angle+
                            dir.getName().substring(j);
                    String newFileName = newName.substring(newName.lastIndexOf(File.separator)+1);
                    System.out.println(dir.getName() +" -> "+newFileName);
                    FileUtils.copyDirectory(dir, new File(newName));

                    calcs.add(newFileName);

                    System.out.println("****************************************************");

                    List<String> fileContent = new ArrayList<>(Files.readAllLines( new File(newName+File.separator+"tri.inp").
                            toPath() , StandardCharsets.UTF_8));

                    // System.out.println(new File(newName+File.separator+"tri.inp").toPath()+"");

                    for (int n = 0; n < fileContent.size(); n++) {

                        //System.out.println(fileContent.get(n));

                        if (fileContent.get(n).contains("->")) {
                            String line = fileContent.get(n);
                            //fileContent.set(n, "new line");
                            line  = line.substring(0, (line.lastIndexOf("d") - 4 ));
                            line = line +" "+angle +" degrees";
                            //break;
                            fileContent.set(n, line);
                        }

                        if (fileContent.get(n).contains("alpha0")) {
                            String line = fileContent.get(n);
                            line = line.substring(line.indexOf("=")+1);
                            String afterEq = line;
                            if (line.contains(",")) line = line.substring(0, line.indexOf(","));
                            line = line.trim();
                            fileContent.set(n, "     alpha0 = "+afterEq.replaceAll(line, angle+""));
                            //break;
                        }
                    }

                    Files.write(new File(newName+File.separator+"tri.inp").
                            toPath() , fileContent, StandardCharsets.UTF_8);

                }
                catch (Exception ignored){System.out.println(ignored.getMessage());}
            }

        }


        //log additional dir names
        try {
            FileWriter calcFile = new FileWriter(pathToWorkDir + File.separator + "calcs.dat");
            for (String calc: calcs) calcFile.write("<dir>"+calc+"</dir>\n");
            calcFile.close();
        }

        catch (Exception e){e.printStackTrace();}

        //log sbatch file

        try {
            FileWriter bashFile = new FileWriter(pathToWorkDir + File.separator + "isinca.sh");
            bashFile.write("#!/bin/bash\n");
            bashFile.write("#\n");
            bashFile.write("#SBATCH --nodes=4\n");
            bashFile.write("#SBATCH --tasks-per-node=16\n");
            bashFile.write("#SBATCH --time=500:00:00\n");
            bashFile.write("cd ../../"+calcs.get(0)+"\n");
            bashFile.write("mpirun ../../bin/cherenkov_gcc.PPROJ/SDTrimSP.exe\n");
            for (int n=1; n<calcs.size(); n++){
                bashFile.write("cd ../"+calcs.get(n)+"\n");
                bashFile.write("mpirun ../../bin/cherenkov_gcc.PPROJ/SDTrimSP.exe\n");
            }
            bashFile.write("cd ..\n");
            bashFile.write("java -jar ISInCa.jar -c launches/H_Fe/isinca.xml\n");
            bashFile.close();

            /*
            for Win10 ubuntu

            #!/bin/bash
                exec 3>&1 4>&2
                trap 'exec 2>&4 1>&3' 0 1 2 3
                exec 1> >( tee -ia log.out) 2>&1


                cd ../../D,T_FeCrNiTi10keV10deg4750k
                mpirun -np 4  --oversubscribe  ../../bin/linux.PRO/SDTrimSP.exe
                cd ../D,T_FeCrNiTi10keV0deg4750k
                mpirun -np 4  --oversubscribe  ../../bin/linux.PRO/SDTrimSP.exe
                cd ../D,T_FeCrNiTi10keV20deg4750k
                mpirun -np 4  --oversubscribe  ../../bin/linux.PRO/SDTrimSP.exe
                cd ../D,T_FeCrNiTi10keV30deg4750k
                mpirun -np 4  --oversubscribe  ../../bin/linux.PRO/SDTrimSP.exe
                cd ../D,T_FeCrNiTi10keV45deg4750k
                mpirun -np 4  --oversubscribe  ../../bin/linux.PRO/SDTrimSP.exe
                cd ../D,T_FeCrNiTi10keV50deg4750k
                mpirun -np 4  --oversubscribe  ../../bin/linux.PRO/SDTrimSP.exe
                cd ../D,T_FeCrNiTi10keV60deg4750k
             */

        }
        catch (Exception e){e.printStackTrace();}

        //here we go again
        double partCountDiv1000 = 1000, energy = 20; //keV

    }
}
