package ru.spbau.bogomolov;

import junit.framework.TestFailure;
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

    @Test
    public void laziness() throws Exception {
        Predicate<Integer> pred2 = x -> (x % 2 == 0);
        Predicate<Object> pred3 = x -> {
            assertFalse(true);
            return true;
        };

        Predicate<Integer> predAnd = pred2.and(pred3);
        Predicate<Integer> predOr  = pred2.or(pred3);

        assertFalse(predAnd.apply(1));
        assertTrue(predOr.apply(2));
    }

}