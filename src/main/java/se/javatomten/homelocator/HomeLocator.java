package se.javatomten.homelocator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Helper class designed to locate the installation directory for your application.
 *
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

    private enum PATH_GIVEN { YES, NO }

    private static final char JAR_CONTENT_SEPARATOR = '!';

    private File relativePath;
    private PATH_GIVEN relativePathGiven;

    public HomeLocator() {
        relativePathGiven = PATH_GIVEN.NO;
    }

    public HomeLocator(final File relativePath) {
        setRelativePath(relativePath);
    }

    public HomeLocator(final String relativePath) {
        setRelativePath(relativePath);
    }

    public File getRelativePath() {
        if (relativePathGiven == PATH_GIVEN.NO) {
            throw new RelativeLocationNotSetException("Relative path not set");
        }
        return relativePath;
    }

    public void setRelativePath(final String relativePath) {
        setRelativePath(new File(relativePath));
    }

    public void setRelativePath(final File relativePath) {
        if (relativePath != null && relativePath.isAbsolute()) {
            throw new IllegalArgumentException("The parameter relativePath must be a relative path: " + relativePath);
        }
        this.relativePath = relativePath;
        relativePathGiven = PATH_GIVEN.YES;
    }

    public void unsetRelativePath() {
        relativePathGiven = PATH_GIVEN.NO;
    }

    /**
     * Returns the actual location of either the directory holding the class files or the
     * directory holding the jar file where the class file is located.
     *
     * @return the absolute directory of the jar file where this class is located in
     * or the base directory of this class file if not packaged into a jar file.
     */
    public File getLocation() {
        File result;
        try {
            final String className = HomeLocator.class.getName();
            final String classFileName = convertClassNameToFileName(className);
            final URI uri = getClassURI(classFileName);
            File location;
            if (isClassPackagedInJar(uri)) {
                location = locateFromJarFile(uri);
            }
            else {
                location = locateFromClassFileDirectory(classFileName, uri);
            }
            location = applyRelativePath(location);
            result = location.getCanonicalFile();
            checkFileExistingOrThrowException(result);
            checkFileIsDirectoryOrThrowException(result);
        }
        catch (URISyntaxException | IOException e) {
            throw new HomeLocatorException(e);
        }

        return result;
    }

    private void checkFileIsDirectoryOrThrowException(File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Relative path pointing to a file: " + file.getPath());
        }
    }

    private void checkFileExistingOrThrowException(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Relative path pointing to non-existing directory: " + file.getPath());
        }
    }

    private boolean isClassPackagedInJar(URI uri) {
        return uri.getScheme().equals("jar");
    }

    private File applyRelativePath(File location) throws IOException {
        if (relativePathGiven == PATH_GIVEN.YES) {
            location = new File(location.getCanonicalPath() + File.separatorChar + relativePath);
        }
        return location;
    }

    private String convertClassNameToFileName(String className) {
        return className.replaceAll("\\.", "/") + ".class";
    }

    private URI getClassURI(String classFileName) throws URISyntaxException {
        final URL resource = ClassLoader.getSystemClassLoader().getResource(classFileName);
        if (resource == null) {
            throw new HomeLocatorException("Could not find home locator class");
        }
        return resource.toURI();
    }

    private File locateFromClassFileDirectory(String classFileName, URI uri) {
        File location;

        String path = uri.getPath();
        path = path.replace(classFileName, "");
        location = new File(path);
        return location;
    }

    private File locateFromJarFile(URI uri) {
        File location;
        String path;
        path = uri.getSchemeSpecificPart();
        final int jarFileSeparator = path.lastIndexOf(JAR_CONTENT_SEPARATOR);
        if (jarFileSeparator >= 0) {
            path = path.substring("file:".length(), jarFileSeparator);
        }
        location = new File(path).getParentFile();
        return location;
    }

}
