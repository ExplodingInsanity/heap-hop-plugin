package org.heaphop;

import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {
        LinkedList ll = new LinkedList();
        ll.ll = new LinkedList();
        ll.ll.value = new int[]{1,2,3};
        JSONObject fields = ll.getState();

        DrawingServer drawingServer = new DrawingServer("localhost:24567", "test.bat");
        drawingServer.sendPostRequest("/query", fields);
//        drawingServer.stopServer();
    }

    static class LinkedList implements Visualizer {
        int[] value;
        LinkedList ll;
    }
}
