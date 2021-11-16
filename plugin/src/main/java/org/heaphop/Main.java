package org.heaphop;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        LinkedList ll = new LinkedList();
//        ll.value = 1;
//        ll.ll = new LinkedList();
//        ll.ll.value = 2;
//        ll.ll.ll = new LinkedList();
//        ll.ll.ll.value = 3;
//        ll.ll.ll.ll = new LinkedList();
//        ll.ll.ll.ll.value = 4;
//        System.out.println(ll.getState());
//        DrawingServer drawingServer = new DrawingServer("http://localhost:24564", "../frontend/server.js");
//        try {
//            drawingServer.sendPostRequest("/query", ll.getState());
//            drawingServer.checkStatus();
//        } finally {
//            drawingServer.stopServer();
//        }
//        TimeUnit.SECONDS.sleep(1);
//        drawingServer.checkStatus();
//        TimeUnit.SECONDS.sleep(1);
//        drawingServer.checkStatus();
//        TimeUnit.SECONDS.sleep(1);
//        drawingServer.checkStatus();
    }

    static class LinkedList implements Visualizer {
        int value;
        String nume = "Andrei";
        LinkedList ll;
    }
}
