package org.heaphop;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;

import com.google.common.collect.Streams;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
        while (true) {
            try {
                FileUtils.copyDirectory(source, dest);
                System.out.println("Copied user's files");
                break;
            } catch (IOException e) {
                try {
                    System.out.println("Sleeping while the file is locked");
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                //e.printStackTrace();
            }
        }
    }

    private final String nameOfClass = "Main";
    private final String nameOfMethod = "main";
    private final String destination = System.getenv("TMP").concat("/heap-hop/src/main/java/");
    private final String sourcePath = System.getenv("TMP").concat("/heap-hop/src/main/java/");
    private String configPath = null;
    // example file :
    // classes = Main,asd

    public static class Pair<A, B> {
        public A fst;
        public B snd;
        public Pair(A a, B b){
            fst = a;
            snd = b;
        }
    }



    BiFunction<String, List<String>, List<Path>> findJavaFiles = (path, files) -> {
        try {
            return Files.walk(Paths.get(path))
                    //.filter(f -> f.getFileName().toString().equals(nameOfClass.concat(".java")))
                    // f -> C:\afasdf\Main.txt; file -> Main
                    .filter(f -> files.stream().anyMatch(file -> file.concat(".java").equals(f.getFileName().toString())))
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

    Function<Stream<String>, List<String>> findClassName = ss ->
            ss.filter(s -> s.contains("class"))
                    .map(s -> Arrays.stream(s.split(" "))
                            .dropWhile(sp -> !sp.equals("class"))
                            .skip(1)
                            .findFirst().get())
                    .collect(Collectors.toList());

    BiFunction<Stream<String>, List<String>, Stream<String>> addExtends = (ss, cls) ->
            ss.map(s -> {
                if (s.contains("class")){
                    return Arrays.stream(s.split(" ")).map(w -> {
                        if (cls.stream().anyMatch(w::equals) && !w.contains("Main")) {
                            return w + " implements Visualizer";
                        }else{
                            return w;
                        }
                    }).reduce("", (a, b) -> a + " " + b);
                }else{
                    return s;
                }
            });

    Supplier<Stream<String>> getConfig = () -> {
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(configPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Arrays.stream(appProps.getProperty("classes").split(","));
    };

    BiFunction<Stream<String>, List<String>, String> findVariableName = (ss, c) ->
            ss.filter(s -> c.stream().anyMatch(s::contains))
                    .map(s -> Arrays.stream(s.split(" "))
                            .dropWhile(sp -> c.stream().noneMatch(sp::equals))
                            .skip(1)
                            .findFirst().get())
                    .findFirst()
                    .get();

    Function<Stream<String>, Integer> getInsertPosition = ls -> {
        List<Pair<Integer, String >> tuples = Streams.zip(
                Stream.iterate(0, a -> a + 1)
                , ls
                , Pair::new
        ).dropWhile(t -> !t.snd.contains("public static void main"))
                .filter(t -> t.snd.contains("{") || t.snd.contains("}"))
                .skip(1)
                .collect(Collectors.toList());

        Integer counter = 1;
        for (Pair<Integer, String> p : tuples){
            if (p.snd.contains("{")) {
                counter += 1;
            }
            if (p.snd.contains("}")) {
                counter -= 1;
            }
            if(counter == 0) {
                return p.fst;
            }
        }
        return -1;
    };

    BiConsumer<Stream<Path>, Function<String, Stream<String>>> writeToFile = (ff, fss) -> {
        List<String> className = getConfig.get().filter(x -> !x.equals("Main")).collect(Collectors.toList());
        ff.forEach(f ->
        {
            try {
                BufferedReader objReader = new BufferedReader(new FileReader(String.valueOf(f)));

                List<String> org = objReader.lines().collect(Collectors.toList());

                Integer position = getInsertPosition.apply(org.stream());

                List<String> startingLines = position == -1 ? new ArrayList<>() : org.stream().limit(position).collect(Collectors.toList());

                //List<String> className = findClassName.apply(org.stream());

                List<String> endingLines = org.stream().skip(Math.max(0, position)).collect(Collectors.toList());
                //System.out.println(f);
                if(f.getFileName().toString().equals("Main.java")) {
                    String variableName = findVariableName.apply(org.stream(), className);
                    List<String> middleLines = fss.apply(variableName).collect(Collectors.toList());
                    Files.write(Path.of(destination.concat(f.getFileName().toString())),
                            addExtends.apply(Stream.concat(startingLines.stream(), Stream.concat(middleLines.stream(), endingLines.stream())),className)
                                    .collect(Collectors.toList()));
                }
                else{
                    Files.write(Path.of(destination.concat(f.getFileName().toString())),
                            addExtends.apply(Stream.concat(startingLines.stream(), endingLines.stream()),className)
                                    .collect(Collectors.toList()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    };

    @Override
    public synchronized void actionPerformed(@NotNull AnActionEvent e) {
        Project project  = e.getProject();
        if (project == null) {
            return;
        }
        configPath = Paths.get(project.getBasePath(), "heap_hop.config").toString();
        String pathToSource = project.getBasePath().concat("/src/"); // Path to the main folder of the user's project
        String pathToDestination = System.getenv("TMP").concat("/heap-hop/src/main/java/");
        this.createAndCopyDir(pathToDestination, pathToSource);

        Function<String, Stream<String>> txt2insert = s ->
                Stream.of
                        ( "\t\tDrawingServer drawingServer = new DrawingServer(\"http://localhost:24564\""
                                        + ", \"" + Config.pathToNodeServer + "\");"
                                , "\t\tdrawingServer.sendPostRequest(\"/query\", " + s + ".getState());"
                                );

        // getConfig -> Stream<String>
        // List.of("main","asd") -> getConfig.get()
        getConfig.get().forEach(System.out::println);
        List<Path> files = findJavaFiles.apply(sourcePath, getConfig.get().collect(Collectors.toList()));

        //System.out.println(files);
        writeToFile.accept(
                files.stream()
                , txt2insert);

        //executeMain.accept(findJavaFiles.apply(sourcePath), "Main");
        try {
            Process pr = Runtime.getRuntime().exec(String.format(
                "cmd /c %s -p %s run",
                Paths.get(System.getenv("TMP"), "heap-hop", "gradlew.bat"),
                Paths.get(System.getenv("TMP"), "heap-hop")
                ));

            Scanner s = new Scanner(pr.getInputStream()).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            System.out.println(result);

            s = new Scanner(pr.getErrorStream()).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
            System.out.println(result);

            pr.waitFor();

            if (null != SharedData.getInstance().webViewerWindow) {
                SharedData.getInstance().webViewerWindow.updateContent(Config.pathToIndexHtmlFile);
            }
            Runtime.getRuntime().exec(String.format(
                    "cmd /c start chrome %s",
                    Config.pathToIndexHtmlFile));
        } catch (InterruptedException | IOException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}