package org.heaphop;

import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {
        LinkedList ll = new LinkedList();
        ll.value = 1;
        ll.ll = new LinkedList();
        ll.ll.value = 2;
        ll.ll.ll = new LinkedList();
        ll.ll.ll.value = 3;
//        System.out.println(ll.getState());
        DrawingServer drawingServer = new DrawingServer("http://localhost:24567", "../frontend/server.js");
        drawingServer.sendPostRequest("/query", ll.getState());
        drawingServer.stopServer();
    }

    static class LinkedList implements Visualizer {
        int value;
        LinkedList ll;
    }
}
