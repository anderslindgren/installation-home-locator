package se.javatomten.homelocator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HomeLocator {

    private HomeLocator() {

    }

    public static File getHomeLocation(final File pRelativePath) {
        if (pRelativePath != null && pRelativePath.isAbsolute()) {
            throw new IllegalArgumentException("The parameter pRelativePath must be a relative path");
        }
        File tResult = null;
        try {
            final String tClassName = HomeLocator.class.getName();
            final String tClassFileName = tClassName.replaceAll("\\.", "/") + ".class";
            final URL tResource = ClassLoader.getSystemClassLoader().getResource(tClassFileName);
            System.out.println(tResource);
            final URI tUri = tResource.toURI();
            String tPath = tUri.getPath();
            final int tJarFileSeparator = tPath.indexOf('$');
            if (tJarFileSeparator >= 0) {
                tPath = tPath.substring(0, tJarFileSeparator);
            }
            else {
                tPath = tPath.replace(tClassFileName, "");
            }
            if (pRelativePath != null) {
                tPath += pRelativePath.toString();
            }
            System.out.println(tPath);
            tResult = new File(tPath).getCanonicalFile();
            System.out.println(tResult);
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
