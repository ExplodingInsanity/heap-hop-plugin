package org.heaphop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class DrawAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project  = e.getProject();
        if (project == null) {
            return;
        }
        //Messages.showMessageDialog(e.getProject(), project.getBasePath(),"Project Path", Messages.getInformationIcon());

        String pathToProject = project.getBasePath(); // Path to the main folder of the user's project

        // TODO: Work your magic Ionelule
        // TODO: Output %TMP%/heap-hop/src
    }
}
