package ru.spbau.bogomolov;

import java.io.*;

/**
 * Created by egor on 9/21/16.
 */

public class TrieImpl implements Trie, StreamSerializable {
    private static final int ALPHABET_SIZE = 52;
    private Node root;

    public TrieImpl() {
        root = new Node();
    }

    public void serialize(OutputStream outStream) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(outStream);
        out.writeObject(root);
    }

    public void deserialize(InputStream inStream) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(inStream);
        root = (Node) in.readObject();
    }

    //If trie contains element - returns node corresponding to it
    //otherwise returns null
    private Node goDown(String element) {
        Node node = root;
        for (int i = 0; i < element.length() && node != null; i++) {
            node = node.getNext(element.charAt(i));
        }
        return node;
    }

    //Add value to number of achievable terminal nodes
    //for all nodes on path corresponding to element
    private void addOnPath(String element, int value) {
        Node node = root;
        Node next;
        node.numberOfAchievable += value;
        for (int i = 0; i < element.length(); i++) {
            next = node.getNext(element.charAt(i));
            next.numberOfAchievable += value;
            if (next.numberOfAchievable == 0) {
                node.remove(element.charAt(i));
                break;
            } else {
                node = next;
            }
        }
    }

    //Add string to trie
    //returns true if string was added
    //returns false if trie already contains string
    public boolean add(String element) {
        Node node = root;
        for (int i = 0; i < element.length(); i++) {
            node = node.add(element.charAt(i));
        }
        if (!node.isTerminal) {
            node.isTerminal = true;
            addOnPath(element, 1);
            return true;
        }
        return false;
    }

    public boolean contains(String element) {
        Node node = goDown(element);
        return node != null && node.isTerminal;
    }

    //Remove string to trie
    //returns true if string was removed
    //returns false if trie doesn't contain string
    public boolean remove(String element) {
        Node node = goDown(element);
        if (node != null && node.isTerminal) {
            node.isTerminal = false;
            addOnPath(element, -1);
            return true;
        }
        return false;
    }

    public int size() {
        return root.numberOfAchievable;
    }

    public int howManyStartsWithPrefix(String prefix) {
        Node node = goDown(prefix);
        return node == null ? 0 : node.numberOfAchievable;
    }

    private class Node implements Serializable {
        private Node[] next;
        private int numberOfAchievable;
        private boolean isTerminal;

        private Node() {
            next = new Node[ALPHABET_SIZE];
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                next[i] = null;
            }
            numberOfAchievable = 0;
            isTerminal = false;
        }

        private int getIndex(char c) {
            if ('a' <= c && c <= 'z') {
                return c - 'a';
            }
            if ('A' <= c && c <= 'Z') {
                return 26 + c - 'A';
            }
            return -1;
        }

        private char getLetter(int i) {
            char res1 = 'a', res2 = 'A';
            if (0 <= i && i < 26) {
                res1 += i;
                return res1;
            } else {
                res2 += (i - 26);
                return res2;
            }
        }

        private Node getNext(char c) {
            int i = getIndex(c);
            return next[i];
        }

        //If node doesn't have an edge with letter c - add it
        private Node add(char c) {
            int i = getIndex(c);
            if (next[i] == null) {
                next[i] = new Node();
            }
            return next[i];
        }

        private void remove(char c) {
            int i = getIndex(c);
            next[i] = null;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            if (isTerminal) {
                out.writeInt(1);
            } else {
                out.writeInt(0);
            }
            int numberOfChildren = 0;
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                if (next[i] != null) {
                    numberOfChildren++;
                }
            }
            out.writeInt(numberOfAchievable);
            out.writeInt(numberOfChildren);
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                if (next[i] != null) {
                    out.writeInt(i);
                    out.writeObject(next[i]);
                }
            }
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            next = new Node[ALPHABET_SIZE];
            int tmp = in.readInt();
            if (tmp == 0) {
                isTerminal = false;
            } else {
                isTerminal = true;
            }
            numberOfAchievable = in.readInt();
            int numberOfChildren = in.readInt();
            for (int i = 0; i < numberOfChildren; i++) {
                int index = in.readInt();
                add(getLetter(index));
                next[index] = (Node) in.readObject();
            }
        }

    }
}
