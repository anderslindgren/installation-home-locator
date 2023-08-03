package se.javatomten.homelocator;

import org.junit.jupiter.api.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class HomeLocatorTest {

    private static final String TWO_LEVELS_UP = "../..";

    @Test
    void usePath() {
        final Path path = Path.of(TWO_LEVELS_UP);
        final HomeLocator homeLocator = new HomeLocator(path);
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of(".").normalize().toAbsolutePath();
        assertThat(homeLocation)
                .as("locations should match")
                .isEqualTo(expectedLocation);
    }

    @Test
    void locateHomeWhereRelativeTwoLevelsUp() {
        final HomeLocator homeLocator = new HomeLocator(TWO_LEVELS_UP);
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of(".").normalize().toAbsolutePath();
        assertThat(homeLocation)
                .as("locations should match")
                .isEqualTo(expectedLocation);
    }

    @Test
    void locateHomeWhereRelativeTwoLevelsUpUsingStringConstructor() {
        final HomeLocator homeLocator = new HomeLocator(TWO_LEVELS_UP);
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of(".").normalize().toAbsolutePath();
        assertThat(homeLocation)
                .as("locations should match")
                .isEqualTo(expectedLocation);
    }

    @Test
    void locateHomeWhereRelativeTwoLevelsUpUsingPathConstructor() {
        final HomeLocator homeLocator = new HomeLocator(Path.of(TWO_LEVELS_UP));
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of(".").normalize().toAbsolutePath();
        assertThat(homeLocation)
                .as("locations should match")
                .isEqualTo(expectedLocation);
    }

    @Test
    void locateHomeWhereNoRelativeIsGiven() {
        final HomeLocator homeLocator = new HomeLocator();
        final Path homeLocation = homeLocator.getLocation();
        final Path expectedLocation = Path.of("target/classes").normalize().toAbsolutePath();
        assertThat(homeLocation)
                .as("locations should match")
                .isEqualTo(expectedLocation);
    }

    @Test
    void nonExistingRelativePathIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator("../garble");

        assertThatIllegalArgumentException()
                .isThrownBy(homeLocator::getLocation)
                .as("Can not point to a non-existing directory")
                .withMessageStartingWith("Relative path pointing to non-existing directory:");
    }

    @Test
    void nullRelativePathIsNotAllowed() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HomeLocator((String) null))
                .as("Not ok to give null as parameter")
                .withMessage("The parameter relativePath can not be null");
    }

    @Test
    void nullRelativePathFileIsNotAllowed() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HomeLocator((Path) null))
                .as("Not ok to give null as parameter")
                .withMessageStartingWith("The parameter relativePath can not be null");
    }

    @Test
    void relativePathPointingToFileIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator("README.md");

        assertThatIllegalArgumentException()
                .isThrownBy(homeLocator::getLocation)
                .as("It is not allowed to give a file as relative path")
                .withMessageStartingWith("Relative path is not a directory:");
    }

    @Test
    void absolutePathIsNotAllowed() {
        final String separator = FileSystems.getDefault().getSeparator();
        final String testPath;
        if ("/".equals(separator)) {
            testPath = "/home/sweet/home"; // On Unix
        } else {
            testPath = "C:/home/sweet/home"; // On Windows
        }

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HomeLocator(testPath))
                .as("The relative path can not be an absolute path")
                .withMessageStartingWith("The parameter relativePath can not be an absolute path:");
    }

    @Test
    void locatorClassNotInJarFileOrClassFile() {
        Class<?> locatorClass = String.class;
        assertThatExceptionOfType(HomeLocatorException.class)
                .isThrownBy(() -> new HomeLocator(locatorClass).getLocation())
                .as("locator class must be in a jar file or class file")
                .withMessageStartingWith("Class is not available in a jar or classfile");
    }
}
