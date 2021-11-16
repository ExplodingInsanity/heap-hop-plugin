package org.heaphop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class StartServerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SharedData.getInstance().drawingServer = new DrawingServer("http://localhost:24564", "D:\\Users\\Adrian\\Desktop\\Faculta\\An3\\Sem1\\PC\\heap-hop-plugin\\frontend\\server.js");
    }
}
