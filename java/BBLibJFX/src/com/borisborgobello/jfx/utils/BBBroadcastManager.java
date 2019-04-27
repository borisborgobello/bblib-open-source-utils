/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author borisborgobello
 */
public interface BBBroadcastManager {

    public static final Integer B_SOMETHING_CHANGED = 0;
    public static final Integer B_CHANGED_PROD_ORDER_LIST = 1;
    public static final Integer B_STOCK_UPDATES = 1;

    public static interface ISBroadcastListener {

        public void onChangeBroadcastReceived(int message);
    }

    static final HashMap<Integer, ArrayList<ISBroadcastListener>> listeners = new HashMap<>();

    public static void addBroadcastListener(ISBroadcastListener b, int message) {
        ArrayList<ISBroadcastListener> lists = BBBroadcastManager.listeners.get(message);
        if (lists == null) {
            lists = new ArrayList<>();
            BBBroadcastManager.listeners.put(message, lists);
        }
        lists.add(b);
    }

    public static void broadcastChange(ISBroadcastListener origin, int message) {
        ArrayList<ISBroadcastListener> lists = BBBroadcastManager.listeners.get(message);
        if (lists == null) {
            return;
        }
        for (ISBroadcastListener b : new ArrayList<>(lists)) {
            if (b == origin) {
                continue;
            }
            b.onChangeBroadcastReceived(message);
        }
    }
}
