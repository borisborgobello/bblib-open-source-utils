/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.io;

import com.borisborgobello.utils.Callb3;
import com.borisborgobello.utils.BBAllParsers;
import javafx.util.Callback;

/**
 *
 * @author borisborgobello
 */
public class BBRequestBuilderJson<T> extends BBRequestBuilder {
    final Class<T> clazz;
    public BBRequestBuilderJson(String url, Class<T> clazz) { super(url); this.clazz = clazz;}

    public Callb3<Integer, T, Exception> cb3 = null;

    public BBRequestBuilder setCB3(Callb3<Integer,T,Exception>cb) {
        cb3 = cb; return this;
    }

    private Callback<byte[], T> transformer = new Callback<byte[], T>() {
        @Override
        public T call(byte[] param) {
            try {
                return BBAllParsers.getPOJOFromBytes(param, clazz, false);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    @Override
    public void post() {
        cb2 = new Callb3<Integer, byte[], Exception>() {
            @Override
            public void run(Integer t, byte[] u, Exception v) {
                try {
                    T t2 = transformer.call(u);
                } catch (Exception e) {
                    try { cb3.run(t, null, e); } catch (Exception e2) { e2.printStackTrace(); }
                }
            }
        };
        super.post();
    }

    @Override
    public void get() {
        cb2 = new Callb3<Integer, byte[], Exception>() {
            @Override
            public void run(Integer t, byte[] u, Exception v) {
                try {
                    T t2 = transformer.call(u);
                } catch (Exception e) {
                    try { cb3.run(t, null, e); } catch (Exception e2) { e2.printStackTrace(); }
                }
            }
        };
        super.get();
    }
}