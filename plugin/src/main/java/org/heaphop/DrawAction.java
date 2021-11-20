package org.heaphop;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
public class DrawAction extends AnAction {

    public void createAndCopyDir(String destP, String sourceP)
    {
        new File(destP).mkdirs();
        File source = new File(sourceP);
        File dest = new File(destP);
        try {
            FileUtils.copyDirectory(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   static class LinkedList implements Visualizer {
        int value;
        String nume = "Andrei";
        LinkedList ll;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        LinkedList ll = new LinkedList();
        ll.value = 1;
        ll.ll = new LinkedList();
        ll.ll.value = 2;
        ll.ll.ll = new LinkedList();
        ll.ll.ll.value = 3;
        ll.ll.ll.ll = new LinkedList();
        ll.ll.ll.ll.value = 4;
        System.out.println(ll.getState());

        System.out.println(DrawingServer.process.isAlive());
        String pathToHTML = SharedData.getInstance().drawingServer.sendPostRequest("/query", ll.getState());
        System.out.println(pathToHTML);
        if (pathToHTML != null) {
                System.out.println(pathToHTML);
                File file = new File(pathToHTML);
            try {
                Runtime.getRuntime().exec(String.format(
                        "cmd /c start chrome %s",
                        file.getAbsolutePath()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            //SharedData.getInstance().webViewerWindow.updateContent(pathToHTML);
        }
        //        DrawingServer.sendPostRequest("/query", ll.getState());

        Project project  = e.getProject();
        if (project == null) {
            return;
        }
        String pathToSource = project.getBasePath(); // Path to the main folder of the user's project
        String pathToDestination = System.getenv("TMP").concat("/heap-hop/src/");
        this.createAndCopyDir(pathToDestination, pathToSource);
    }
}