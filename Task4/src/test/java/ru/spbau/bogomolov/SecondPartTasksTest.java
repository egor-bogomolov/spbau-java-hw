package ru.spbau.bogomolov;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() throws IOException {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        File f1 = folder.newFile("f1");
        File f2 = folder.newFile("f2");
        File f3 = folder.newFile("f3");

        PrintStream printStream = new PrintStream(f1);
        String l1 = "It's a test string";
        printStream.println(l1);
        String l2 = "testing is important";
        printStream.println(l2);
        String l3 = "t e s t s e v e r y w h e r e";
        printStream.println(l3);

        printStream = new PrintStream(f2);
        String l4 = ".....tests......";
        printStream.println(l4);
        String l5 = "TestTESTtEsTtesT";
        printStream.println(l5);
        String l6 = "test";
        printStream.println(l6);

        printStream = new PrintStream(f3);
        String l7 = "";
        printStream.println(l7);
        String l8 = "1234567890"; printStream.println(l8);
        String l9 = "l;aslkdf;asjdf;alskjdftestaksdjf;alskdjf";
        printStream.println(l9);

        List<String> correctResult = Arrays.asList(l1, l2, l4, l6, l9);
        Collections.sort(correctResult);

        List<String> result = SecondPartTasks.findQuotes(
                Arrays.asList(f1.getPath(), f2.getPath(), f3.getPath()), "test");
        Collections.sort(result);

        assertEquals(correctResult, result);
    }

    @Test
    public void testPiDividedBy4() {
        final double PRECISION = 1e-3;
        for (int i = 0; i < 5; i++) {
            assertTrue(Math.abs(SecondPartTasks.piDividedBy4() - Math.PI / 4) < PRECISION);
        }
    }

    @Test
    public void testFindPrinterEmptyMap() {
        assertNull(SecondPartTasks.findPrinter(Collections.emptyMap()));
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> compositions = new HashMap<>();
        compositions.put("1", Arrays.asList("", "", ""));
        compositions.put("2", Arrays.asList("a", "b", "c", "d", "e"));
        compositions.put("3", Arrays.asList("abc", "def", "ghi"));
        compositions.put("4", Arrays.asList("abcd", "efgh"));

        assertEquals("3", SecondPartTasks.findPrinter(compositions));
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> ord1 = new HashMap<>();
        ord1.put("1", 100);
        ord1.put("2", 200);
        ord1.put("3", 300);

        Map<String, Integer> ord2 = new HashMap<>();
        ord2.put("2", 100);
        ord2.put("3", 200);
        ord2.put("4", 300);

        Map<String, Integer> ord3 = new HashMap<>();
        ord3.put("1", 100);
        ord3.put("3", 200);
        ord3.put("5", 300);

        Map<String, Integer> expectedMap = new HashMap<>();
        expectedMap.put("1", 200);
        expectedMap.put("2", 300);
        expectedMap.put("3", 700);
        expectedMap.put("4", 300);
        expectedMap.put("5", 300);

        assertEquals(expectedMap, SecondPartTasks.calculateGlobalOrder(Arrays.asList(ord1, ord2, ord3)));
    }
}