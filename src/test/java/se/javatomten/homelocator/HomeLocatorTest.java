package se.javatomten.homelocator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class HomeLocatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void localteFileOnDiskRelativeTwoLevelsUp() throws IOException {
        File tLocation = HomeLocator.getHomeLocation(new File("../.."));
        assertEquals(tLocation, new File(".").getCanonicalFile());
    }
    
    @Test
    public void localteFileOnDiskRelativeIsNull() throws IOException {
        File tLocation = HomeLocator.getHomeLocation(null);
        assertEquals(tLocation, new File("target/classes").getCanonicalFile());
    }
    
    @Test
    public void localteFileOnDiskRelativeIsNonExisting() throws IOException {
        File tLocation = HomeLocator.getHomeLocation(new File("../putte"));
        assertEquals(tLocation, new File("target/putte").getCanonicalFile());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void onlyRelativePathAllowed() {
        char tSep = File.separatorChar;
        File tTestFile;
        if (tSep == '/') {
            tTestFile = new File("/home/sweet/home"); // On Unix
        }
        else {
            tTestFile = new File("C:/home/sweet/home"); // On Windows
        }
        HomeLocator.getHomeLocation(tTestFile);
    }

}
