package se.javatomten.integrationtests;

import org.junit.jupiter.api.Test;
import se.javatomten.homelocator.HomeLocator;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests to be run with the HomeLocator class in a jar file.
 */
public class HomeLocatorIntegrationTest {

    public static final String LOCAL_REPO = "/.m2/repository/se/javatomten/installation-home-locator/home-locator";

    @Test
    public void locateHomeWhereNoRelativeGiven() {
        final HomeLocator locator = new HomeLocator();
        final Path homeLocation = locator.getLocation();
        final String expectedLocation = Path.of("../source/target").toAbsolutePath().normalize().toString();
        final String expectedLocation2 = Path.of("../source/target/classes").toAbsolutePath().normalize().toString();
        final String userHome = System.getProperty("user.home");
        final String expectedLocation3 = Path.of(userHome + LOCAL_REPO + "/1.0.0").toAbsolutePath().normalize().toString();
        String locationPath = homeLocation.toString();
        assertThat("Home location not found", locationPath,
                anyOf(equalTo(expectedLocation), equalTo(expectedLocation2), equalTo(expectedLocation3)));
    }

    @Test
    public void locateHomeWhereRelativeIsOneLevelUp() {
        final String relativePath = "..";
        final HomeLocator locator = new HomeLocator(relativePath);
        final Path homeLocation = locator.getLocation();
        final String expectedLocation = Path.of("../source").toAbsolutePath().normalize().toString();
        final String expectedLocation2 = Path.of("../source/target").toAbsolutePath().normalize().toString();
        final String userHome = System.getProperty("user.home");
        final String expectedLocation3 = Path.of(userHome + LOCAL_REPO).toAbsolutePath().normalize().toString();
        String locationPath = homeLocation.toString();
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
        final String separator = FileSystems.getDefault().getSeparator();
        final String absolutePath;
        if ("/".equals(separator)) {
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
