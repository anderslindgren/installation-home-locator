package se.javatomten.homelocator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class HomeLocatorTest {

    @Test
    public void locateHomeWhereRelativeTwoLevelsUp() throws IOException {
        final File tHomeLocation = HomeLocator.getHomeLocation("../..");
        final File tExpectedLocation = new File(".").getCanonicalFile();
        assertEquals("Could not find expected location", tExpectedLocation, tHomeLocation);
    }
    
    @Test
    public void locateHomeWhereNoRelativeIsGiven() throws IOException {
        final File tHomeLocation = HomeLocator.getHomeLocation();
        final File tExpectedLocation = new File("target/classes").getCanonicalFile();
        assertEquals("Could not find expected location", tExpectedLocation, tHomeLocation);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void nonExistingRelativePathIsNotAllowed() throws IOException {
        final File tLocation = HomeLocator.getHomeLocation("../garble");
        fail("Non existing directory is not allowed: " + tLocation.getPath());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void relativePathPointingToFileIsNotAllowed() throws IOException {
        final File tLocation = HomeLocator.getHomeLocation("README");
        fail("Can't point to a file: " + tLocation.getPath());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void absolutePathIsNotAllowed() {
        final char tSep = File.separatorChar;
        final String tTestFile;
        if (tSep == '/') {
            tTestFile = "/home/sweet/home"; // On Unix
        }
        else {
            tTestFile = "C:/home/sweet/home"; // On Windows
        }
        HomeLocator.getHomeLocation(tTestFile);
    }

}
