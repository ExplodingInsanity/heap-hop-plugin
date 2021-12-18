package org.heaphop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class StartServerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SharedData.getInstance().drawingServer = new DrawingServer(Config.urlToNodeServer, Config.pathToNodeServer);
        if (e.getProject() == null) {
            return;
        }
        String configPath = Paths.get(e.getProject().getBasePath(), "heap_hop.config").toString();
        System.out.println(configPath);
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
