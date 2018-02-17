package ru.spbau.bogomolov;

import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LazyFactoryTest {
    private static final int THREADS_NUMBER = 8;
    private static final int TEST_NUMBER = 239;
    private static final long SLEEPING_TIME = 1000;

    private static int callsToNumberSupplier;
    private static int callsToSlowSupplier;

    private static final Supplier<Integer> numberSupplier = () -> {
        callsToNumberSupplier++;
        return TEST_NUMBER;
    };

    private static final Supplier<Integer> slowSupplier = () -> {
        try {
            Thread.sleep(SLEEPING_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        callsToSlowSupplier++;
        return TEST_NUMBER;
    };

    private static final Supplier<Object> nullSupplier = () -> null;

    @Before
    public void prepare() {
        callsToNumberSupplier = 0;
        callsToSlowSupplier = 0;
    }

    private void testSimple(Lazy<Integer> integerLazy) throws Exception {
        Integer result = integerLazy.get();
        assertEquals(TEST_NUMBER, result.intValue());
        for (int i = 0; i < 5; i++) {
            assertSame(result, integerLazy.get());
        }
        assertEquals(1, callsToNumberSupplier);
    }

    private void testNoCallsToGet(Lazy<Integer> integerLazy) throws Exception {
        assertEquals(0, callsToNumberSupplier);
    }

    private void testNullSupplier(Lazy<Object> nullLazy) throws Exception {
        assertNull(nullLazy.get());
    }

    @Test
    public void nonConcurrentLazySimple() throws Exception {
        testSimple(LazyFactory.createNonConcurrentLazy(numberSupplier));
    }

    @Test
    public void nonConcurrentLazyNoCallsToGet() throws Exception {
        testNoCallsToGet(LazyFactory.createNonConcurrentLazy(numberSupplier));
    }

    @Test
    public void nonConcurrentLazyNullSupplier() throws Exception {
        testNullSupplier(LazyFactory.createNonConcurrentLazy(nullSupplier));
    }

    @Test
    public void multipleNonConcurrentLazyOneSupplier() throws Exception {
        for (int i = 1; i <= 5; i++) {
            Lazy<Integer> integerLazy = LazyFactory.createNonConcurrentLazy(numberSupplier);
            Integer result = integerLazy.get();
            assertEquals(TEST_NUMBER, result.intValue());
            assertSame(result, integerLazy.get());
            assertEquals(i, callsToNumberSupplier);
        }
    }

    @Test
    public void concurrentLazySimple() throws Exception {
        testSimple(LazyFactory.createConcurrentLazy(numberSupplier));
    }

    @Test
    public void concurrentLazyNoCallsToGet() throws Exception {
        testNoCallsToGet(LazyFactory.createConcurrentLazy(numberSupplier));
    }

    @Test
    public void concurrentLazyNullSupplier() throws Exception {
        testNullSupplier(LazyFactory.createConcurrentLazy(nullSupplier));
    }

    @Test
    public void multipleConcurrentLazyOneSupplier() throws Exception {
        for (int i = 1; i <= 5; i++) {
            Lazy<Integer> integerLazy = LazyFactory.createConcurrentLazy(numberSupplier);
            Integer result = integerLazy.get();
            assertEquals(TEST_NUMBER, result.intValue());
            assertSame(result, integerLazy.get());
            assertEquals(i, callsToNumberSupplier);
        }
    }

    @Test
    public void concurrentLockFreeLazySimple() throws Exception {
        testSimple(LazyFactory.createConcurrentLockFreeLazy(numberSupplier));
    }

    @Test
    public void concurrentLockFreeLazyNoCallsToGet() throws Exception {
        testNoCallsToGet(LazyFactory.createConcurrentLockFreeLazy(numberSupplier));
    }

    @Test
    public void concurrentLockFreeLazyNullSupplier() throws Exception {
        testNullSupplier(LazyFactory.createConcurrentLockFreeLazy(nullSupplier));
    }

    @Test
    public void multipleConcurrentLockFreeLazyOneSupplier() throws Exception {
        for (int i = 1; i <= 5; i++) {
            Lazy<Integer> integerLazy = LazyFactory.createConcurrentLockFreeLazy(numberSupplier);
            Integer result = integerLazy.get();
            assertEquals(TEST_NUMBER, result.intValue());
            assertSame(result, integerLazy.get());
            assertEquals(i, callsToNumberSupplier);
        }
    }

    private void testMultipleThreads(Lazy<Integer> integerLazy, boolean isLockFree) throws Exception {
        Thread[] threads = new Thread[THREADS_NUMBER];
        Integer[] result = new Integer[THREADS_NUMBER];

        for (int i = 0; i < THREADS_NUMBER; i++) {
            int j = i;
            threads[i] = new Thread(() -> result[j] = integerLazy.get());
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        for (int i = 0; i < THREADS_NUMBER; i++) {
            assertEquals(TEST_NUMBER, result[i].intValue());
            if (i != 0) {
                assertSame(result[i - 1], result[i]);
            }
        }
        if (!isLockFree) {
            assertEquals(1, callsToSlowSupplier);
        }
    }

    @Test
    public void concurrentLazyMultipleThreads() throws Exception {
        testMultipleThreads(LazyFactory.createConcurrentLazy(slowSupplier), false);
    }

    @Test
    public void concurrentLockFreeLazyMultipleThreads() throws Exception {
        testMultipleThreads(LazyFactory.createConcurrentLockFreeLazy(slowSupplier), true);
    }

}