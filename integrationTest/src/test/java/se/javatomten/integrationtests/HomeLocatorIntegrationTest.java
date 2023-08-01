package se.javatomten.integrationtests;

import org.junit.jupiter.api.Test;
import se.javatomten.homelocator.HomeLocator;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatPath;

/**
 * Tests to be run with the HomeLocator class in a jar file.
 */
class HomeLocatorIntegrationTest {

    private static final String LOCAL_REPO = "/.m2/repository/se/javatomten/installation-home-locator/home-locator";

    @Test
    void locateHomeWhereNoRelativeGiven() {
        final HomeLocator locator = new HomeLocator();
        final Path homeLocation = locator.getLocation();
        final Path expectedLocation = Path.of("../source/target").toAbsolutePath().normalize();
        final Path expectedLocation2 = Path.of("../source/target/classes").toAbsolutePath().normalize();
        final String userHome = System.getProperty("user.home");
        final Path expectedLocation3 = Path.of(userHome + LOCAL_REPO + "/1.0.0").toAbsolutePath().normalize();
        assertThatPath(homeLocation)
                .isIn(expectedLocation, expectedLocation2, expectedLocation3);
    }

    @Test
    void locateHomeWhereRelativeIsOneLevelUp() {
        final String relativePath = "..";
        final HomeLocator locator = new HomeLocator(relativePath);
        final Path homeLocation = locator.getLocation();
        final Path expectedLocation = Path.of("../source").toAbsolutePath().normalize();
        final Path expectedLocation2 = Path.of("../source/target").toAbsolutePath().normalize();
        final String userHome = System.getProperty("user.home");
        final Path expectedLocation3 = Path.of(userHome + LOCAL_REPO).toAbsolutePath().normalize();
        assertThatPath(homeLocation)
                .isIn(expectedLocation, expectedLocation2, expectedLocation3);
    }

    @Test
    void nonExistingRelativePathIsNotAllowed() {
        final HomeLocator locator = new HomeLocator("../garble");

        assertThatIllegalArgumentException()
                .isThrownBy(locator::getLocation)
                .as("Message must reflect relative path is not aan existing directory")
                .withMessageStartingWith("Relative path pointing to non-existing directory:");
    }

    @Test
    void relativePathPointingToFileIsNotAllowed() {
        HomeLocator homeLocator = new HomeLocator("README.md", HomeLocatorIntegrationTest.class);
        assertThatIllegalArgumentException()
                .isThrownBy(homeLocator::getLocation)
                .as("Message must reflect relative path is not a directory")
                .withMessageStartingWith("Relative path is not a directory:");
    }

    @Test
    void absolutePathIsNotAllowed() {
        final String separator = FileSystems.getDefault().getSeparator();
        final String absolutePath;
        if ("/".equals(separator)) {
            absolutePath = "/home/sweet/home"; // On Unix
        } else {
            absolutePath = "C:/home/sweet/home"; // On Windows
        }

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HomeLocator(absolutePath))
                .as("Message must reflect absolute path")
                .withMessageStartingWith("The parameter relativePath can not be an absolute path:");
    }
}
