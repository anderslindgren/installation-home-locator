# Installation Home Locator
[![Java CI with Maven](https://github.com/anderslindgren/installation-home-locator/actions/workflows/maven.yml/badge.svg)](https://github.com/anderslindgren/installation-home-locator/actions/workflows/maven.yml)

## Introduction

Welcome to the installation-home-locator!

This is a project written in Java that will help you make it easier to find your installation home directory.
I.e. The name of the directory or folder where your program has been installed.

### Background

Twice in the past I had to figure this out, so I now finally wrote it down as a more general solution.

Hope it can help anyone else. At least now I know where to find the code the next time the problem arises :-)

## Usage

The Installation Home Locator is meant to be used as a utility to drop in to your application.
Specially if you are developing a GUI application, you don't know where in the file system your
application is finally being deployed.

The HomeLocator will return the directory where your code has been installed.

All this can be achieved with various start scripts in bash or bat-files, but it's more fun doing it in pure Java.

So how do I use this? It's easy! Include the jar file in your directory of third-party jars and add it to your
classpath.

In your application you only need to add a few lines (or cram it together on one line if you like):

```java
import java.nio.file.Path;

import se.javatomten.homelocator.*;

public class MyApplication {

    public MyApplication() {
        // Option; Give a relative path to the directory you actually call home.
        // I.e. if the jar file is located in a subdirectory to your home directory, as "lib"
        // You can give a relative path (i.e. "../") as an argument to the Constructor.
        final String relativePath = "../";
        final HomeLocator locator = new HomeLocator(relativePath);
        final Path homeDirectory = locator.getLocation();

        System.out.println("MyApplication is installed in: " + homeDirectory);
    }
}
```

It is also possible to add another class as a locator class if you have the HomeLocator class in a maven directory.
