package ru.spbau.bogomolov;

import java.io.*;

/**
 * Created by egor on 10/4/16.
 */

public class TrieImpl implements Trie, StreamSerializable {
    private Node root;

    public TrieImpl() {
        root = new Node();
    }

    /**
     * Write current state to output stream.
     */
    public void serialize(OutputStream out) throws IOException {
        ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(root);
    }

    /**
     * Replace current state with data from input stream.
     */
    public void deserialize(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream objectIn = new ObjectInputStream(in);
        root = (Node) objectIn.readObject();
    }

    /**
     * Return node corresponding to string path if it exists.
     * Otherwise return null.
     */
    private Node goDown(String path) {
        Node node = root;
        for (int i = 0; i < path.length() && node != null; i++) {
            node = node.getNext(path.charAt(i));
        }
        return node;
    }

    /**
     * Return node corresponding to string path.
     * If it doesn't exist - create it.
     */
    private Node goDownAndCreate(String path) {
        Node node = root;
        for (int i = 0; i < path.length(); i++) {
            node = node.createNext(path.charAt(i));
        }
        return node;
    }

    /**
     * Change number of achievable terminal nodes for all
     * nodes, corresponding to prefixes of path.
     * Delete unused nodes.
     */
    private void addOnPath(String path, int value) {
        Node node = root;
        root.numberOfTerminal += value;
        for (int i = 0; i < path.length(); i++) {
            Node next = node.getNext(path.charAt(i));
            next.numberOfTerminal += value;
            if (next.numberOfTerminal == 0) {
                node.removeNext(path.charAt(i));
                break;
            }
            node = next;
        }
    }

    /**
     * Add a string to trie and return true.
     * If the string is already in trie - return false
     */
    public boolean add(String element) {
        Node node = goDownAndCreate(element);
        if (!node.isTerminal) {
            node.isTerminal = true;
            addOnPath(element, 1);
            return true;
        }
        return false;
    }

    /**
     * Check whether the trie contains string or not.
     */
    public boolean contains(String element) {
        Node node = goDown(element);
        return node != null && node.isTerminal;
    }

    /**
     * Remove a string from trie and return true.
     * If the string isn't in trie - return false.
     */
    public boolean remove(String element) {
        Node node = goDown(element);
        if (node != null && node.isTerminal) {
            node.isTerminal = false;
            addOnPath(element, -1);
            return true;
        }
        return false;
    }

    /**
     * Return number of strings in the trie.
     */
    public int size() {
        return root.numberOfTerminal;
    }

    /**
     * Return number of strings starting with prefix in the trie
     */
    public int howManyStartsWithPrefix(String prefix) {
        Node node = goDown(prefix);
        return node == null ? 0 : node.numberOfTerminal;
    }

    private class Node implements Serializable {
        private static final int ALPHABET_SIZE = 52;
        Node[] next;
        boolean isTerminal;   // node is terminal when there is a string ending in it
        int numberOfTerminal; // number of terminal nodes in subtree

        public Node() {
            next = new Node[ALPHABET_SIZE];
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                next[i] = null;
            }
            numberOfTerminal = 0;
            isTerminal = false;
        }

        private int getIndex(char c) {
            if ('a' <= c && c <= 'z') {
                return c - 'a';
            } else {
                return 26 + (c - 'A');
            }
        }

        /**
         * Return the node that letter c leads to or null if it doesn't exist
         */
        public Node getNext(char c) {
            int i = getIndex(c);
            return next[i];
        }

        /**
         * If there is no edge with letter c from the node - create it and return new node
         * Otherwise return the node that letter c leads to
         */
        public Node createNext(char c) {
            int i = getIndex(c);
            if (next[i] == null) {
                next[i] = new Node();
            }
            return next[i];
        }

        /**
         * Delete the node that letter c leads to
         */
        public void removeNext(char c) {
            int i = getIndex(c);
            next[i] = null;
        }

        //Implementing "serializable"
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
            out.writeInt(numberOfChildren);
            out.writeInt(numberOfTerminal);
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                if (next[i] != null) {
                    out.writeInt(i);
                    out.writeObject(next[i]);
                }
            }
        }

        //Implementing "serializable"
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            next = new Node[ALPHABET_SIZE];
            int tmp = in.readInt();
            if (tmp == 1) {
                isTerminal = true;
            } else {
                isTerminal = false;
            }
            int numberOfChildren = in.readInt();
            numberOfTerminal = in.readInt();
            for (int i = 0; i < numberOfChildren; i++) {
                int index = in.readInt();
                next[index] = (Node) in.readObject();
            }
        }

    }
}
