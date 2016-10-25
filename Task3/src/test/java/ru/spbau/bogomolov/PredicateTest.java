package ru.spbau.bogomolov;

import org.junit.Test;

import static org.junit.Assert.*;

public class PredicateTest {
    @Test
    public void and() throws Exception {
        Predicate<Integer> pred2 = x -> (x % 2 == 0);
        Predicate<Integer> pred3 = x -> (x % 3 == 0);
        Predicate<Integer> pred = pred2.and(pred3);
        for (int x = 0; x <= 200; x++) {
            assertEquals((x % 2 == 0) && (x % 3 == 0), pred.apply(x));
        }
    }

    @Test
    public void or() throws Exception {
        Predicate<Integer> pred2 = x -> (x % 2 == 0);
        Predicate<Integer> pred3 = x -> (x % 3 == 0);
        Predicate<Integer> pred = pred2.or(pred3);
        for (int x = 0; x <= 200; x++) {
            assertEquals((x % 2 == 0) || (x % 3 == 0), pred.apply(x));
        }
    }

    @Test
    public void not() throws Exception {
        Predicate<Integer> pred2 = x -> (x % 2 == 0);
        Predicate<Integer> pred3 = x -> (x % 3 == 0);
        Predicate<Integer> pred = pred2.and(pred3).not();
        for (int x = 0; x <= 200; x++) {
            assertEquals(!((x % 2 == 0) && (x % 3 == 0)), pred.apply(x));
        }
    }

}