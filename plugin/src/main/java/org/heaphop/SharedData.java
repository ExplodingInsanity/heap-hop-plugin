package org.heaphop;

import com.intellij.openapi.project.Project;
import webviewer.WebViewerWindow;

public class SharedData {

    private static final SharedData sharedData = new SharedData();
    public DrawingServer drawingServer;
    public WebViewerWindow webViewerWindow;
    public Project project;

    private SharedData() {
    }

    public static SharedData getInstance() {
        return sharedData;
    }
}
