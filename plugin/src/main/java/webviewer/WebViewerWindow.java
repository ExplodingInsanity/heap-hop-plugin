package webviewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.CefApp;
import org.heaphop.SharedData;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WebViewerWindow {

    Project project;
    private JBCefBrowser webView;

    public WebViewerWindow(Project project) {
        this.project = project;
        SharedData.getInstance().webViewerWindow = this;
        initWebView();
    }

    private void initWebView() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        ArrayList<String> args = new ArrayList<>();
        String[] cefArgs = new String[]{"--disable-web-security"};
        //CefApp.getInstance(cefArgs);
        webView = new JBCefBrowser();

        webView.setProperty("javaScriptEnabled", true);

        registerAppSchemeHandler();
//        Path tmp = Paths.get(System.getenv("TMP"), "heap-hop", "index.html");
//        webView.loadURL(tmp.toString());
        Disposer.register(project, webView);
    }

    public void updateContent(String path) {
        if (webView != null) {
            webView.loadURL(path);
        }
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
