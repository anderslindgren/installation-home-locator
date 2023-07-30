package se.javatomten.homelocator;

import org.junit.jupiter.api.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HomeLocatorTest {

    private static final String TWO_LEVELS_UP = "../..";

    @Test
    void usePath() {
        final HomeLocator homeLocator = new HomeLocator();
        final Path path = Path.of(TWO_LEVELS_UP);
        homeLocator.setRelativePath(path);
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of(".").normalize().toAbsolutePath();
        assertThat(homeLocation).as("locations should match").isEqualTo(expectedLocation);
    }

    @Test
    void locateHomeWhereRelativeTwoLevelsUp() {
        final HomeLocator homeLocator = new HomeLocator();
        homeLocator.setRelativePath(TWO_LEVELS_UP);
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of(".").normalize().toAbsolutePath();
        assertThat(homeLocation).as("locations should match").isEqualTo(expectedLocation);
    }

    @Test
    void locateHomeWhereRelativeTwoLevelsUpUsingStringConstructor() {
        final HomeLocator homeLocator = new HomeLocator(TWO_LEVELS_UP);
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of(".").normalize().toAbsolutePath();
        assertThat(homeLocation).as("locations should match").isEqualTo(expectedLocation);
    }

    @Test
    void locateHomeWhereRelativeTwoLevelsUpUsingPathConstructor() {
        final HomeLocator homeLocator = new HomeLocator(Path.of(TWO_LEVELS_UP));
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of(".").normalize().toAbsolutePath();
        assertThat(homeLocation).as("locations should match").isEqualTo(expectedLocation);
    }

    @Test
    void locateHomeWhereNoRelativeIsGiven() {
        final HomeLocator homeLocator = new HomeLocator();
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of("target/classes").normalize().toAbsolutePath();
        assertThat(homeLocation).as("locations should match").isEqualTo(expectedLocation);
    }

    @Test
    void getRelativePathWhenNonHasBeenGiven() {
        final HomeLocator homeLocator = new HomeLocator();
        final RelativeLocationNotSetException relativeLocationNotSetException =
                assertThrows(RelativeLocationNotSetException.class, homeLocator::getRelativePath);
        assertThat(relativeLocationNotSetException).isNotNull();
        assertThat(relativeLocationNotSetException).hasMessage("Relative path not set");
    }

    @Test
    void getRelativePathTwoLevelsUp() {
        final HomeLocator homeLocator = new HomeLocator(TWO_LEVELS_UP);
        final Path relativePath = homeLocator.getRelativePath();
        final Path expectedPath = Path.of(TWO_LEVELS_UP);
        assertThat(relativePath).as("paths should match").isEqualTo(expectedPath);
    }

    @Test
    void unsetRelativePath() {
        final HomeLocator homeLocator = new HomeLocator(TWO_LEVELS_UP);
        final Path relativePath = homeLocator.getRelativePath();
        final Path expectedPath = Path.of(TWO_LEVELS_UP);
        assertThat(relativePath).as("paths should match").isEqualTo(expectedPath);
        homeLocator.unsetRelativePath();
        final RelativeLocationNotSetException relativeLocationNotSetException =
                assertThrows(RelativeLocationNotSetException.class, homeLocator::getRelativePath);

        assertThat(relativeLocationNotSetException).isNotNull();
        assertThat(relativeLocationNotSetException).hasMessage("Relative path not set");
    }

    @Test
    void nonExistingRelativePathIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();
        homeLocator.setRelativePath("../garble");

        final IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, homeLocator::getLocation);

        assertThat(illegalArgumentException).isNotNull();
        assertThat(illegalArgumentException)
                .hasMessageStartingWith("Relative path pointing to non-existing directory:");
    }

    @Test
    void nullRelativePathIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();

        final IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> homeLocator.setRelativePath((String) null));

        assertThat(illegalArgumentException).isNotNull();
        assertThat(illegalArgumentException).hasMessage("The parameter relativePath can not be null");
    }

    @Test
    void nullRelativePathFileIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();

        final IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> homeLocator.setRelativePath((Path) null));

        assertThat(illegalArgumentException).isNotNull();
        assertThat(illegalArgumentException).hasMessage("The parameter relativePath can not be null");
    }

    @Test
    void relativePathPointingToFileIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();
        homeLocator.setRelativePath("README.md");
        final IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, homeLocator::getLocation);

        assertThat(illegalArgumentException).isNotNull();
        assertThat(illegalArgumentException).hasMessageStartingWith("Relative path is not a directory:");
    }

    @Test
    void absolutePathIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();
        final String separator = FileSystems.getDefault().getSeparator();
        final String testPath;
        if ("/".equals(separator)) {
            testPath = "/home/sweet/home"; // On Unix
        } else {
            testPath = "C:/home/sweet/home"; // On Windows
        }
        final IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> homeLocator.setRelativePath(testPath));

        assertThat(illegalArgumentException).isNotNull();
        assertThat(illegalArgumentException)
                .hasMessageStartingWith("The parameter relativePath can not be an absolute path:");
    }
}
