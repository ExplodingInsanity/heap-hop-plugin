package org.heaphop;

import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {
        LinkedList ll = new LinkedList();
        ll.value = 1;
        ll.ll = new LinkedList();
        lll.ll.value = 2;
        ll.ll.ll = new LinkedList();
        ll.ll.ll.value = 3;
        ll.getState();
        // TODO: change the file path from test.js to something else
        DrawingServer drawingServer = new DrawingServer("localhost:24567", "test.js");
        drawingServer.sendPostRequest("/query", fields);
        drawingServer.stopServer();
    }

    static class LinkedList implements Visualizer {
        int value;
        LinkedList ll;
    }
}
