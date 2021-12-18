package org.heaphop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class StopServerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) {
            return;
        }
        if (SharedData.getInstance().project == null) {
            SharedData.getInstance().project = e.getProject();
        }

        DrawingServer drawingServer = SharedData.getInstance().drawingServer;
        if (drawingServer == null) {
            Notifier.notifyError("The server is stopped!");
        } else {
            drawingServer.stopServer();
        }
    }
}
