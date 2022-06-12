package se.javatomten.integrationtests;

import org.junit.jupiter.api.Test;
import se.javatomten.homelocator.HomeLocator;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests to be run with the HomeLocator class in a jar file.
 */
public class HomeLocatorIntegrationTest {
	
    @Test
    public void locateHomeWhereNoRelativeGiven() throws IOException {
    	final HomeLocator locator = new HomeLocator();
        final File homeLocation = locator.getLocation();
        final String expectedLocation = new File("target/classes").getCanonicalPath();
        assertThat("Home location not found", homeLocation.getPath(), equalTo(expectedLocation));
    }
    
    @Test
    public void locateHomeWhereRelativeIsOneLevelUp() throws IOException {
        final String tRelativePath = "..";
    	final HomeLocator locator = new HomeLocator(tRelativePath);
        final File homeLocation = locator.getLocation();
        final String expectedLocation = new File("target").getCanonicalPath();
        assertThat("Home location not found", homeLocation.getPath(), equalTo(expectedLocation));
    }
    
    @Test
    public void nonExistingRelativePathIsNotAllowed() {
    	final HomeLocator locator = new HomeLocator("../garble");

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, locator::getLocation);

        assertThat("Missing exception: Non existing directory is not allowed",
                illegalArgumentException, notNullValue());
    }
    
    @Test
    public void relativePathPointingToFileIsNotAllowed() {
    	final HomeLocator locator = new HomeLocator("README");

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, locator::getLocation);

        assertThat("Missing exception: Can't point to a file", illegalArgumentException, notNullValue());
    }
    
    @Test
    public void absolutePathIsNotAllowed() {
        final char tSep = File.separatorChar;
        final String tAbsolutePath;
        if (tSep == '/') {
            tAbsolutePath = "/home/sweet/home"; // On Unix
        }
        else {
            tAbsolutePath = "C:/home/sweet/home"; // On Windows
        }

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> new HomeLocator(tAbsolutePath));

        assertThat("Missing exception: Relative path can't be absolute",
                illegalArgumentException, notNullValue());

    }
    
}
