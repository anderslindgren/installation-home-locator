package se.javatomten.homelocator;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Helper class designed to locate the installation directory for your application.
 * <p>
 * Basically you would put this jar (or class) file in your directory of third party jars.
 * Basic usage would be:
 * <pre>
 * HomeLocator locator = new HomeLocator("..");
 * Path location = locator.getLocation();
 * </pre>
 * When calling the {@link #getLocation()} method you will get a Path back pointing you
 * to where on the file system your application has been installed.
 *
 * @author Anders Lindgren
 */
public class HomeLocator {

    private static final char JAR_CONTENT_SEPARATOR = '!';
    private final Class<?>[] locatorClasses;

    private Path relativePath;

    /**
     * Create a new HomeLocator object with the default settings for locator class and relative path.
     * <p/>
     * The default value for locator class is this class ({@link HomeLocator}) and the default value for relative path
     * is the current directory.
     *
     * @param locatorClasses The first class in this list will be used to locate your home path.
     *                       If not given this class is used instead.
     */
    public HomeLocator(final Class<?>... locatorClasses) {
        if (locatorClasses.length == 0) {
            this.locatorClasses = new Class[]{HomeLocator.class};
        } else {
            this.locatorClasses = locatorClasses;
        }
    }

    /**
     * Create a new HomeLocator object with a relative path and an optional locator class.
     * <p/>
     * The default value for locator class is this class ({@link HomeLocator})
     *
     * @param relativePath   a relative path that is appended to the location of the locator class.
     * @param locatorClasses The first class in this list will be used to locate your home path.
     *                       If not given this class is used instead.
     * @throws IllegalArgumentException if relativePath is null.
     */
    public HomeLocator(final String relativePath, final Class<?>... locatorClasses) {
        this(Path.of(checkPath(relativePath)), locatorClasses);
    }

    /**
     * Create a new HomeLocator object with a relative path and an optional locator class.
     * <p/>
     * The default value for locator class is this class ({@link HomeLocator})
     *
     * @param relativePath   a relative path that is appended to the location of the locator class.
     * @param locatorClasses The first class in this list will be used to locate your home path.
     *                       If not given this class is used instead.
     * @throws IllegalArgumentException if relativePath is null.
     */
    public HomeLocator(final Path relativePath, final Class<?>... locatorClasses) {
        this(locatorClasses);
        if (relativePath == null) {
            throw new IllegalArgumentException("The parameter relativePath can not be null");
        }
        if (relativePath.isAbsolute()) {
            throw new IllegalArgumentException("The parameter relativePath can not be an absolute path: "
                    + relativePath);
        }
        this.relativePath = relativePath;
    }

    /**
     * Checks if the given relative path is null and if so throws an IllegalArgumentException
     *
     * @param relativePath string to check
     * @return the relativePath
     * @throws IllegalArgumentException if relativePath is null.
     */
    private static String checkPath(String relativePath) {
        if (relativePath == null) {
            throw new IllegalArgumentException("The parameter relativePath can not be null");
        }
        return relativePath;
    }

    /**
     * Returns the actual location of either the directory holding the location class file or the
     * directory holding the jar file where the location class file is located.
     *
     * @return the absolute directory of the jar file where this class is located in
     * or the base directory of this class file if not packaged into a jar file.
     * @throws HomeLocatorException if the location class can not be found in the classpath
     */
    public Path getLocation() {
        final String className = Arrays.stream(locatorClasses)
                .findFirst()
                .orElseThrow()
                .getName();
        final String classFileName = convertClassNameToFileName(className);
        try {
            final URI uri = getClassURI(classFileName);
            final Path location;
            if (isClassPackagedInJar(uri)) {
                location = locateDirectoryFromJarFile(uri);
            } else {
                location = locateDirectoryFromClassFile(classFileName, uri);
            }
            final Path result = applyRelativePath(location);
            checkResult(result);
            return result;
        } catch (URISyntaxException e) {
            throw new HomeLocatorException("Could not locate jar file", e);
        }
    }

    private void checkResult(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Relative path pointing to non-existing directory: " + path);
        }
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Relative path is not a directory: " + path);
        }
    }

    private boolean isClassPackagedInJar(URI uri) {
        return uri.getScheme().equals("jar");
    }

    private Path applyRelativePath(Path location) {
        Path result;
        if (relativePath != null) {
            result = location.resolve(relativePath);
        } else {
            result = location;
        }
        return result.toAbsolutePath().normalize();
    }

    private String convertClassNameToFileName(String className) {
        return className.replaceAll("\\.", "/") + ".class";
    }

    private URI getClassURI(String classFileName) throws URISyntaxException {
        final URL resource = ClassLoader.getSystemClassLoader().getResource(classFileName);
        if (resource == null) { // This should not be possible as it would mean it can not find this class
            throw new HomeLocatorException("Could not find home locator class");
        }
        return resource.toURI();
    }

    private Path locateDirectoryFromClassFile(String classFileName, URI uri) {
        String path = uri.getPath().replace(classFileName, "");
        return Path.of(path);
    }

    private Path locateDirectoryFromJarFile(URI uri) {
        String path = uri.getSchemeSpecificPart();
        final int jarFileSeparatorPosition = path.lastIndexOf(JAR_CONTENT_SEPARATOR);
        if (jarFileSeparatorPosition >= 0) {
            path = path.substring("file:".length(), jarFileSeparatorPosition);
        }
        return Path.of(path).getParent();
    }
}
