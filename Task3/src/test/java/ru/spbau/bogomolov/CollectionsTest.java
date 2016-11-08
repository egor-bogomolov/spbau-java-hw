package ru.spbau.bogomolov;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

public class CollectionsTest {
    private List<Integer> array;
    private List<Integer> onlyEven;
    private List<Integer> less50;
    private List<Integer> mod3;
    private List<Integer> numbers;

    @Before
    public void init() {
        array = new ArrayList<>();
        onlyEven = new ArrayList<>();
        less50 = new ArrayList<>();
        mod3 = new ArrayList<>();
        numbers = Arrays.asList(2, 2, 2);
        for (int i = 0; i < 100; i++) {
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

    private Integer intPower(int a, int b) {
        int res = 1;
        for (int i = 0; i < b; i++) {
            res *= a;
        }
        return res;
    }

    @Test
    public void testFoldrAndFoldl() throws Exception {
        Function2<Integer, Integer, Integer> power = this::intPower;
        assertEquals(intPower(2, 16), Collections.foldr(power, 2, numbers));
        assertEquals(intPower(2, 8), Collections.foldl(power, 2, numbers));
    }

    @Test
    public void testWildcardsInFolds() throws  Exception {
        Function2<Integer, Object, String> makeStringR = (a, b) -> a.toString() + b.toString();
        Function2<Object, Integer, String> makeStringL = (a, b) -> a.toString() + b.toString();
        assertEquals("222", Collections.foldr(makeStringR, "", numbers));
        assertEquals("222", Collections.foldl(makeStringL, "", numbers));
    }

}