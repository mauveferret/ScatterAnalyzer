import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) throws  Exception {


        float sort=-1, en=0, cosx, cosy,cosz;
        BufferedReader br = new BufferedReader(new FileReader("BACKSCAT.txt"));
        //rubbish lines
        for (int i = 0; i < 9; i++) br.readLine();
        //find out the initial energy
        String line=br.readLine();
        int initialEnergy=Integer.parseInt(line.substring(line.indexOf("(")+1,line.indexOf("keV")-1))*1000;
        System.out.println(initialEnergy);
        //two rubbish lines must be excluded
        br.readLine();
        br.readLine();
        //now lets sort
        while (br.ready())
        {
            line=br.readLine();
            if (line.startsWith("B")) sort=-1;
            else if (line.startsWith("S")) sort=0;
            else if (line.startsWith("T")) sort=1;
            line=line.substring(line.indexOf(","));
            en=Float.parseFloat(line.substring(0,line.indexOf(" ")).replace(",","0."));
            //find "cosz" column
            if (line.endsWith(" ")) line=line.substring(0,line.length()-1);
            cosz=Float.parseFloat(line.substring(line.lastIndexOf(" ")+1).replace(",","0."));
            //find "cosy" column
            line=line.substring(0,line.lastIndexOf(" "));
            if (line.endsWith(" ")) line=line.substring(0,line.length()-1);
            cosy=Float.parseFloat(line.substring(line.lastIndexOf(" ")+1).replace(",","0."));
            //find "cosx" column
            line=line.substring(0,line.lastIndexOf(" "));
            if (line.endsWith(" ")) line=line.substring(0,line.length()-1);
            cosx=Float.parseFloat(line.substring(line.lastIndexOf(" ")+1).replace(",","0."));

            //отвергаем "оборванную" строку
            if (cosx>1 || cosy>1 || cosz >1)
            {
                cosx=0;
                cosy=0;
                cosz=0;  //одна битая точка никому не повредит)
            }
            System.out.println(sort+" "+" "+en+" "+cosx+" "+cosy+" "+cosz);
        }
        br.close();

    }
}
