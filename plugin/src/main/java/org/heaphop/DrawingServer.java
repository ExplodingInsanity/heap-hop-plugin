package org.heaphop;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Scanner;

public class DrawingServer {
    public static Process process;
    private final String baseURI;

    public DrawingServer(String baseURI, String pathToServer) {
        this.baseURI = baseURI;
        startServer(pathToServer);
        checkStatus();
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

//                System.out.println(EntityUtils.toString(httpResponse.getEntity()));
//                File file = new File(EntityUtils.toString(httpResponse.getEntity()));
//                Runtime.getRuntime().exec(String.format(
//                        "cmd /c start chrome --disable-web-security --disable-gpu --user-data-dir=%%HOMEPATH%%\\chromeTemp %s",
//                        file.getAbsolutePath()));
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
                //process = Runtime.getRuntime().exec(String.format("cmd /c cat %s", pathToServer));
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
            try {
                process.children().forEach(ProcessHandle::destroyForcibly);
                Runtime.getRuntime().exec("cmd /c taskkill /PID " + process.pid() + " /F");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
