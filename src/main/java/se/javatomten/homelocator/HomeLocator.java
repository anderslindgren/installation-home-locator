package se.javatomten.homelocator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HomeLocator {

    private static final char JAR_CONTENT_SEPARATOR = '!';

	private HomeLocator() {

    }

    public static File getHomeLocation(final String pRelativePath) {
        if (pRelativePath != null && new File(pRelativePath).isAbsolute()) {
            throw new IllegalArgumentException("The parameter pRelativePath must be a relative path");
        }
        File tResult = null;
        try {
            final String tClassName = HomeLocator.class.getName();
            final String tClassFileName = tClassName.replaceAll("\\.", "/") + ".class";
            final URL tResource = ClassLoader.getSystemClassLoader().getResource(tClassFileName);
            final URI tUri = tResource.toURI();
            File tLocation;
            if (tUri.getScheme().equals("jar")) {
            	String tPath;
            	tPath = tUri.getSchemeSpecificPart(); 
            	final int tJarFileSeparator = tPath.lastIndexOf(JAR_CONTENT_SEPARATOR);
            	if (tJarFileSeparator >= 0) {
            		tPath = tPath.substring("file:".length(), tJarFileSeparator);
            	}
            	tLocation = new File(tPath).getParentFile();
            }
            else {
            	String tPath = tUri.getPath();
                tPath = tPath.replace(tClassFileName, "");
                tLocation = new File(tPath);
            }
            if (pRelativePath != null) {
                tLocation = new File(tLocation.getCanonicalPath() + File.separatorChar + pRelativePath);
            }
            tResult = tLocation.getCanonicalFile();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return tResult;
    }
}
