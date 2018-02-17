import JUnit.*;
import JUnit.Exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.SyncFailedException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Console application that takes paths to .jar files as arguments, loads classes from them and runs tests in
 * those classes.
 */
public class Main {

    private static List<Class> loadClassesFromJar(@NotNull String pathToJar) {
        List<Class> result = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(pathToJar);
            Enumeration<JarEntry> entries = jarFile.entries();
            URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if(je.isDirectory() || !je.getName().endsWith(".class")){
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0,je.getName().length()-6);
                className = className.replace('/', '.');
                try {
                    Class c = cl.loadClass(className);
                    result.add(c);
                } catch (ClassNotFoundException e) {
                    System.out.println("File \"" + pathToJar + "\" was corrupted.");
                }

            }
        } catch (IOException e) {
            System.out.println("Wasn't able to read from file \"" + pathToJar + "\":");
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("You should pass at least one path to .jar file.");
            return;
        }
        for (String pathToJar : args) {
            List<Class> testClasses = loadClassesFromJar(pathToJar);
            for (Class testClass : testClasses) {
                String failMessage = null;
                Tester tester = new Tester();
                try {
                    TestResults results = tester.testClass(testClass);
                    System.out.println("Results of running tests in \"" + testClass.getName() + "\" class from \""
                            + pathToJar + "\":");
                    for (PassedTest test : results.getPassed()) {
                        System.out.println("Test \"" + test.getName() + "\" passed successfully in " + test.getTime() + "ms.");
                    }
                    for (FailedTest test : results.getFailed()) {
                        if (test.getException() != null) {
                            System.out.println("Test \"" + test.getName() + "\" failed with exception:");
                            System.out.println(test.getException().getMessage());
                        } else {
                            System.out.println("Test \"" + test.getName() + "\" failed because expected exception wasn't thrown.");
                        }
                    }
                    for (IgnoredTest test : results.getIgnored()) {
                        System.out.println("Test \"" + test.getName() + "\" ignored with following message:");
                        System.out.println(test.getReason());
                    }
                } catch (NoDefaultConstructorException e) {
                    failMessage = "Class should have a default constructor.";
                } catch (IncorrectAnnotationUsageException e) {
                    failMessage = "You can't use annotations test and before/after/beforeClass/afterClass at the same time.";
                } catch (AfterFailException e) {
                    failMessage = "Method " + e.getMessage() + " ran because of \"After\" annotation and failed with exception:";
                    failMessage += "\n" + e.getSuppressed()[0].getMessage();
                } catch (AfterClassFailException e) {
                    failMessage = "Method " + e.getMessage() + " ran because of \"AfterClass\" annotation and failed with exception:";
                    failMessage += e.getSuppressed()[0].getMessage();
                } catch (BeforeFailException e) {
                    failMessage = "Method " + e.getMessage() + " ran because of \"Before\" annotation and failed with exception:";
                    failMessage += e.getSuppressed()[0].getMessage();
                } catch (BeforeClassFailException e) {
                    failMessage = "Method " + e.getMessage() + " ran because of \"BeforeClass\" annotation and failed with exception:";
                    failMessage += e.getSuppressed()[0].getMessage();
                } catch (NotTestClassException e) {
                    continue;
                }
                if (failMessage != null) {
                    System.out.println("Failed testing in class \"" + testClass.getName() + "\" class from \""
                            + pathToJar + "\":");
                    System.out.println(failMessage);
                }
                System.out.println("");
            }
        }
    }
}
