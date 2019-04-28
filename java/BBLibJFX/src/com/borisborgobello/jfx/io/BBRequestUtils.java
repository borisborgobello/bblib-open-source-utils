/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.io;

import com.borisborgobello.jfx.utils.Callb2;
import com.borisborgobello.jfx.utils.Callb3;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import javafx.application.Platform;
import javafx.util.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author borisborgobello
 */
public class BBRequestUtils {
    
    public static final boolean DEBUGON = false;
    
    private static HttpClient getHttpClient() {     
        return new DefaultHttpClient();
    }
    
    public static enum METHOD { POST, GET, PUT, DELETE }
    
    public static final class Prm extends Pair<String,String>{
        public Prm(String key, String value) { super(key, value); }
    }
    
    public static final class BBMultipartEntityBuilder {
        final WeakReference<BBRequestBuilder> req;
        final MultipartEntityBuilder meb;

        public BBMultipartEntityBuilder(BBRequestBuilder req) {
            this.req = new WeakReference<>(req);
            this.meb = MultipartEntityBuilder.create();
        }
        
        public BBMultipartEntityBuilder setMode(HttpMultipartMode mode) {
            meb.setMode(mode); return this;
        }
        public BBMultipartEntityBuilder setLaxMode() {
            meb.setLaxMode(); return this;
        }
        public BBMultipartEntityBuilder setStrictMode() {
            meb.setStrictMode(); return this;
        }
        public BBMultipartEntityBuilder setBoundary(String boundary) {
            meb.setBoundary(boundary); return this;
        }
        public BBMultipartEntityBuilder setMimeSubtype(String subType) {
            meb.setMimeSubtype(subType); return this;
        }
        public BBMultipartEntityBuilder setContentType(ContentType contentType) {
            meb.setContentType(contentType); return this;
        }
        public BBMultipartEntityBuilder setCharset(Charset charset) {
            meb.setCharset(charset); return this;
        }
        public BBMultipartEntityBuilder addPart(FormBodyPart bodyPart) {
            meb.addPart(bodyPart); return this;
        }
        public BBMultipartEntityBuilder addPart(String name, ContentBody contentBody) {
            meb.addPart(name, contentBody); return this;
        }
        public BBMultipartEntityBuilder addTextBody(String name, String text, ContentType contentType) {
            meb.addTextBody(name, text, contentType); return this;
        }
        public BBMultipartEntityBuilder addTextBody(String name, String text) {
            meb.addTextBody(name, text); return this;
        }
        public BBMultipartEntityBuilder addBinaryBody(String name, byte[] b, ContentType contentType, String filename) {
            meb.addBinaryBody(name,b,contentType,filename); return this;
        }
        public BBMultipartEntityBuilder addBinaryBody(String name, byte[] b) {
            meb.addBinaryBody(name, b); return this;
        }
        public BBMultipartEntityBuilder addBinaryBody(String name, File file, ContentType contentType, String filename) {
            meb.addBinaryBody(name, file, contentType, filename); return this;
        }
        public BBMultipartEntityBuilder addBinaryBody(String name, File file) {
            meb.addBinaryBody(name, file); return this;
        }
        public BBMultipartEntityBuilder addBinaryBody(String name, InputStream stream, ContentType contentType, String filename) {
            meb.addBinaryBody(name, stream, contentType, filename); return this;
        }
        public BBMultipartEntityBuilder addBinaryBody(String name, InputStream stream) {
            meb.addBinaryBody(name, stream); return this;
        }
        public BBRequestBuilder build() { 
            req.get().entity = meb.build(); return req.get();
        }
    }
    /*
    private static abstract class JSONResponse<T> implements Callb3<Integer, T, Exception> {
        public JSONResponse() {}
    }*/
    
    
    protected static void executeRequest(boolean async, HttpRequestBase request, 
            Callb2<Double, Boolean> progressUp, Callb2<Double, Boolean> progressDown,
            Callb3<Integer, byte[], Exception> cbCompleted
    ) {
        if (async)
            new Thread() {
                @Override
                public void run() {
                    executeRequest(request, progressUp, progressDown, cbCompleted);
                }
            }.start();
        else executeRequest(request, progressUp, progressDown, cbCompleted);
    }

    protected static void executeRequest(HttpRequestBase request, 
            Callb2<Double, Boolean> progressUp, Callb2<Double, Boolean> progressDown,
            Callb3<Integer, byte[], Exception> cbCompleted
    ) {
        int code = -1;
        String status = null;
        byte[] data = null;
        try {
            boolean notifUp = false;
            if (request instanceof HttpPost && progressUp != null) {
                HttpPost post = (HttpPost) request;
                if (post.getEntity() != null) {
                    notifUp = true;
                    Platform.runLater(() -> { progressUp.run(0.0, false); });
                    post.setEntity(new BBProgressHttpEntityWrapper(post.getEntity(), (float progress) -> {
                        // already in %
                        Platform.runLater(() -> { progressUp.run(progress/100.0, false); });
                    }));
                }
            }

            HttpClient httpclient = getHttpClient();
            if (DEBUGON) System.out.println("Executing request " + request.getRequestLine());

            HttpResponse response = httpclient.execute(request);
            if (notifUp) { Platform.runLater(() -> { progressUp.run(1.0, true); }); }
            // Request executed

            code = response.getStatusLine().getStatusCode();
            status = response.getStatusLine().toString();
            if (DEBUGON) { System.out.println(status); }

            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                if (progressDown != null) {
                    Platform.runLater(() -> { progressDown.run(0.0, false); });
                    resEntity = new BBProgressHttpEntityWrapper(resEntity, (float progress) -> {
                        // already in %
                        Platform.runLater(() -> { progressDown.run(progress/100.0, false); });
                    });
                }
                data = EntityUtils.toByteArray(resEntity);
                resEntity.consumeContent();
                if (progressDown != null) Platform.runLater(() -> { progressDown.run(1.0, true); });
            }
            httpclient.getConnectionManager().shutdown();
            //System.out.println("Done");

            try { cbCompleted.run(code, data, null); } catch (Exception e) { e.printStackTrace(); }
        } catch (Exception e) {
            try { cbCompleted.run(code, data, e); } catch (Exception e2) { e2.printStackTrace(); }
        }
    }
}
