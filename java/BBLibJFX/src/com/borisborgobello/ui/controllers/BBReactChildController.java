
package com.borisborgobello.ui.controllers;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ResourceBundle;
import com.borisborgobello.utils.BBBroadcastManager;

/**
 *
 * @author borisborgobello
 */
public abstract class BBReactChildController extends BBSuperController implements BBBroadcastManager.ISBroadcastListener {
    public WeakReference<BBSuperController> mainC;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BBBroadcastManager.addBroadcastListener(this, BBBroadcastManager.B_SOMETHING_CHANGED);
    }
    
    protected void broadcastChange() {
        BBBroadcastManager.broadcastChange(null, BBBroadcastManager.B_SOMETHING_CHANGED);
    }
}
