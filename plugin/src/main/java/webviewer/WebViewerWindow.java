package webviewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.CefApp;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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
