/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.io;

import com.borisborgobello.jfx.utils.Callb2;
import com.borisborgobello.jfx.utils.Callb3;
import com.borisborgobello.jfx.utils.BBAllParsers;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;


/**
 *
 * @author borisborgobello
 */
public class BBRequestBuilder {
    public final String url;
    public boolean async = true;
    public ArrayList<BBRequestUtils.Prm> headers = new ArrayList<>();
    public ArrayList<BBRequestUtils.Prm> prm = new ArrayList<>();
    public HttpEntity entity =null;

    Callb2<Double, Boolean> progressUp = null;
    Callb2<Double, Boolean> progressDown = null;

    Callb2<Integer, Exception> cb1 = null;
    Callb3<Integer, byte[], Exception> cb2 = null;

    public BBRequestBuilder setCB1(Callb2<Integer, Exception> cb) { this.cb1= cb; return this; }
    public BBRequestBuilder setCB2(Callb3<Integer, byte[], Exception> cb) { this.cb2 = cb; return this; }

    protected BBRequestBuilder(String url) { this.url = url; }
    public static BBRequestBuilder url(String url) { return new BBRequestBuilder(url); }

    public BBRequestBuilder addHeader(String key, Object value) { headers.add(new BBRequestUtils.Prm(key, value.toString())); return this; }
    public BBRequestBuilder addParam(String key, Object value) { prm.add(new BBRequestUtils.Prm(key, value.toString())); return this; }
    public BBRequestBuilder sync() { async = false; return this; }

    public BBRequestBuilder setBodyJson(Object jacksonObject) { 
        try {
            EntityBuilder eb = EntityBuilder.create().setBinary(BBAllParsers.getValuePOJOasBytes(jacksonObject, false));
            eb.setContentType(ContentType.APPLICATION_JSON);
            entity = eb.build();
        } catch (Exception e) { throw new RuntimeException(e); }
        return this;
    }
    public BBRequestBuilder setBodyBinary(byte[] bin) {
        entity = EntityBuilder.create().setBinary(bin).build(); return this;
    }
    public BBRequestBuilder setBodyText(String txt) {
        EntityBuilder eb = EntityBuilder.create().setText(txt);
        entity = eb.build();
        return this;
    }
    public BBRequestUtils.BBMultipartEntityBuilder setBodyMultipart() { return new BBRequestUtils.BBMultipartEntityBuilder(this); }

    public BBRequestBuilder setBodyMultipleJson(Object ... jsonObjects) {
        BBRequestUtils.BBMultipartEntityBuilder bbme = setBodyMultipart();
        try {    
            int data = 0;
            for (Object o : jsonObjects) {
                data++;
                setBodyMultipart().addBinaryBody(data == 1 ? "data" : ("data"+data), BBAllParsers.getValuePOJOasBytes(o, false));
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return bbme.build();
    }

    public BBRequestBuilder setProgressUpListener(Callb2<Double, Boolean> pup) {
        progressUp = pup; return this;
    }
    public BBRequestBuilder setProgressDownListener(Callb2<Double, Boolean> pup) {
        progressDown = pup; return this;
    }

    public void post() {
        if (entity != null && !prm.isEmpty()) {
            if (cb1 != null) cb1.run(-1, new RuntimeException("Params and Body entity cannot be defined at the same time for POST"));
            else {
                cb2.run(-1, null, new RuntimeException("Params and Body entity cannot be defined at the same time for POST"));
            }
            return;
        }

        try {
            URIBuilder b = new URIBuilder(url);
            for (BBRequestUtils.Prm p: prm) {
                b.addParameter(p.getKey(), p.getValue());
            }

            HttpPost request = new HttpPost(b.build());
            if (entity != null) request.setEntity(entity);

            BBRequestUtils.executeRequest(async, request, progressUp, progressDown, new Callb3<Integer, byte[], Exception>() {
                @Override
                public void run(Integer t, byte[] u, Exception v) {
                    if (cb1 != null) try { cb1.run(t, v); } catch (Exception e2) { e2.printStackTrace(); }
                    else {
                        try { cb2.run(t, u, v); } catch (Exception e2) { e2.printStackTrace(); }
                    }
                }
            });
        } catch (Exception e) {
            if (cb1 != null) try { cb1.run(-1, e); } catch (Exception e2) { e2.printStackTrace(); }
            else {
                try { cb2.run(-1, null, e); } catch (Exception e2) { e2.printStackTrace(); }
            }
        }
    }

    public void get() {
        try {
            URIBuilder b = new URIBuilder(url);
            for (BBRequestUtils.Prm p: prm) {
                b.addParameter(p.getKey(), p.getValue());
            }

            HttpGet request = new HttpGet(b.build());
            BBRequestUtils.executeRequest(async, request, progressUp, progressDown, new Callb3<Integer, byte[], Exception>() {
                @Override
                public void run(Integer t, byte[] u, Exception v) {
                    if (cb1 != null) try { cb1.run(t, v); } catch (Exception e2) { e2.printStackTrace(); }
                    else {
                        try { cb2.run(t, u, v); } catch (Exception e2) { e2.printStackTrace(); }
                    }
                }
            });
        } catch (Exception e) {
            if (cb1 != null) try { cb1.run(-1, e); } catch (Exception e2) { e2.printStackTrace(); }
            else {
                try { cb2.run(-1, null, e); } catch (Exception e2) { e2.printStackTrace(); }
            }
        }
    }
}
