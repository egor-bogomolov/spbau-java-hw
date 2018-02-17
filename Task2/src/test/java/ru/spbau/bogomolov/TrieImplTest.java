package ru.spbau.bogomolov;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.junit.Assert.*;

/**
 * Created by egor on 9/21/16.
 */
public class TrieImplTest {

    @Test
    public void addRemove() throws Exception {
        TrieImpl trie = new TrieImpl();
        String s = "asbsdvzZSDASCDA";
        assertTrue(trie.add(s));
        assertEquals(1, trie.size());
        assertTrue(trie.contains(s));
        assertTrue(trie.remove(s));
        assertEquals(0, trie.size());
        assertFalse(trie.contains(s));
    }

    @Test
    public void addRemoveAndCheckPrefix() throws Exception {
        TrieImpl trie = new TrieImpl();
        String s = "ASDASCDZasbsdvaz";
        assertTrue(trie.add(s));
        assertTrue(trie.contains(s));
        String t = "";
        for (int i = 0; i < s.length(); i++) {
            t = t + s.charAt(i);
            assertEquals(1, trie.howManyStartsWithPrefix(t));
        }
        assertTrue(trie.remove(s));
        assertFalse(trie.contains(s));
        t = "";
        for (int i = 0; i < s.length(); i++) {
            t = t + s.charAt(i);
            assertEquals(0, trie.howManyStartsWithPrefix(t));
        }
    }

    @Test
    public void doesntContainPrefix() throws Exception {
        TrieImpl trie = new TrieImpl();
        String s = "ASDASCDZasbsdvaz";
        trie.add(s);
        String prefix = "";
        for (int i = 0; i < s.length(); i++) {
            assertFalse(trie.contains(prefix));
            prefix = prefix + s.charAt(i);
        }
        assertTrue(trie.contains(prefix));
    }

    @Test
    public void doesntRemovePrefix() throws Exception {
        TrieImpl trie = new TrieImpl();
        String s = "ASDASCDZasbsdvaz";
        assertTrue(trie.add(s));
        String prefix = "ASDASCDZasbsdva";
        assertFalse(trie.add(s));
        assertTrue(trie.add(prefix));
        assertTrue(trie.contains(s));
        assertTrue(trie.contains(prefix));
        assertEquals(2, trie.size());
        assertTrue(trie.remove(s));
        assertFalse(trie.contains(s));
        assertTrue(trie.contains(prefix));
        assertEquals(1, trie.size());
    }

    @Test
    public void multiAdd() throws Exception {
        TrieImpl trie = new TrieImpl();
        String s = "ASDASCDZasbsdvaz";
        for (int i = 0; i < 10; i++) {
            if (i > 0) assertFalse(trie.add(s));
            else assertTrue(trie.add(s));
            assertEquals(1, trie.size());
        }
    }

    @Test
    public void multiRemove() throws Exception {
        TrieImpl trie = new TrieImpl();
        String s = "ASDASCDZasbsdvaz";
        assertTrue(trie.add(s));
        for (int i = 0; i < 10; i++) {
            if (i > 0) assertFalse(trie.remove(s));
            else assertTrue(trie.remove(s));
            assertEquals(0, trie.size());
        }
    }

    @Test
    public void serializeAndDeserialize() throws Exception {
        TrieImpl trieOut = new TrieImpl();
        trieOut.add("aaaa");
        trieOut.add("aaab");
        trieOut.add("c");
        FileOutputStream fout = new FileOutputStream("serialize.out");
        trieOut.serialize(fout);
        fout.close();

        TrieImpl trieIn = new TrieImpl();
        FileInputStream fin = new FileInputStream("serialize.out");
        trieIn.deserialize(fin);
        fin.close();

        assertEquals(3, trieIn.size());
        assertTrue(trieIn.contains("aaaa"));
        assertTrue(trieIn.contains("aaab"));
        assertTrue(trieIn.contains("c"));
    }

}