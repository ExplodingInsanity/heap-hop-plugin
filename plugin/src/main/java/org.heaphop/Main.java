package org.heaphop;

public class Main {
    public static void main(String[] args) {
        LinkedList ll = new LinkedList();
        ll.value = 1;
        ll.ll = new LinkedList();
        ll.ll.value = 2;
        ll.ll.ll = new LinkedList();
        ll.ll.ll.value = 3;
        ll.getState();
    }

    static class LinkedList implements Visualizer {
        int value;
        LinkedList ll;
    }
}
