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
        final File homeLocation = mHomeLocator.getLocation();
        final File expectedLocation = new File(".").getCanonicalFile();
        assertEquals("Could not find expected location", expectedLocation, homeLocation);
    }
    
    @Test
    public void locateHomeWhereRelativeTwoLevelsUpUsingStringConstructor() throws IOException {
        HomeLocator homeLocator = new HomeLocator("../..");
        final File homeLocation = homeLocator.getLocation();
        final File expectedLocation = new File(".").getCanonicalFile();
        assertEquals("Could not find expected location", expectedLocation, homeLocation);
    }

    @Test
    public void locateHomeWhereRelativeTwoLevelsUpUsingFileConstructor() throws IOException {
        HomeLocator homeLocator = new HomeLocator(new File("../.."));
        final File homeLocation = homeLocator.getLocation();
        final File expectedLocation = new File(".").getCanonicalFile();
        assertEquals("Could not find expected location", expectedLocation, homeLocation);
    }

    @Test
    public void locateHomeWhereNoRelativeIsGiven() throws IOException {
        final File homeLocation = mHomeLocator.getLocation();
        final File expectedLocation = new File("target/classes").getCanonicalFile();
        assertEquals("Could not find expected location", expectedLocation, homeLocation);
    }

    @Test(expected = RelativeLocationNotSetException.class)
    public void getRelativePathWhenNonHasBeenGiven() {
        mHomeLocator.getRelativePath();
    }
    
    @Test
    public void getRelativePathTwoLevelsUp() {
        HomeLocator homeLocator = new HomeLocator("../..");
        File tRelativePath = homeLocator.getRelativePath();
        assertEquals("Relative File wrong", new File("../.."), tRelativePath);
    }

    @Test(expected = RelativeLocationNotSetException.class)
    public void unsetRelativePath() {
        HomeLocator homeLocator = new HomeLocator("../..");
        File tRelativePath = homeLocator.getRelativePath();
        assertEquals("Relative File wrong", new File("../.."), tRelativePath);
        homeLocator.unsetRelativePath();
        homeLocator.getRelativePath();
    }

    @Test(expected=IllegalArgumentException.class)
    public void nonExistingRelativePathIsNotAllowed() {
        mHomeLocator.setRelativePath("../garble");
        final File tLocation = mHomeLocator.getLocation();
        fail("Non existing directory is not allowed: " + tLocation.getPath());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void relativePathPointingToFileIsNotAllowed() {
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
