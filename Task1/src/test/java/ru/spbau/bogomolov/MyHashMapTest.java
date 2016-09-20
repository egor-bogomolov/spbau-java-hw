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
            assertEquals(true, hashMap.contains(s));
            s += 'a';
        }
    }

    @Test
    public void removeAfterEachPut() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 1000; i++) {
            hashMap.put(s, s);
            assertEquals(true, s.equals(hashMap.remove(s)));
            assertEquals(false, hashMap.contains(s));
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
            assertNotEquals(null, hashMap.remove(s));
            s += 'a';
        }
    }

    @Test
    public void removeFromEmpty() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 10; i++) {
            assertEquals(null, hashMap.remove(s));
            s += 'a';
        }
    }

    @Test
    public void get() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        for (int i = 0; i < 10; i++) {
            hashMap.put(s, s);
            assertEquals(true, s.equals(hashMap.get(s)));
            hashMap.remove(s);
            s += 'a';
        }
    }

    @Test
    public void clearAndSize() throws Exception {
        MyHashMap hashMap = new MyHashMap();
        String s = "";
        int size = 0;
        for (int i = 0; i < 1000; i++) {
            hashMap.put(s, s);
            size++;
            assertEquals(size, hashMap.size());
            if (i % 40 == 0) {
                hashMap.clear();
                size = 0;
                assertEquals(size, hashMap.size());
            }
        }
    }
}