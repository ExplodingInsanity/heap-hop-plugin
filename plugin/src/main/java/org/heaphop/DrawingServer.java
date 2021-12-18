package org.heaphop;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DrawingServer {
    public static Process process;
    private final String baseURI;

    public DrawingServer(String baseURI, String pathToServer) {
        this.baseURI = baseURI;
        startServer(pathToServer);

        //System.out.println(new File(".").getAbsolutePath());

//        for (var path : new File("../").list()) {
//            System.out.println(path);
//        }
    }

    private static void startServer(String pathToServer) {
        try {
            if (process == null || !process.isAlive()) {
                process = Runtime.getRuntime().exec(String.format("cmd /c node %s", pathToServer));
//                process = Runtime.getRuntime().exec(String.format("cmd /c cat %s", pathToServer));
            }

            process.waitFor(2, TimeUnit.SECONDS);

            System.out.println(process.isAlive());
            if (!process.isAlive()) {
                if (process.getErrorStream().available() > 0) {
                    Scanner s = new Scanner(process.getErrorStream()).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";
                    if (!result.isEmpty()) {
                        if (result.contains("already in use")) {
                            throw new ConnectException("The server is already started!");
                        }
                        else {
                            throw new ConnectException("Couldn't start the server!");
                        }
                    }
                }
                throw new ConnectException("Server has stopped unexpectedly!");
            }


//            s = new Scanner(process.getInputStream()).useDelimiter("\\A");
//            result = s.hasNext() ? s.next() : "";
//            System.out.println(result);

            Notifier.notifyInformation("The server has been started!");
        } catch (ConnectException e) {
            Notifier.notifyError(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Notifier.notifyError("I/O error when starting the server.");
            e.printStackTrace();
        } catch (SecurityException e) {
            Notifier.notifyError("You don't have permission to start the server.");
            e.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
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

    public void checkStatus() {
        if (DrawingServer.process.isAlive()) {
            Notifier.notifyInformation("The server is started with PID " + DrawingServer.process.pid());
        } else {
            try {
                Notifier.notifyInformation("The server is stopped with exit code " + DrawingServer.process.exitValue());
            } catch (Exception ignored) {
            }
        }
    }

    public void stopServer() {
        if (process.isAlive()) {
            process.children().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
            //Runtime.getRuntime().exec("cmd /c taskkill /PID " + process.pid() + " /F");
            Notifier.notifyInformation("The server has been stopped!");
        }
    }
}
