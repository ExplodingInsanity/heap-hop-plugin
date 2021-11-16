package org.heaphop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class StartServerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SharedData.getInstance().drawingServer = new DrawingServer("http://localhost:24564", "../frontend/server.js");
    }
}
