package ru.mauveferret;

import java.io.File;

public class Console {
    File file;
    public Console(String[] args) {
        file = new File(args[0]);
        if (file.exists()) {
            //TODO
        }
        else{
            System.out.println("directory doesn't exist: "+args[0]);
            System.exit(-1);
        }
    }






}
