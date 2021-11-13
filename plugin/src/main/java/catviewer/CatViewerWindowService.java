package catviewer;

import com.intellij.openapi.project.Project;

class CatViewerWindowService {
    Project project;
    CatViewerWindow catViewerWindow;

    public CatViewerWindowService(Project project) {
        this.project = project;
        catViewerWindow = new CatViewerWindow(project);
    }
}