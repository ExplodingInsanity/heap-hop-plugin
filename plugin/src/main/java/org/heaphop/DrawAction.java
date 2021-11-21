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
import java.util.Scanner;
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
    private final String destination = System.getenv("TMP").concat("/heap-hop/src/main/java/Main.java");
    private final String sourcePath = System.getenv("TMP").concat("/heap-hop/src/main/java/Main.java");;

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

    Function<Stream<String>, List<String>> findClassName = ss ->
            ss.filter(s -> s.contains("static class"))
                    .map(s -> Arrays.stream(s.split(" "))
                            .dropWhile(sp -> !sp.equals("class"))
                            .skip(1)
                            .findFirst().get())
                    .collect(Collectors.toList());

    BiFunction<Stream<String>, List<String>, Stream<String>> addExtends = (ss, cls) ->
            ss.map(s -> {
                if (s.contains("static class")){
                    return Arrays.stream(s.split(" ")).map(w -> {
                        if (cls.stream().anyMatch(w::equals)) {
                            return w + " implements Visualizer";
                        }else{
                            return w;
                        }
                    }).reduce("", (a, b) -> a + " " + b);
                }else{
                    return s;
                }
            });

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

    BiConsumer<Path, Function<String, Stream<String>>> writeToFile = (f, fss) -> {
        try {
            BufferedReader objReader = new BufferedReader(new FileReader(String.valueOf(f)));

            List<String> org = objReader.lines().collect(Collectors.toList());

            Integer position = getInsertPosition.apply(org.stream());

            Stream<String> startingLines = org.stream().limit(position);

            List<String> className = findClassName.apply(org.stream());
            String variableName = findVariableName.apply(org.stream(), className);
            Stream<String> middleLines = fss.apply(variableName);

            Stream<String> endingLines = org.stream().skip(position);

            Stream<String> finalLines = addExtends.apply(Stream.concat(startingLines, Stream.concat(middleLines, endingLines)), className);

            Files.write(Path.of(destination), finalLines.collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project  = e.getProject();
        if (project == null) {
            return;
        }
        String pathToSource = project.getBasePath().concat("/src/"); // Path to the main folder of the user's project
        String pathToDestination = System.getenv("TMP").concat("/heap-hop/src/main/java/");
        this.createAndCopyDir(pathToDestination, pathToSource);

        Function<String, Stream<String>> txt2insert = s ->
                Stream.of
                        ( "\t\tDrawingServer drawingServer = new DrawingServer(\"http://localhost:24564\""
                                        + ", \"" + Config.pathToNodeServer + "\");"
                                , "\tdrawingServer.sendPostRequest(\"/query\", " + s + ".getState());"
                                );

        writeToFile.accept(
                findJavaFiles.apply(sourcePath)
                        .stream()
                        .findFirst()
                        .get()
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

            synchronized (pr) {
                pr.waitFor();
            }

            Runtime.getRuntime().exec(String.format(
                    "cmd /c start chrome %s",
                    Paths.get(System.getenv("TMP"), "heap-hop", "website", "index.html")));
        } catch (InterruptedException | IOException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}