package JUnit;

import JUnit.Annotations.*;
import JUnit.Exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that provides access to single public method "testClass". This method is used to run all tests from
 * given class.
 * For each run of tests you need a fresh instance of this class.
 */
public class Tester {
    private Object instance = null;
    private List<Method> beforeClass = new ArrayList<>();
    private List<Method> before = new ArrayList<>();
    private List<TestMethod> methodsToTest = new ArrayList<>();
    private List<Method> after = new ArrayList<>();
    private List<Method> afterClass = new ArrayList<>();
    private TestResults results = new TestResults();

    /**
     * Runs all tests from given class according to annotations.
     * @param testClass - class containing tests
     * @return - TestResults instance containing information about all run tests.
     * @throws NoDefaultConstructorException - thrown if given class doesn't have default constructor.
     * @throws IncorrectAnnotationUsageException - thrown if the class contains method that has both test and
     * before/beforeClass/after/afterClass annotations.
     * @throws BeforeClassFailException - thrown if one of methods with annotation "beforeClass" failed with an
     * exception.
     * @throws AfterClassFailException - thrown if one of methods with annotation "afterClass" failed with an
     * exception.
     * @throws AfterFailException - thrown if one of methods with annotation "after" failed with an
     * exception.
     * @throws BeforeFailException - thrown if one of methods with annotation "before" failed with an
     * exception.
     * @throws NotTestClassException - thrown if given class doesn't have any methods with annotation test.
     */
    public TestResults testClass(@NotNull Class testClass)
            throws NoDefaultConstructorException, IncorrectAnnotationUsageException,
            BeforeClassFailException, AfterClassFailException, AfterFailException, BeforeFailException, NotTestClassException {
        parseClass(testClass);
        runBeforeClass();
        runTests();
        runAfterClass();
        return results;
    }

    /**
     * Processes all methods of given class and divides them in groups by their annotations and purposes.
     * @param testClass - class containing tests.
     * @throws NoDefaultConstructorException - thrown if the class doesn't have default constructor.
     * @throws IncorrectAnnotationUsageException - thrown if the class contains method that has both test and
     * before/beforeClass/after/afterClass annotations.
     * @throws NotTestClassException - thrown if given class doesn't have any methods with annotation test.
     */
    private void parseClass(@NotNull Class testClass)
            throws NoDefaultConstructorException, IncorrectAnnotationUsageException, NotTestClassException {
        boolean hasTest = false;
        Method[] methods = testClass.getDeclaredMethods();
        for (Method method : methods) {
            if (!checkMethodValidity(method)) {
                throw new IncorrectAnnotationUsageException();
            }
            if (method.isAnnotationPresent(Before.class)) {
                before.add(method);
            }
            if (method.isAnnotationPresent(After.class)) {
                after.add(method);
            }
            if (method.isAnnotationPresent(BeforeClass.class)) {
                beforeClass.add(method);
            }
            if (method.isAnnotationPresent(AfterClass.class)) {
                afterClass.add(method);
            }
            if (method.isAnnotationPresent(Test.class)) {
                hasTest = true;
                Test testAnnotation = method.getAnnotation(Test.class);
                if (testAnnotation.ignore().equals("")) {
                    if (testAnnotation.expected().equals(NotAnException.class)) {
                        methodsToTest.add(new TestMethod(method));
                    } else {
                        methodsToTest.add(new TestMethod(method, testAnnotation.expected()));
                    }
                } else {
                    results.addIgnored(new IgnoredTest(method.getName(), testAnnotation.ignore()));
                }
            }
        }
        if (!hasTest) {
            throw new NotTestClassException();
        }
        try {
            instance = testClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new NoDefaultConstructorException();
        }
    }

    /**
     * Checks that given method doesn't have test alongside with before/beforeClass/after/afterClass annotation.
     * @param method - method that should be checked.
     * @return - whether method is valid or not.
     */
    private boolean checkMethodValidity(Method method) {
        return !method.isAnnotationPresent(Test.class) ||
                !(method.isAnnotationPresent(Before.class) ||
                method.isAnnotationPresent(After.class) ||
                method.isAnnotationPresent(BeforeClass.class) ||
                method.isAnnotationPresent(AfterClass.class));
    }

    /**
     * Runs all methods with annotation "test". Before each method runBefore is called, after each method runAfter
     * is called.
     * @throws BeforeFailException - thrown if one of methods called in runBefore failed with an exception.
     * @throws AfterFailException - thrown if one of methods called in runAfter failed with an exception.
     */
    private void runTests() throws BeforeFailException, AfterFailException {
        for (TestMethod method : methodsToTest) {
            runBefore();
            long startTime = System.currentTimeMillis();
            Exception exception = null;
            try {
                method.getMethod().invoke(instance);

            } catch (Exception e) {
                exception = e;
            }
            long stopTime = System.currentTimeMillis();
            if ((method.getExpected() == null && exception == null) ||
                    (exception != null && method.getExpected() != null && method.getExpected().equals(exception.getClass()))) {
                results.addPassed(new PassedTest(method.getMethod().getName(), stopTime - startTime));
            } else {
                results.addFailed(new FailedTest(method.getMethod().getName(), exception));
            }
            runAfter();
        }
    }

    /**
     * Runs all methods with annotation "before".
     * @throws BeforeFailException - thrown if one of methods failed with an exception.
     */
    private void runBefore() throws BeforeFailException {
        for (Method method : before) {
            try {
                method.invoke(instance);
            } catch (Exception e) {
                BeforeFailException exception =
                        new BeforeFailException("Fail on method " + method.getName());
                exception.addSuppressed(e);
                throw exception;
            }
        }
    }

    /**
     * Runs all methods with annotation "after".
     * @throws AfterFailException - thrown if one of methods failed with an exception.
     */
    private void runAfter() throws AfterFailException {
        for (Method method : after) {
            try {
                method.invoke(instance);
            } catch (Exception e) {
                AfterFailException exception =
                        new AfterFailException("Fail on method " + method.getName());
                exception.addSuppressed(e);
                throw exception;
            }
        }
    }

    /**
     * Runs all methods with annotation "beforeClass".
     * @throws BeforeClassFailException - thrown if one of methods failed with an exception.
     */
    private void runBeforeClass() throws BeforeClassFailException {
        for (Method method : beforeClass) {
            try {
                method.invoke(instance);
            } catch (Exception e) {
                BeforeClassFailException exception =
                        new BeforeClassFailException("Fail on method " + method.getName());
                exception.addSuppressed(e);
                throw exception;
            }
        }
    }

    /**
     * Runs all methods with annotation "afterClass".
     * @throws AfterClassFailException - thrown if one of methods failed with an exception.
     */
    private void runAfterClass() throws AfterClassFailException {
        for (Method method : afterClass) {
            try {
                method.invoke(instance);
            } catch (Exception e) {
                AfterClassFailException exception =
                        new AfterClassFailException("Fail on method " + method.getName());
                exception.addSuppressed(e);
                throw exception;
            }
        }
    }
}