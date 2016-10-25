package ru.spbau.bogomolov;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function2Test {
    @Test
    public void apply() throws Exception {
        Function2<Integer, Integer, Integer> multiply = (x, y) -> x * y;
        for (int x = -100; x <= 100; x++) {
            for (int y = -100; y <= 100; y++) {
                assertEquals(x * y, multiply.apply(x, y).intValue());
            }
        }
    }

    @Test
    public void compose() throws Exception {
        Function1<Object, String> toString = x -> x.toString();
        Function2<Integer, Integer, Integer> multiply = (x, y) -> x * y;
        for (int x = -100; x <= 100; x++) {
            for (int y = -100; y <= 100; y++) {
                Integer mult = x * y;
                assertEquals(mult.toString(), multiply.compose(toString).apply(x, y));
            }
        }
    }

    @Test
    public void bind1() throws Exception {
        Function2<Long, Integer, Long> multiply = (x, y) -> x * y;
        Function1<Integer, Long> mult3 = multiply.bind1(3L);
        for (int x = -100; x <= 100; x++) {
            assertEquals(3 * x, mult3.apply(x).longValue());
        }
    }

    @Test
    public void bind2() throws Exception {
        Function2<Long, Integer, Long> multiply = (x, y) -> x * y;
        Function1<Long, Long> mult3 = multiply.bind2(3);
        for (long x = -100; x <= 100; x++) {
            assertEquals(3 * x, mult3.apply(x).longValue());
        }
    }

    @Test
    public void curry() throws Exception {
        Function2<Long, Integer, Long> multiply = (x, y) -> x * y;
        for (long x = -100; x <= 100; x++) {
            for (int y = -100; y <= 100; y++) {
                assertEquals(multiply.apply(x, y).longValue(), multiply.curry().apply(x).apply(y).longValue());
            }
        }
    }

}