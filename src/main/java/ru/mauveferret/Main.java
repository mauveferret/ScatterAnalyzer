package ru.mauveferret;

public class Main  {

    public static void main(String[] args) {

        if (args.length>0)
            new Console(args);
        else  GUI.main(args);
    }

}
