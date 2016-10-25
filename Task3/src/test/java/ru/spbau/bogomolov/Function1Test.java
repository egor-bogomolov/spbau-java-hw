package ru.spbau.bogomolov;

import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;

public class Function1Test {
    @Test
    public void apply() throws Exception {
        Function1<Integer, Integer> square = x -> x * x;
        for (int x = -100; x <= 100; x++) {
            assertEquals(x * x, square.apply(x).intValue());
        }
    }

    @Test
    public void compose() throws Exception {
        Function1<Object, String> toString = x -> x.toString();
        Function1<String, Integer> andBack = s -> new Integer(s);
        for (Integer x = -100; x <= 100; x++) {
            assertEquals(x.intValue(), toString.compose(andBack).apply(x).intValue());
        }
    }

}