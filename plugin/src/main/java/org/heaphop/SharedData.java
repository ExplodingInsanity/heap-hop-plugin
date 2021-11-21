package org.heaphop;

import webviewer.WebViewerWindow;

public class SharedData {

    private static final SharedData sharedData = new SharedData();
    public DrawingServer drawingServer;
    public WebViewerWindow webViewerWindow;

    private SharedData() {
    }

    public static SharedData getInstance() {
        return sharedData;
    }
}
