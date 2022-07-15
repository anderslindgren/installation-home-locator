package se.javatomten.homelocator;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HomeLocatorTest {

    public static final String TWO_LEVELS_UP = "../..";

    @Test
    public void usePath() throws IOException {
        final HomeLocator homeLocator = new HomeLocator();
        final Path path = Path.of(TWO_LEVELS_UP);
        homeLocator.setRelativePath(path);
        final File homeLocation = homeLocator.getLocation();
        final File expectedLocation = new File(".").getCanonicalFile();
        assertThat("Could not find expected location", homeLocation, equalTo(expectedLocation));
    }
    @Test
    public void locateHomeWhereRelativeTwoLevelsUp() throws IOException {
        final HomeLocator homeLocator = new HomeLocator();
        homeLocator.setRelativePath(TWO_LEVELS_UP);
        final File homeLocation = homeLocator.getLocation();
        final File expectedLocation = new File(".").getCanonicalFile();
        assertThat("Could not find expected location", homeLocation, equalTo(expectedLocation));
    }

    @Test
    public void locateHomeWhereRelativeTwoLevelsUpUsingStringConstructor() throws IOException {
        final HomeLocator homeLocator = new HomeLocator(TWO_LEVELS_UP);
        final File homeLocation = homeLocator.getLocation();
        final File expectedLocation = new File(".").getCanonicalFile();
        assertThat("Could not find expected location", homeLocation, equalTo(expectedLocation));
    }

    @Test
    public void locateHomeWhereRelativeTwoLevelsUpUsingFileConstructor() throws IOException {
        final HomeLocator homeLocator = new HomeLocator(new File(TWO_LEVELS_UP));
        final File homeLocation = homeLocator.getLocation();
        final File expectedLocation = new File(".").getCanonicalFile();
        assertThat("Could not find expected location", homeLocation, equalTo(expectedLocation));
    }

    @Test
    public void usePathConstructor() throws IOException {
        final HomeLocator homeLocator = new HomeLocator(Path.of(TWO_LEVELS_UP));
        final File homeLocation = homeLocator.getLocation();
        final File expectedLocation = new File(".").getCanonicalFile();
        assertThat("Could not find expected location", homeLocation, equalTo(expectedLocation));
    }

    @Test
    public void locateHomeWhereNoRelativeIsGiven() throws IOException {
        final HomeLocator homeLocator = new HomeLocator();
        final File homeLocation = homeLocator.getLocation();
        final File expectedLocation = new File("target/classes").getCanonicalFile();
        assertThat("Could not find expected location", homeLocation, equalTo(expectedLocation));
    }

    @Test
    public void getRelativePathWhenNonHasBeenGiven() {
        final HomeLocator homeLocator = new HomeLocator();
        RelativeLocationNotSetException relativeLocationNotSetException =
                assertThrows(RelativeLocationNotSetException.class, homeLocator::getRelativePath);
        assertThat(relativeLocationNotSetException, notNullValue());
        assertThat(relativeLocationNotSetException.getMessage(), containsString("Relative path not set"));
    }

    @Test
    public void getRelativePathTwoLevelsUp() {
        final HomeLocator homeLocator = new HomeLocator(TWO_LEVELS_UP);
        final File relativePath = homeLocator.getRelativePath();
        assertThat("Relative File wrong", relativePath, equalTo(new File(TWO_LEVELS_UP)));
    }

    @Test
    public void unsetRelativePath() {
        final HomeLocator homeLocator = new HomeLocator(TWO_LEVELS_UP);
        final File relativePath = homeLocator.getRelativePath();
        assertEquals(new File(TWO_LEVELS_UP), relativePath, "Relative File wrong");
        homeLocator.unsetRelativePath();
        RelativeLocationNotSetException relativeLocationNotSetException =
                assertThrows(RelativeLocationNotSetException.class, homeLocator::getRelativePath);

        assertThat(relativeLocationNotSetException, notNullValue());
        assertThat(relativeLocationNotSetException.getMessage(), containsString("Relative path not set"));
    }

    @Test
    public void nonExistingRelativePathIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();
        homeLocator.setRelativePath("../garble");

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, homeLocator::getLocation);

        assertThat("Non existing directory is not allowed", illegalArgumentException, notNullValue());
        assertThat(illegalArgumentException.getMessage(),
                containsString("Relative path pointing to non-existing directory:"));
    }

    @Test
    public void nullRelativePathIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> homeLocator.setRelativePath((String) null));

        assertThat("Non existing directory is not allowed", illegalArgumentException, notNullValue());
        assertThat(illegalArgumentException.getMessage(),
                equalTo("The parameter relativePath can not be null"));
    }
    @Test
    public void nullRelativePathFileIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> homeLocator.setRelativePath((File) null));

        assertThat("Non existing directory is not allowed", illegalArgumentException, notNullValue());
        assertThat(illegalArgumentException.getMessage(),
                equalTo("The parameter relativePath can not be null"));
    }

    @Test
    public void relativePathPointingToFileIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();
        homeLocator.setRelativePath("README");
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, homeLocator::getLocation);

        assertThat("Can't point to a file", illegalArgumentException, notNullValue());
        assertThat(illegalArgumentException.getMessage(),
                containsString("Relative path pointing to non-existing directory:"));
    }

    @Test
    public void absolutePathIsNotAllowed() {
        final HomeLocator homeLocator = new HomeLocator();
        final char separatorChar = File.separatorChar;
        final String testFile;
        if (separatorChar == '/') {
            testFile = "/home/sweet/home"; // On Unix
        } else {
            testFile = "C:/home/sweet/home"; // On Windows
        }
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> homeLocator.setRelativePath(testFile));

        assertThat("Relative path can't be absolute", illegalArgumentException, notNullValue());
        assertThat(illegalArgumentException.getMessage(),
                containsString("The parameter relativePath must be a relative path:"));
    }

}
