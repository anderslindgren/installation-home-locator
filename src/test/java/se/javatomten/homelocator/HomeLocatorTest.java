package se.javatomten.homelocator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class HomeLocatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void locateFileOnDiskRelativeTwoLevelsUp() throws IOException {
        File tLocation = HomeLocator.getHomeLocation("../..");
        assertEquals(tLocation, new File(".").getCanonicalFile());
    }
    
    @Test
    public void locateFileOnDiskRelativeIsNull() throws IOException {
        File tLocation = HomeLocator.getHomeLocation(null);
        assertEquals(tLocation, new File("target/classes").getCanonicalFile());
    }
    
    @Test
    public void locateFileOnDiskRelativeIsNonExisting() throws IOException {
        File tLocation = HomeLocator.getHomeLocation("../putte");
        assertEquals(tLocation, new File("target/putte").getCanonicalFile());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void onlyRelativePathAllowed() {
        char tSep = File.separatorChar;
        String tTestFile;
        if (tSep == '/') {
            tTestFile = "/home/sweet/home"; // On Unix
        }
        else {
            tTestFile = "C:/home/sweet/home"; // On Windows
        }
        HomeLocator.getHomeLocation(tTestFile);
    }

}
