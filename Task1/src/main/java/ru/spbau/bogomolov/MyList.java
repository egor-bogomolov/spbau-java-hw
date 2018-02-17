package ru.spbau.bogomolov;

/**
 * Created by Egor Bogomolov on 9/19/16.
 */

public class MyList {

    private static class Node {
        PairKeyValue stored;
        Node next;
        private Node() {
            next = this;
        }
        private Node(String key, String value) {
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

    public String get(String key) {
        Node node = head;
        while(node.next != node) {
            if (node.stored.key.equals(key)) {
                return node.stored.value;
            }
            node = node.next;
        }
        return null;
    }

    public String put(String key, String value) {
        Node node = head;
        while(node.next != node) {
            if (node.stored.key.equals(key)) {
                String oldValue = node.stored.value;
                node.stored.value = value;
                return oldValue;
            }
            node = node.next;
        }
        Node newNode = new Node(key, value);
        newNode.next = head;
        head = newNode;
        size++;
        return null;
    }

    public String remove(String key) {
        Node node = head;
        if (node.next != node && node.stored.key.equals(key)) {
            String result = node.stored.value;
            head = node.next;
            size--;
            return result;
        }
        while(node.next.next != node.next) {
            if (node.next.stored.key.equals(key)) {
                String result = node.next.stored.value;
                node.next = node.next.next;
                size--;
                return result;
            }
            node = node.next;
        }
        return null;
    }

    public boolean contains(String key) {
        Node node = head;
        while(node.next != node) {
            if (node.stored.key.equals(key)) {
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
