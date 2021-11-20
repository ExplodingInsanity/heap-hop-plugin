package org.heaphop;

import io.perfmark.Link;

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
        LinkedList ll = new LinkedList();
        System.out.println(ll.getState());
    }

    static class LinkedList implements Visualizer {
        int value;
        String nume = "Andrei";
        int[] a = new int[]{1, 2, 3};
    }
}
