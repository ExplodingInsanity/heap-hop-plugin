package org.heaphop.webviewer;

import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

class WindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ComponentManager componentManager = ProjectManager.getInstance().getOpenProjects()[0];
        var webViewerWindow = componentManager.getService(WebViewerWindowService.class).webViewerWindow;
        var component = toolWindow.getComponent();
        component.getParent().add(webViewerWindow.content());
    }
}
