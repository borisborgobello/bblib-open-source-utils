/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello;

import com.borisborgobello.handler.Handler;
import com.borisborgobello.utils.BBLog;
import javafx.application.Platform;

/**
 *
 * @author borisborgobello
 */
public class BBLibInit {
    public static final synchronized void init() {
        if (!Platform.isFxApplicationThread()) throw new RuntimeException("BBLib init must be done on FX/Main/UI thread");
        Handler.init();
        BBLog.init(true);
    }
}
