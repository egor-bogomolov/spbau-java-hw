package ru.spbau.bogomolov;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CollectionsTest {
    private List<Integer> array;
    private List<Integer> onlyEven;
    private List<Integer> less50;
    private List<Integer> mod3;
    private List<String> letters;

    @Before
    public void init() {
        array = new ArrayList<>();
        onlyEven = new ArrayList<>();
        less50 = new ArrayList<>();
        mod3 = new ArrayList<>();
        letters = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f"));
        for (int i   = 0; i < 100; i++) {
            array.add(i);
            if (i % 2 == 0) onlyEven.add(i);
            if (i < 50) less50.add(i);
            mod3.add(i % 3);
        }

    }

    @Test
    public void map() throws Exception {
        Function1<Integer, Integer> func = x -> x % 3;
        assertEquals(mod3, Collections.map(func, array));
    }

    @Test
    public void filter() throws Exception {
        Predicate<Integer> pred = x -> x % 2 == 0;
        assertEquals(onlyEven, Collections.filter(pred, array));
    }

    @Test
    public void takeWhile() throws Exception {
        Predicate<Integer> pred = x -> x < 50;
        assertEquals(less50, Collections.takeWhile(pred, array));
    }

    @Test
    public void takeUnless() throws Exception {
        Predicate<Integer> pred = x -> x >= 50;
        assertEquals(less50, Collections.takeUnless(pred, array));
    }

    @Test
    public void foldr() throws Exception {
        Function2<String, String, String> concat = (s, t) -> s + t;
        assertEquals("abcdef", Collections.foldr(concat, "", letters));
    }

    @Test
    public void foldl() throws Exception {
        Function2<String, String, String> concat = (s, t) -> s + t;
        assertEquals("abcdef", Collections.foldl(concat, "", letters));
    }

}