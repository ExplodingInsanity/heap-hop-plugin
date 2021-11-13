package catviewer;

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
        var catViewerWindow = componentManager.getService(CatViewerWindowService.class).catViewerWindow;
//        var catViewerWindow = ServiceManager.getService(project, CatViewerWindowService.class).catViewerWindow;
        var component = toolWindow.getComponent();
        component.getParent().add(catViewerWindow.content());
    }
}
