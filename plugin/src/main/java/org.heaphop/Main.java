package org.heaphop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LinkedList ll = new LinkedList();
        ll.ll = new LinkedList();
        ll.ll.value = new int[]{1,2,3};
        ll.getState();
    }

    static class LinkedList implements Visualizer {
        int[] value;
        LinkedList ll;
    }
}
