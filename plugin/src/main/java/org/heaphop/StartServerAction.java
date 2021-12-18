package org.heaphop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StartServerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) {
            return;
        }
        if (SharedData.getInstance().project == null) {
            SharedData.getInstance().project = e.getProject();
        }

        File sourceDirectory = Paths.get(Config.pathToResources, "modelProject", "heap-hop").toFile();
        File targetDirectory = Paths.get(System.getenv("TMP"), "heap-hop").toFile();
        if (targetDirectory.exists()) {
            FileUtil.delete(targetDirectory);
        }

        try {
            FileUtil.copyDir(sourceDirectory, targetDirectory);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        SharedData.getInstance().drawingServer = new DrawingServer(Config.urlToNodeServer, Config.pathToNodeServer);
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
