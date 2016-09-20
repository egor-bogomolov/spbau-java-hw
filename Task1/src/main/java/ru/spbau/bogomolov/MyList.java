package ru.spbau.bogomolov;

/**
 * Created by Egor Bogomolov on 9/19/16.
 */

public class MyList {
    private class Node {
        PairKeyValue stored;
        Node next;
        private Node() {
            next = this;
        }
        private Node(int key, String value) {
            stored = new PairKeyValue(key, value);
            next = this;
        }
    }

    private Node head;
    private int size;

    public MyList() {
        head = new Node();
        size = 0;
    }

    public String get(int key) {
        String result = null;
        Node node = head;
        while(node.next != node) {
            if (node.stored.key == key) {
                result = node.stored.value;
            }
            node = node.next;
        }
        return result;
    }

    public String put(int key, String value) {
        String result = get(key);
        Node node = new Node(key, value);
        node.next = head;
        head = node;
        size++;
        return result;
    }

    public String remove(int key) {
        Node node = head;
        String result = null;
        if (node.next != node && node.stored.key == key) {
            result = node.stored.value;
            head = node.next;
            size--;
            return result;
        }
        while(node.next.next != node.next) {
            if (node.next.stored.key == key) {
                result = node.next.stored.value;
                node.next = node.next.next;
                size--;
                return result;
            }
            node = node.next;
        }
        return result;
    }

    public boolean contains(int key) {
        Node node = head;
        while(node.next != node) {
            if (node.stored.key == key) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    public boolean empty() {
        return (size == 0);
    }

    public PairKeyValue removeHead() {
        PairKeyValue result = head.stored;
        head = head.next;
        size--;
        return result;
    }
}
