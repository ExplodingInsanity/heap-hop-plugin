package webviewer;

import org.cef.callback.CefCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.NoSuchElementException;
import java.util.Optional;

class CustomResourceHandler implements CefResourceHandler {

    private ResourceHandlerState state = new ClosedConnection();

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        var urlOption = Optional.of(cefRequest.getURL());
        try {
            String processedUrl = urlOption.get();
//            String pathToResource = processedUrl.replace("http://heap-hop", "webview/");
            String pathToResource = processedUrl;
            URL newUrl = getClass().getClassLoader().getResource(pathToResource);
            if (newUrl != null) {
                state = new OpenedConnection(newUrl.openConnection());
            }
            cefCallback.Continue();
            return true;
        } catch (NoSuchElementException | IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        state.getResponseHeaders(cefResponse, responseLength, redirectUrl);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        return state.readResponse(dataOut, bytesToRead, bytesRead, callback);
    }

    @Override
    public void cancel() {
        state.close();
        state = new ClosedConnection();
    }
}

interface ResourceHandlerState {

    void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl);

    default boolean processRequest(CefRequest request, CefCallback callback) {
        return false;
    }

    default boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
            return false;
    }

    default void close() {}
}

class OpenedConnection implements ResourceHandlerState {
    private final InputStream inputStream;
    URLConnection connection;

    public OpenedConnection(URLConnection connection) throws IOException {
        this.connection = connection;
        inputStream = connection.getInputStream();
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        try {
            String url = connection.getURL().toString();

            if (url.contains("css")) {
                cefResponse.setMimeType("text/css");
            }
            else if (url.contains("js")) {
                cefResponse.setMimeType("text/javascript");
            }
            else if (url.contains("html")) {
                cefResponse.setMimeType("text/html");
            }
            else {
                cefResponse.setMimeType(connection.getContentType());
            }
            responseLength.set(inputStream.available());
            cefResponse.setStatus(200);
        } catch (IOException ex) {
                cefResponse.setError(CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND);
                cefResponse.setStatusText(ex.getLocalizedMessage());
                cefResponse.setStatus(404);
        }
    }

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        return ResourceHandlerState.super.processRequest(request, callback);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        try {
            int availableSize = inputStream.available();
            if (availableSize > 0) {
                int maxBytesToRead = Math.min(availableSize, bytesToRead);
                int realNumberOfReadBytes = inputStream.read(dataOut, 0, maxBytesToRead);
                bytesRead.set(realNumberOfReadBytes);
                return true;
            } else {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClosedConnection implements ResourceHandlerState {

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        cefResponse.setStatus(404);
    }

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        return ResourceHandlerState.super.processRequest(request, callback);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        return false;
    }

    @Override
    public void close() {
        ResourceHandlerState.super.close();
    }
}
