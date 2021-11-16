package org.heaphop.webviewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.CefApp;
import org.heaphop.DrawingServer;
import org.heaphop.Main;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.heaphop.Visualizer;

class LinkedList implements Visualizer {
    int value;
    String nume = "Andrei";
    LinkedList ll;
}

class WebViewerWindow {

    Project project;
    private JBCefBrowser webView;



    public WebViewerWindow(Project project) {
        this.project = project;
        initWebView();
    }

    private void initWebView() {
        webView = new JBCefBrowser();
        registerAppSchemeHandler();
        LinkedList ll = new LinkedList();
        ll.value = 1;
        ll.ll = new LinkedList();
        ll.ll.value = 2;
        ll.ll.ll = new LinkedList();
        ll.ll.ll.value = 3;
        ll.ll.ll.ll = new LinkedList();
        ll.ll.ll.ll.value = 4;
        System.out.println(ll.getState());
        DrawingServer drawingServer = new DrawingServer("http://localhost:24563", "../frontend/server.js");
        try {
            drawingServer.sendPostRequest("/query", ll.getState());
        } finally {
            drawingServer.stopServer();
        }
        Path tmp = Paths.get(System.getenv("TMP"), "heap-hop", "index.html");
        webView.loadURL(tmp.toString());
        Disposer.register(project, webView);
    }

    public JComponent content() {
        return webView.getComponent();
    }

    private void registerAppSchemeHandler() {
        CefApp.getInstance().registerSchemeHandlerFactory(
                        "http",
                        "",
                        new CustomSchemeHandlerFactory()
                );
    }
}
