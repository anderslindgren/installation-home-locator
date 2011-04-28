package se.javatomten.integrationtests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import se.javatomten.homelocator.HomeLocator;

public class ITHomeLocator {
    @Before
    public void setUp() throws Exception {
    	System.out.println(System.getProperty("java.class.path"));
    }

    @Test
    public void locateFileOnDiskRelativeOneLevelUp() throws IOException {
        File tLocation = HomeLocator.getHomeLocation("..");
        assertEquals(new File(".").getCanonicalPath(), tLocation.getPath());
    }
    
    @Test
    public void locateFileOnDiskNullRelative() throws IOException {
    	File tLocation = HomeLocator.getHomeLocation(null);
    	assertEquals(new File("target").getCanonicalPath(), tLocation.getPath());
    }
    

//    @Test
//    public void locateFileOnDiskRelativeTwoLevelsUp() throws Exception {
//        assertEquals(0, execute(new String[] {"../..", new File(".").getCanonicalPath()}));
//    }

//    private int execute(String[] args) throws Exception {
//        File jar = new File("target/installation-home-locator-0.0.1-SNAPSHOT.jar");
//
//        String[] execArgs = new String[args.length + 4];
//        execArgs[0] = "java";
//        execArgs[1] = "-cp";
//        execArgs[2] = jar.getCanonicalPath();
//        execArgs[3] = "se.javatomten.homelocator.Main";
//        System.arraycopy(args, 0, execArgs, 4, args.length);
//        System.out.println(Arrays.asList(execArgs).toString());
//        Process p = Runtime.getRuntime().exec(execArgs);
//        p.waitFor();
//        return p.exitValue();
//    }
}
