package org.heaphop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class Check extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) {
            return;
        }
        if (SharedData.getInstance().project == null) {
            SharedData.getInstance().project = e.getProject();
        }

        if (SharedData.getInstance().drawingServer == null) {
            Notifier.notifyInformation("The server is stopped!");
        }
        else {
            SharedData.getInstance().drawingServer.checkStatus();
        }
    }
}
