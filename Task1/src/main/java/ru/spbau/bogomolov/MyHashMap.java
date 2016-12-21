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
        return hashMap[getPositionInMap(key)].contains(key);
    }

    public String get(String key) {
        return hashMap[getPositionInMap(key)].get(key);
    }

    public String put(String key, String value) {
        String oldValue = hashMap[getPositionInMap(key)].put(key, value);
        if (oldValue == null) {
            size++;
        }
        if (size == hashMap.length) {
            extend();
        }
        return oldValue;
    }

    public String remove(String key) {
        String result = hashMap[getPositionInMap(key)].remove(key);
        if (result != null) {
            size--;
        }
        return result;
    }

    public void clear() {
        for (int i = 0; i < hashMap.length; i++) {
            hashMap[i] = new MyList();
        }
        size = 0;
    }

    private void extend() {
        MyList[] oldHashMap = hashMap;
        hashMap = new MyList[2 * hashMap.length];
        for (int i = 0; i < hashMap.length; i++) {
            hashMap[i] = new MyList();
        }
        for (MyList list : oldHashMap) {
            while(!list.empty()) {
                PairKeyValue stored = list.removeHead();
                hashMap[getPositionInMap(stored.key)].put(stored.key, stored.value);
            }
        }
    }

    private int getPositionInMap(String key) {
        int hash = key.hashCode();
        int pos = hash % hashMap.length;
        if (pos < 0) pos += hashMap.length;
        return pos;
    }

}
