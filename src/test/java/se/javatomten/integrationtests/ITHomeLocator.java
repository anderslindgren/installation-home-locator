package se.javatomten.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import se.javatomten.homelocator.HomeLocator;

public class ITHomeLocator {

    @Test
    public void locateHomeWhereNoRelativeGiven() throws IOException {
        final File tHomeLocation = HomeLocator.getHomeLocation();
        final String tExpectedLocation = new File("target").getCanonicalPath();
        assertEquals("Home location not found", tExpectedLocation, tHomeLocation.getPath());
    }
    
    @Test
    public void locateHomeWhereRelativeIsOneLevelUp() throws IOException {
        final String tRelativePath = "..";
        final File tHomeLocation = HomeLocator.getHomeLocation(tRelativePath);
        final String tExpectedLocation = new File(".").getCanonicalPath();
        assertEquals("Home location not found", tExpectedLocation, tHomeLocation.getPath());
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
    public void absolutePathIsNotAllowed() throws IOException {
        final char tSep = File.separatorChar;
        final String tAbsolutePath;
        if (tSep == '/') {
            tAbsolutePath = "/home/sweet/home"; // On Unix
        }
        else {
            tAbsolutePath = "C:/home/sweet/home"; // On Windows
        }

        final File tHomeLocation = HomeLocator.getHomeLocation(tAbsolutePath);
        fail("Got an unexpected result back: " + tHomeLocation);
    }
    
}
