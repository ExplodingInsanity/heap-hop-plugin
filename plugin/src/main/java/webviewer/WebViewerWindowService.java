package webviewer;

import com.intellij.openapi.project.Project;

public class WebViewerWindowService {
    Project project;
    WebViewerWindow webViewerWindow;

    public WebViewerWindowService(Project project) {
        this.project = project;
        webViewerWindow = new WebViewerWindow(project);
    }
}