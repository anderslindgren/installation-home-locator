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
    	final HomeLocator locator = new HomeLocator();
        final File homeLocation = locator.getLocation();
        final String expectedLocation = new File("target").getCanonicalPath();
        assertEquals("Home location not found", expectedLocation, homeLocation.getPath());
    }
    
    @Test
    public void locateHomeWhereRelativeIsOneLevelUp() throws IOException {
        final String tRelativePath = "..";
    	final HomeLocator locator = new HomeLocator(tRelativePath);
        final File homeLocation = locator.getLocation();
        final String expectedLocation = new File(".").getCanonicalPath();
        assertEquals("Home location not found", expectedLocation, homeLocation.getPath());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void nonExistingRelativePathIsNotAllowed() {
    	final HomeLocator locator = new HomeLocator("../garble");
        final File tLocation = locator.getLocation();
        fail("Non existing directory is not allowed: " + tLocation.getPath());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void relativePathPointingToFileIsNotAllowed() {
    	final HomeLocator locator = new HomeLocator("README");
        final File tLocation = locator.getLocation();
        fail("Can't point to a file: " + tLocation.getPath());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void absolutePathIsNotAllowed() {
        final char tSep = File.separatorChar;
        final String tAbsolutePath;
        if (tSep == '/') {
            tAbsolutePath = "/home/sweet/home"; // On Unix
        }
        else {
            tAbsolutePath = "C:/home/sweet/home"; // On Windows
        }

    	final HomeLocator locator = new HomeLocator(tAbsolutePath);
        final File homeLocation = locator.getLocation();
        fail("Got an unexpected result back: " + homeLocation);
    }
    
}
