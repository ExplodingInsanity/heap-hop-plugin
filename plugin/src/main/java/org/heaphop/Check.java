package org.heaphop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class Check extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SharedData.getInstance().drawingServer.checkStatus();
    }
}
