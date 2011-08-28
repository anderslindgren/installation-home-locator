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

    private File mRelativePath;
    private PATH_GIVEN mRelativePathGiven;

    public HomeLocator() {
        mRelativePathGiven = PATH_GIVEN.NO;
    }

    public HomeLocator(final File pRelativePath) {
        setRelativePath(pRelativePath);
    }

    public HomeLocator(final String pRelativePath) {
        setRelativePath(pRelativePath);
    }

    public File getRelativePath() {
        if (mRelativePathGiven == PATH_GIVEN.NO) {
            throw new RelativeLocationNotSetException();
        }
        return mRelativePath;
    }

    public void setRelativePath(final String pRelativePath) {
        setRelativePath(new File(pRelativePath));
    }

    public void setRelativePath(final File pRelativePath) {
        if (pRelativePath != null && pRelativePath.isAbsolute()) {
            throw new IllegalArgumentException("The parameter pRelativePath must be a relative path: " + pRelativePath);
        }
        mRelativePath = pRelativePath;
        mRelativePathGiven = PATH_GIVEN.YES;
    }

    public void unsetRelativePath() {
        mRelativePathGiven = PATH_GIVEN.NO;
    }

    /**
     * Returns the actual location of either the directory holding the class files or the
     * directory holding the jar file where the class file is located.
     *
     * @return the absolute directory of the jar file where this class is located in
     * or the base directory of this class file if not packaged into a jar file.
     */
    public File getLocation() {
        File tResult;
        try {
            final String tClassName = HomeLocator.class.getName();
            final String tClassFileName = convertClassNameToFileName(tClassName);
            final URI tUri = getClassURI(tClassFileName);
            File tLocation;
            if (isClassPackagedInJar(tUri)) {
                tLocation = locateFromJarFile(tUri);
            }
            else {
                tLocation = locateFromClassFileDirectory(tClassFileName, tUri);
            }
            tLocation = applyRelativePath(tLocation);
            tResult = tLocation.getCanonicalFile();
            checkFileExistingOrThrowException(tResult);
            checkFileIsDirectoryOrThrowException(tResult);
        }
        catch (URISyntaxException e) {
            throw new HomeLocatorException(e);
        }
        catch (IOException e) {
            throw new HomeLocatorException(e);
        }

        return tResult;
    }

    private void checkFileIsDirectoryOrThrowException(File tResult) {
        if (!tResult.isDirectory()) {
            throw new IllegalArgumentException("Relative path pointing to a file: " + tResult.getPath());
        }
    }

    private void checkFileExistingOrThrowException(File tResult) {
        if (!tResult.exists()) {
            throw new IllegalArgumentException("Relative path pointing to non-existing directory: "
                    + tResult.getPath());
        }
    }

    private boolean isClassPackagedInJar(URI tUri) {
        return tUri.getScheme().equals("jar");
    }

    private File applyRelativePath(File tLocation) throws IOException {
        if (mRelativePathGiven == PATH_GIVEN.YES) {
            tLocation = new File(tLocation.getCanonicalPath() + File.separatorChar + mRelativePath);
        }
        return tLocation;
    }

    private String convertClassNameToFileName(String tClassName) {
        return tClassName.replaceAll("\\.", "/") + ".class";
    }

    private URI getClassURI(String tClassFileName) throws URISyntaxException {
        final URL tResource = ClassLoader.getSystemClassLoader().getResource(tClassFileName);
        return tResource.toURI();
    }

    private File locateFromClassFileDirectory(String tClassFileName, URI tUri) {
        File tLocation;
        String tPath = tUri.getPath();
        tPath = tPath.replace(tClassFileName, "");
        tLocation = new File(tPath);
        return tLocation;
    }

    private File locateFromJarFile(URI tUri) {
        File tLocation;
        String tPath;
        tPath = tUri.getSchemeSpecificPart();
        final int tJarFileSeparator = tPath.lastIndexOf(JAR_CONTENT_SEPARATOR);
        if (tJarFileSeparator >= 0) {
            tPath = tPath.substring("file:".length(), tJarFileSeparator);
        }
        tLocation = new File(tPath).getParentFile();
        return tLocation;
    }

}
