package org.heaphop;

public class Main {
    public static void main(String[] args) {
        LinkedList ll = new LinkedList();
        ll.value = 1;
        ll.ll = new LinkedList();
        ll.ll.value = 2;
        ll.ll.ll = new LinkedList();
        ll.ll.ll.value = 3;
        ll.ll.ll.ll = new LinkedList();
        ll.ll.ll.ll.value = 4;
        System.out.println(ll.getState());
//        DrawingServer drawingServer = new DrawingServer("http://localhost:24563", "../frontend/server.js");
//        try {
//            drawingServer.sendPostRequest("/query", ll.getState());
//        } finally {
//            drawingServer.stopServer();
//        }
    }

    static class LinkedList implements Visualizer {
        int value;
        String nume = "Andrei";
        LinkedList ll;
    }
}
