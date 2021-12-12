package org.heaphop;

import com.intellij.openapi.util.io.FileUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class DrawingServer {
    public static Process process;
    private final String baseURI;

    public DrawingServer(String baseURI, String pathToServer) {
        this.baseURI = baseURI;
        startServer(pathToServer);
        checkStatus();

        //System.out.println(new File(".").getAbsolutePath());

//        for (var path : new File("../").list()) {
//            System.out.println(path);
//        }

        Path sourceDirectory = Paths.get(Config.pathToResources, "modelProject", "heap-hop");
        Path targetDirectory = Paths.get(System.getenv("TMP"), "heap-hop");
        try {
            FileUtil.copyDir(sourceDirectory.toFile(), targetDirectory.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendPostRequest(String URI, JSONObject json) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(baseURI.concat(URI));
            StringEntity entity = new StringEntity(json.toString());

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "text/plain");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                System.out.println("Response code: " + httpResponse.getStatusLine().getStatusCode());
            } else {
                return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void startServer(String pathToServer) {
        try {
            if (process == null || !process.isAlive()) {
                process = Runtime.getRuntime().exec(String.format("cmd /c node %s", pathToServer));
//                process = Runtime.getRuntime().exec(String.format("cmd /c cat %s", pathToServer));
            }
//            Scanner s = new Scanner(process.getInputStream()).useDelimiter("\\A");
//            String result = s.hasNext() ? s.next() : "";
//            System.out.println(result);
//
//            s = new Scanner(process.getErrorStream()).useDelimiter("\\A");
//            result = s.hasNext() ? s.next() : "";
//            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkStatus() {
        System.out.println(DrawingServer.process.isAlive());
        System.out.println(DrawingServer.process.info());
        System.out.println(DrawingServer.process.pid());
        try {
            System.out.println(DrawingServer.process.exitValue());
        } catch (Exception ignored) {
    }}

    public void stopServer() {
        if (process.isAlive()) {
            process.children().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
            //Runtime.getRuntime().exec("cmd /c taskkill /PID " + process.pid() + " /F");
        }
    }
}
