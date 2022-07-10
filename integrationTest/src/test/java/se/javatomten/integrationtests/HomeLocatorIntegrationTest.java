package se.javatomten.integrationtests;

import org.junit.jupiter.api.Test;
import se.javatomten.homelocator.HomeLocator;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests to be run with the HomeLocator class in a jar file.
 */
public class HomeLocatorIntegrationTest {

    public static final String LOCAL_REPO = "/.m2/repository/se/javatomten/installation-home-locator/home-locator";

    @Test
    public void locateHomeWhereNoRelativeGiven() throws IOException {
        final HomeLocator locator = new HomeLocator();
        final File homeLocation = locator.getLocation();
        final String expectedLocation = new File("../source/target").getCanonicalPath();
        final String expectedLocation2 = new File("../source/target/classes").getCanonicalPath();
        final String userHome = System.getProperty("user.home");
        final String expectedLocation3 = new File(userHome + LOCAL_REPO + "/1.0.0").getCanonicalPath();
        String locationPath = homeLocation.getPath();
        assertThat("Home location not found", locationPath,
                anyOf(equalTo(expectedLocation), equalTo(expectedLocation2), equalTo(expectedLocation3)));
    }
    
    @Test
    public void locateHomeWhereRelativeIsOneLevelUp() throws IOException {
        final String relativePath = "..";
        final HomeLocator locator = new HomeLocator(relativePath);
        final File homeLocation = locator.getLocation();
        final String expectedLocation = new File("../source").getCanonicalPath();
        final String expectedLocation2 = new File("../source/target").getCanonicalPath();
        final String userHome = System.getProperty("user.home");
        final String expectedLocation3 = new File(userHome + LOCAL_REPO).getCanonicalPath();
        String locationPath = homeLocation.getPath();
        assertThat("Home location not found", locationPath,
                anyOf(equalTo(expectedLocation), equalTo(expectedLocation2), equalTo(expectedLocation3)));
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
        final char separatorChar = File.separatorChar;
        final String absolutePath;
        if (separatorChar == '/') {
            absolutePath = "/home/sweet/home"; // On Unix
        } else {
            absolutePath = "C:/home/sweet/home"; // On Windows
        }

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> new HomeLocator(absolutePath));

        assertThat("Missing exception: Relative path can't be absolute",
                illegalArgumentException, notNullValue());
    }
}
