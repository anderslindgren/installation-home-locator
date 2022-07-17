package se.javatomten.homelocator;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private Path relativePath;
    private boolean relativePathGiven;

    public HomeLocator() {
    }

    public HomeLocator(final String relativePath) {
        setRelativePath(relativePath);
    }

    public HomeLocator(Path relativePath) {
        setRelativePath(relativePath);
    }

    public Path getRelativePath() {
        if (!relativePathGiven) {
            throw new RelativeLocationNotSetException("Relative path not set");
        }
        return relativePath;
    }

    public void setRelativePath(final String relativePath) {
        if (relativePath == null) {
            throw new IllegalArgumentException("The parameter relativePath can not be null");
        }
        setRelativePath(Path.of(relativePath));
    }

    public void setRelativePath(final Path relativePath) {
        if (relativePath == null) {
            throw new IllegalArgumentException("The parameter relativePath can not be null");
        }
        if (relativePath.isAbsolute()) {
            throw new IllegalArgumentException("The parameter relativePath must be a relative path: " + relativePath);
        }
        this.relativePath = relativePath;
        relativePathGiven = true;
    }

    public void unsetRelativePath() {
        relativePathGiven = false;
    }

    /**
     * Returns the actual location of either the directory holding the class files or the
     * directory holding the jar file where the class file is located.
     *
     * @return the absolute directory of the jar file where this class is located in
     * or the base directory of this class file if not packaged into a jar file.
     */
    public Path getLocation() {
        final String className = HomeLocator.class.getName();
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
        if (relativePathGiven) {
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
