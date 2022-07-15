package se.javatomten.homelocator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Helper class designed to locate the installation directory for your application.
 * <p>
 * Basically you would put this jar (or class) file in your directory of third party jars.
 * Basic usage would be:
 * <pre>
 * HomeLocator tLocator = new HomeLocator("..");
 * File tLocation = tLocator.getLocation();
 * </pre>
 * When calling the {@link #getLocation()} method you will get a File back pointing you
 * to where on the file system your application has been installed.
 *
 * @author Anders Lindgren
 */
public class HomeLocator {

    private static final char JAR_CONTENT_SEPARATOR = '!';

    private File relativePath;
    private boolean relativePathGiven;

    public HomeLocator() {
    }

    public HomeLocator(final File relativePath) {
        setRelativePath(relativePath);
    }

    public HomeLocator(final String relativePath) {
        setRelativePath(relativePath);
    }

    public HomeLocator(Path relativePath) {
        setRelativePath(relativePath.toFile());
    }

    public File getRelativePath() {
        if (!relativePathGiven) {
            throw new RelativeLocationNotSetException("Relative path not set");
        }
        return relativePath;
    }

    public void setRelativePath(final String relativePath) {
        if (relativePath == null) {
            throw new IllegalArgumentException("The parameter relativePath can not be null");
        }
        setRelativePath(new File(relativePath));
    }

    public void setRelativePath(final File relativePath) {
        if (relativePath == null) {
            throw new IllegalArgumentException("The parameter relativePath can not be null");
        }
        if (relativePath.isAbsolute()) {
            throw new IllegalArgumentException("The parameter relativePath must be a relative path: " + relativePath);
        }
        this.relativePath = relativePath;
        relativePathGiven = true;
    }

    public void setRelativePath(Path relativePath) {
        if (relativePath == null) {
            throw new IllegalArgumentException("The parameter relativePath can not be null");
        }
        setRelativePath(relativePath.toFile());
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
    public File getLocation() {
        final String className = HomeLocator.class.getName();
        final String classFileName = convertClassNameToFileName(className);
        try {
            final URI uri = getClassURI(classFileName);
            final File location;
            if (isClassPackagedInJar(uri)) {
                location = locateDirectoryFromJarFile(uri);
            } else {
                location = locateDirectoryFromClassFile(classFileName, uri);
            }
            final File result = applyRelativePath(location);
            checkResult(result);
            return result;
        } catch (URISyntaxException | IOException e) {
            throw new HomeLocatorException("Could not locate jar file", e);
        }
    }

    private void checkResult(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Relative path pointing to non-existing directory: " + file.getPath());
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Relative path is not a directory: " + file.getPath());
        }
    }

    private boolean isClassPackagedInJar(URI uri) {
        return uri.getScheme().equals("jar");
    }

    private File applyRelativePath(File location) throws IOException {
        File result;
        if (relativePathGiven) {
            result = new File(location.getPath(), relativePath.getPath());
        } else {
            result = location;
        }
        return result.getCanonicalFile();
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

    private File locateDirectoryFromClassFile(String classFileName, URI uri) {
        final String path = uri.getPath();
        return new File(path.replace(classFileName, ""));
    }

    private File locateDirectoryFromJarFile(URI uri) {
        String path = uri.getSchemeSpecificPart();
        final int jarFileSeparatorPosition = path.lastIndexOf(JAR_CONTENT_SEPARATOR);
        if (jarFileSeparatorPosition >= 0) {
            path = path.substring("file:".length(), jarFileSeparatorPosition);
        }
        return new File(path).getParentFile();
    }
}
