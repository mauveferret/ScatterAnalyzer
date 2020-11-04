package ru.mauveferret;

public class Main  {

    public static void main(String[] args) {



        if (args.length>0)
        {
            try {
                switch (args[0]){
                    case "-c": new Console(args);
                    break;
                    case "-v": {
                        System.out.println("[ISInCa] Java Virtual Machine current version: "+
                                System.getProperty("java.version"));
                        System.out.println("[ISInCa] ISInCa current version: "+Main.getVersion());
                        System.out.println("[ISInCa] check updates at https://github.com/mauveferret/ISInCa ");
                    }
                    break;
                    case "-g": GUI.main(args);
                    break;
                }
            }
            catch (Exception e){
                System.out.println("wrong command! "+e.getMessage());
            }
        }
        else  GUI.main(args);
    }

    public static String getVersion(){
        String version = "2020.3.4";
        return  version;
    }

}
