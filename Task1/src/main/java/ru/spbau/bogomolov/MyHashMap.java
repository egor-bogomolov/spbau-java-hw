package ru.spbau.bogomolov;

/**
 * Created by Egor Bogomolov on 9/19/16.
 */

public class MyHashMap {
    private static final int START_SIZE = 5;

    private MyList[] hashMap;
    private int size;

    public MyHashMap() {
        size = 0;
        hashMap = new MyList[START_SIZE];
        for (int i = 0; i < START_SIZE; i++) {
            hashMap[i] = new MyList();
        }
    }

    public int size() {
        return size;
    }

    public boolean contains(String key) {
        int hash = key.hashCode();
        if (hash < 0) {
            hash *= -1;
        }
        return hashMap[hash % hashMap.length].contains(hash);
    }

    public String get(String key) {
        int hash = key.hashCode();
        if (hash < 0) {
            hash *= -1;
        }
        return hashMap[hash % hashMap.length].get(hash);
    }

    public String put(String key, String value) {
        if (size == hashMap.length) {
            extend();
        }
        size++;
        int hash = key.hashCode();
        if (hash < 0) {
            hash *= -1;
        }
        return hashMap[hash % hashMap.length].put(hash, value);
    }

    public String remove(String string) {
        int hash = string.hashCode();
        if (hash < 0) {
            hash *= -1;
        }
        String result = hashMap[hash % hashMap.length].remove(hash);
        if (result != null) {
            size--;
        }
        return result;
    }

    public void clear() {
        for (int i = 0; i < hashMap.length; i++) {
            while(!hashMap[i].empty()) {
                hashMap[i].removeHead();
            }
        }
        size = 0;
    }

    private void extend() {
        MyList[] newHashMap = new MyList[2 * hashMap.length];
        int oldLength = hashMap.length;
        int newLength = newHashMap.length;
        for (int i = 0; i < newLength; i++) {
            newHashMap[i] = new MyList();
        }
        for (int i = 0; i < oldLength; i++) {
            while(!hashMap[i].empty()) {
                PairKeyValue stored = hashMap[i].removeHead();
                newHashMap[stored.key % newLength].put(stored.key, stored.value);
            }
        }
        hashMap = newHashMap;
    }

}
