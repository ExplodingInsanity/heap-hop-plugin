package org.heaphop;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import com.google.common.collect.Streams;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    private final String nameOfClass = "Main";
    private final String nameOfMethod = "main";
    private final String destination = "Main.java";
    private final String sourcePath = "";

    public static class Pair<A, B> {
        public A fst;
        public B snd;
        public Pair(A a, B b){
            fst = a;
            snd = b;
        }
    }



    Function<String, List<Path>> findJavaFiles = path -> {
        try {
            return Files.walk(Paths.get(path))
                    .filter(f -> f.getFileName().toString().equals(nameOfClass.concat(".java")))
                    .collect(Collectors.toList());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    BiConsumer<List<Path>, String> executeMain = (fs, cn) -> {
        fs.forEach(f -> {
            try {
                URL url = f.toUri().toURL();
                URL[] urls = new URL[]{url};
                ClassLoader cl = new URLClassLoader(urls);
                Class<?> cls = cl.loadClass(cn);
                Method getLl = cls.getDeclaredMethod(nameOfMethod, String[].class);
                getLl.invoke(cls.getDeclaredConstructor().newInstance(), (Object) null);
            } catch ( ClassNotFoundException
                    | NoSuchMethodException
                    | InvocationTargetException
                    | InstantiationException
                    | IllegalAccessException
                    | MalformedURLException e)
            { throw new RuntimeException(e); }
        });
    };

    Function<Stream<String>, String> findClassName = ss ->
            ss.filter(s -> s.contains("static class"))
                    .map(s -> Arrays.stream(s.split(" "))
                            .dropWhile(sp -> !sp.equals("class"))
                            .skip(1)
                            .findFirst().get())
                    .findFirst()
                    .get();

    BiFunction<Stream<String>, String, Stream<String>> addExtends = (ss, cls) ->
            ss.map(s -> {
                if (s.contains("static class")){
                    return Arrays.stream(s.split(" ")).map(w -> {
                        if (w.equals(cls)) {
                            return w + " extends Visualizer";
                        }else{
                            return w;
                        }
                    }).reduce("", (a, b) -> a + " " + b);
                }else{
                    return s;
                }
            });

    BiFunction<Stream<String>, String, String> findVariableName = (ss, c) ->
            ss.filter(s -> s.contains(c))
                    .map(s -> Arrays.stream(s.split(" "))
                            .dropWhile(sp -> !sp.equals(c))
                            .skip(1)
                            .findFirst().get())
                    .findFirst()
                    .get();

    BiConsumer<Path, Function<String, Stream<String>>> writeToFile = (f, fss) -> {
        try {
            BufferedReader objReader = new BufferedReader(new FileReader(String.valueOf(f)));

            List<String> org = objReader.lines().collect(Collectors.toList());

            Integer position = 10;

            Stream<String> startingLines = org.stream().limit(position);

            String className = findClassName.apply(org.stream());
            String variableName = findVariableName.apply(org.stream(), className);
            Stream<String> middleLines = fss.apply(variableName);

            Stream<String> endingLines = org.stream().skip(position);

            Stream<String> finalLines = addExtends.apply(Stream.concat(startingLines, Stream.concat(middleLines, endingLines)), className);

            Files.write(Path.of(destination), finalLines.collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;

    };
    // TODO : Move this to "actionPerformed"
    /*


        Function<String, Stream<String>> txt2insert = s ->
                Stream.of
                ( "\t\tDrawingServer drawingServer = new DrawingServer(\"http://localhost:24564\""
                                + ", \"../frontend/server.js\");"
                        , "\t\ttry {"
                        , "\t\t    drawingServer.sendPostRequest(\"/query\", " + s + ".getState());"
                        , "\t\t    drawingServer.checkStatus();"
                        , "\t\t} finally {"
                        , "\t\t    drawingServer.stopServer();"
                        , "\t\t}");

        writeToFile.accept(
                findJavaFiles.apply(sourcePath)
                        .stream()
                        .findFirst()
                        .get()
                , txt2insert);

        executeMain.accept(findJavaFiles.apply(sourcePath), "Main");



     */



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