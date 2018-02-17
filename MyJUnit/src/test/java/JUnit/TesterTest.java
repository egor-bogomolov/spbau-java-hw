package JUnit;

import JUnit.Annotations.After;
import JUnit.Annotations.AfterClass;
import JUnit.Annotations.Before;
import JUnit.Annotations.BeforeClass;
import JUnit.Exceptions.AfterClassFailException;
import JUnit.Exceptions.IncorrectAnnotationUsageException;
import JUnit.Exceptions.NoDefaultConstructorException;
import JUnit.Exceptions.NotTestClassException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TesterTest {

    @Test
    public void testIgnoredClass() throws Exception {
        TestResults results = (new Tester().testClass(TestIgnored.class));
        assertTrue(results.getIgnored().containsAll(Arrays.asList(
                new IgnoredTest("ignoredTest1", "reason1"),
                new IgnoredTest("ignoredTest2", "reason2"))));
        assertTrue(results.getPassed().contains(new PassedTest("notIgnoredTest", 0)));
    }

    @Test
    public void testException() throws Exception {
        TestResults results = (new Tester().testClass(TestException.class));
        assertTrue(results.getPassed().contains(new PassedTest("exceptionTest1", 0)));
        assertTrue(results.getFailed().contains(new FailedTest("exceptionTest2", new Exception())));
    }

    @Test(expected = IncorrectAnnotationUsageException.class)
    public void testMultipleAnnotations() throws Exception {
        new Tester().testClass(TestMultipleAnnotations.class);
    }

    @Test(expected = NotTestClassException.class)
    public void testWithoutTests() throws Exception {
        new Tester().testClass(TestWithoutTests.class);
    }

    @Test(expected = NoDefaultConstructorException.class)
    public void testNoDefault() throws Exception {
        new Tester().testClass(TestNoDefault.class);
    }

    @Test
    public void testBefore() throws Exception {
        TestResults results = (new Tester().testClass(TestBefore.class));
        assertTrue(results.getFailed().isEmpty());
    }

    @Test
    public void testAfter() throws Exception {
        TestResults results = (new Tester().testClass(TestAfter.class));
        assertTrue(results.getFailed().isEmpty());
    }

    @Test
    public void testBeforeClass() throws Exception {
        TestResults results = (new Tester().testClass(TestBeforeClass.class));
        assertTrue(results.getFailed().isEmpty());
    }

    @Test(expected = AfterClassFailException.class)
    public void testAfterClass() throws Exception {
        TestResults results = (new Tester().testClass(TestAfterClass.class));
        assertTrue(results.getFailed().isEmpty());
    }


    public static class TestIgnored {

        @JUnit.Annotations.Test(ignore = "reason1")
        public void ignoredTest1() {}

        @JUnit.Annotations.Test(ignore = "reason2")
        public void ignoredTest2() {}

        @JUnit.Annotations.Test
        public void notIgnoredTest() {}

    }

    public static class TestException {

        @JUnit.Annotations.Test(expected = Exception.class)
        public void exceptionTest1() throws Exception {
            throw new Exception("some text");
        }

        @JUnit.Annotations.Test(expected = Exception.class)
        public void exceptionTest2() throws MyException {
            throw new MyException();
        }

        public static class MyException extends Exception {}
    }

    public static class TestMultipleAnnotations {
        @JUnit.Annotations.Test
        @Before
        public void method() {}
    }

    public static class TestWithoutTests {}

    public static class TestNoDefault {
        TestNoDefault(int n) {}

        @JUnit.Annotations.Test
        public void testMethod() {}
    }

    public static class TestBefore {
        private int i = 1;

        @JUnit.Annotations.Test
        public void method1() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
            i = 1;
        }

        @JUnit.Annotations.Test
        public void method2() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
            i = 1;
        }

        @JUnit.Annotations.Test
        public void method3() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
            i = 1;
        }

        @Before
        public void beforeMethod() {
            i = 0;
        }
    }

    public static class TestAfter {
        private int i = 0;

        @JUnit.Annotations.Test
        public void method1() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
            i = 1;
        }

        @JUnit.Annotations.Test
        public void method2() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
            i = 1;
        }

        @JUnit.Annotations.Test
        public void method3() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
            i = 1;
        }

        @After
        public void afterMethod() {
            i = 0;
        }
    }

    public static class TestBeforeClass {
        private int i = 1;

        @JUnit.Annotations.Test
        public void method1() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
        }

        @JUnit.Annotations.Test
        public void method2() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
        }

        @JUnit.Annotations.Test
        public void method3() throws Exception {
            if (i == 1) {
                throw new Exception();
            }
        }

        @BeforeClass
        public void beforeClassMethod() {
            i = 0;
        }
    }

    public static class TestAfterClass {

        @JUnit.Annotations.Test
        public void method1() throws Exception {}

        @JUnit.Annotations.Test
        public void method2() throws Exception {}

        @JUnit.Annotations.Test
        public void method3() throws Exception {}

        @AfterClass
        public void afterClassMethod() throws Exception {
            throw new AfterClassFailException("this method was called");
        }
    }
}