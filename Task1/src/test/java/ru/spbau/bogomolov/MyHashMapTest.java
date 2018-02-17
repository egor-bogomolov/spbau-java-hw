package ru.spbau.bogomolov;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Egor Bogomolov on 9/19/16.
 */

public class MyHashMapTest {
    @Test
    public void put() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 1000; i++) {
            hashMap.put(s, s);
            assertTrue(hashMap.contains(s));
            s += 'a';
        }
    }

    @Test
    public void removeAfterEachPut() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 1000; i++) {
            hashMap.put(s, s);
            assertTrue(s.equals(hashMap.remove(s)));
            assertFalse(hashMap.contains(s));
            s += 'a';
        }
    }

    @Test
    public void removeAfterAllPuts() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 1000; i++) {
            hashMap.put(s, s);
            s += 'a';
        }
        s = "";
        for (int i = 0; i < 1000; i++) {
            assertNotNull(hashMap.remove(s));
            s += 'a';
        }
    }

    @Test
    public void removeFromEmpty() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 10; i++) {
            assertNull(hashMap.remove(s));
            s += 'a';
        }
    }

    @Test
    public void get() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 10; i++) {
            hashMap.put(s, s);
            assertTrue(s.equals(hashMap.get(s)));
            hashMap.remove(s);
            s += 'a';
        }
    }

    @Test
    public void clearAndSize() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 1000; i++) {
            hashMap.put(s, s);
            assertEquals(1, hashMap.size());
            if (i % 40 == 0) {
                hashMap.clear();
                assertEquals(0, hashMap.size());
            }
        }
    }
}