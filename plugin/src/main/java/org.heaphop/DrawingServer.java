package org.heaphop;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import java.io.IOException;

public class DrawingServer {
    private static Process process;
    private final String baseURI;

    public DrawingServer(String baseURI, String pathToServer) {
        this.baseURI = baseURI;
        startServer(pathToServer);
    }

    public void sendPostRequest(String URI, JSONObject json) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(baseURI.concat(URI));
            StringEntity entity = new StringEntity(json.toString());

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "text/html");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                System.out.println("Couldn't send data!");
            }
            else {
                System.out.println(EntityUtils.toString(httpResponse.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startServer(String pathToServer) {
        try {
            process = Runtime.getRuntime().exec(String.format("cmd /c node %s", pathToServer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        process.destroyForcibly();
    }
}
