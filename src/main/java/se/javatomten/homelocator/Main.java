package se.javatomten.homelocator;

import java.io.File;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final File tHomeLocation = HomeLocator.getHomeLocation(new File(args[0]));
        if (args[1].equals(tHomeLocation)) {
            System.exit(0);
        }
        System.exit(1);
    }

}
