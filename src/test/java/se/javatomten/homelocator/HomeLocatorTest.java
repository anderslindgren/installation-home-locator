package se.javatomten.homelocator;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HomeLocatorTest {

    private HomeLocator mHomeLocator;

    @Before
    public void setup() {
        mHomeLocator = new HomeLocator();
    }

    @Test
    public void locateHomeWhereRelativeTwoLevelsUp() throws IOException {
        mHomeLocator.setRelativePath("../..");
        final File tHomeLocation = mHomeLocator.getLocation();
        final File tExpectedLocation = new File(".").getCanonicalFile();
        assertEquals("Could not find expected location", tExpectedLocation, tHomeLocation);
    }
    
    @Test
    public void locateHomeWhereRelativeTwoLevelsUpUsingStringConstructor() throws IOException {
        HomeLocator tHomeLocator = new HomeLocator("../..");
        final File tHomeLocation = tHomeLocator.getLocation();
        final File tExpectedLocation = new File(".").getCanonicalFile();
        assertEquals("Could not find expected location", tExpectedLocation, tHomeLocation);
    }

    @Test
    public void locateHomeWhereRelativeTwoLevelsUpUsingFileConstructor() throws IOException {
        HomeLocator tHomeLocator = new HomeLocator(new File("../.."));
        final File tHomeLocation = tHomeLocator.getLocation();
        final File tExpectedLocation = new File(".").getCanonicalFile();
        assertEquals("Could not find expected location", tExpectedLocation, tHomeLocation);
    }

    @Test
    public void locateHomeWhereNoRelativeIsGiven() throws IOException {
        final File tHomeLocation = mHomeLocator.getLocation();
        final File tExpectedLocation = new File("target/classes").getCanonicalFile();
        assertEquals("Could not find expected location", tExpectedLocation, tHomeLocation);
    }

    @Test(expected = RelativeLocationNotSetException.class)
    public void getRelativePathWhenNonHasBeenGiven() {
        mHomeLocator.getRelativePath();
    }
    
    @Test
    public void getRelativePathTwoLevelsUp() {
        HomeLocator tHomeLocator = new HomeLocator("../..");
        File tRelativePath = tHomeLocator.getRelativePath();
        assertEquals("Relative File wrong", new File("../.."), tRelativePath);
    }

    @Test(expected = RelativeLocationNotSetException.class)
    public void unsetRelativePath() {
        HomeLocator tHomeLocator = new HomeLocator("../..");
        File tRelativePath = tHomeLocator.getRelativePath();
        assertEquals("Relative File wrong", new File("../.."), tRelativePath);
        tHomeLocator.unsetRelativePath();
        tHomeLocator.getRelativePath();
    }

    @Test(expected=IllegalArgumentException.class)
    public void nonExistingRelativePathIsNotAllowed() throws IOException {
        mHomeLocator.setRelativePath("../garble");
        final File tLocation = mHomeLocator.getLocation();
        fail("Non existing directory is not allowed: " + tLocation.getPath());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void relativePathPointingToFileIsNotAllowed() throws IOException {
        mHomeLocator.setRelativePath("README");
        final File tLocation = mHomeLocator.getLocation();
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
        mHomeLocator.setRelativePath(tTestFile);
        mHomeLocator.getLocation();
    }

}
