package org.heaphop;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LinkedList ll = new LinkedList();
//        ll.add(1);
//        ll.add(2);
//        ll.add(3);
//        ll.add(4);
//        ll.add(5);

//        ll.add(new int[]{1, 2, 3}, 1);
//        ll.add(new int[]{3, 5}, 2);
//        ll.add(new int[]{5, 6, 3}, 3);
//        ll.add(new int[]{7}, 4);
//        ll.add(new int[]{}, 5);
//        ll.add(new int[]{9, 8, 7, 6, 5}, 6);
        ll.add(Map.ofEntries(Map.entry(1, "a"), Map.entry(2, "b")), 1);
        ll.add(Map.ofEntries(Map.entry(6, "buna")), 2);
        ll.add(Map.ofEntries(Map.entry(4, "ziua"), Map.entry(2, "b")), 3);
        ll.add(Map.ofEntries(Map.entry(9, "mai"), Map.entry(2, "flacai")), 4);
        System.out.println(ll.getState());
        PseudoDrawingServer drawingServer = new PseudoDrawingServer("http://localhost:24564", "C:\\Users\\alexs\\IdeaProjects\\pluginDevelopment\\heap-hop-plugin\\frontend\\server.js");
        drawingServer.sendPostRequest("/query", ll.getState());
    }

    static class LinkedList implements PseudoVisualizer {
        private String msg = "Hello";
        private Node start = null;

//        void add(int value) {
//            this.start = new Node(value, this.start);
//        }

        //        void add(int[] values, int id) {
        void add(Map<Integer, String> values, int id) {
            this.start = new Node(values, id, this.start);
        }
    }

    static class Node implements PseudoVisualizer {
        //        private int value;
//        private int[] values;
        Map<Integer, String> values;
        private int id;
        Node next;

//        public Node(int value, Node next) {
//            this.value = value;
//            this.next = next;
//        }

        //        public Node(int[] values, int id, Node next) {
        public Node(Map<Integer, String> values, int id, Node next) {
            this.values = values;
            this.id = id;
            this.next = next;
        }
    }
}
