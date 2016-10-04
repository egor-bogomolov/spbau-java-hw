package ru.spbau.bogomolov;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class TrieImplTest {
    private TrieImpl trie;

    @Before
    public void init() {
        trie = new TrieImpl();
    }

    @Test
    public void addAndRemove() {
        assertTrue(trie.add("aaaa"));
        assertTrue(trie.contains("aaaa"));
        assertEquals(1, trie.size());
        assertTrue(trie.remove("aaaa"));
        assertFalse(trie.contains("aaaa"));
        assertEquals(0, trie.size());
    }

    @Test
    public void addTestPrefixAndRemove() {
        assertTrue(trie.add("ZZ"));
        assertFalse(trie.contains(""));
        assertFalse(trie.contains("Z"));
        assertTrue(trie.contains("ZZ"));
        assertEquals(1, trie.howManyStartsWithPrefix(""));
        assertEquals(1, trie.howManyStartsWithPrefix("Z"));
        assertEquals(1, trie.howManyStartsWithPrefix("ZZ"));

        assertTrue(trie.remove("ZZ"));
        assertFalse(trie.contains(""));
        assertFalse(trie.contains("Z"));
        assertFalse(trie.contains("ZZ"));
        assertEquals(0, trie.howManyStartsWithPrefix(""));
        assertEquals(0, trie.howManyStartsWithPrefix("Z"));
        assertEquals(0, trie.howManyStartsWithPrefix("ZZ"));
    }

    @Test
    public void multiAdd() {
        String testString1 = "abcdefghijklmnopqrstuvwxyz";
        String testString2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertTrue(trie.add(testString1));
        assertTrue(trie.add(testString2));
        for (int i = 0; i < 10; i++) {
            assertFalse(trie.add(testString1));
            assertFalse(trie.add(testString2));
            assertEquals(2, trie.size());
            assertTrue(trie.contains(testString1));
            assertTrue(trie.contains(testString2));
        }
        assertTrue(trie.remove(testString1));
        assertTrue(trie.remove(testString2));
    }

    @Test
    public void multiRemove() {
        String testString1 = "abcdefghijklmnopqrstuvwxyz";
        String testString2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertTrue(trie.add(testString1));
        assertTrue(trie.add(testString2));
        assertTrue(trie.remove(testString1));
        assertTrue(trie.remove(testString2));
        for (int i = 0; i < 10; i++) {
            assertFalse(trie.remove(testString1));
            assertFalse(trie.remove(testString2));
            assertEquals(0, trie.size());
            assertFalse(trie.contains(testString1));
            assertFalse(trie.contains(testString2));
        }
    }

    @Test
    public void emptyStrings() {
        assertTrue(trie.add(""));
        assertFalse(trie.add(""));
        assertTrue(trie.contains(""));
        assertEquals(1, trie.size());
        assertTrue(trie.remove(""));
        assertFalse(trie.remove(""));
        assertFalse(trie.contains(""));
        assertEquals(0, trie.size());
    }

    @Test
    public void size() {
        StringBuilder testString = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            assertEquals(i, trie.size());
            testString.append('a' + i);
            assertTrue(trie.add(testString.toString()));
            assertTrue(trie.contains(testString.toString()));
            assertEquals(i + 1, trie.size());
        }
        testString = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            assertEquals(26 - i, trie.size());
            testString.append('a' + i);
            assertTrue(trie.remove(testString.toString()));
            assertFalse(trie.contains(testString.toString()));
            assertEquals(26 - i - 1, trie.size());
        }
    }

    @Test
    public void serializeAndDeserialize() throws IOException, ClassNotFoundException {
        trie.add("aaaa");
        trie.add("aaab");
        trie.add("Z");

        FileOutputStream fout = new FileOutputStream("serialize.out");
        trie.serialize(fout);
        fout.close();

        TrieImpl newTrie = new TrieImpl();
        FileInputStream fin = new FileInputStream("serialize.out");
        newTrie.deserialize(fin);
        fin.close();

        assertEquals(3, newTrie.size());
        assertTrue(newTrie.contains("aaaa"));
        assertTrue(newTrie.contains("aaab"));
        assertTrue(newTrie.contains("Z"));
        assertEquals(2, newTrie.howManyStartsWithPrefix("a"));
    }

}