/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author borisborgobello
 */
public class BBIOUtils {
    public interface TransferDelegate {

        void onTransferUpdate(long transmitedBytes, long totalBytes, long deltaBytes, Object customObject) throws InterruptedException;

        void onTransferFailed(InputStream is, OutputStream os, Exception e);

        void onTransferFinished(InputStream is, OutputStream os);
    }

    public static void transferData(InputStream is, OutputStream os, long length, TransferDelegate td, Object customObject) {
        int nRead;
        byte[] data = new byte[16284];
        long currentOverallRead = 0;

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                os.write(data, 0, nRead);
                //os.flush();
                currentOverallRead += nRead;
                if (td != null) {
                    td.onTransferUpdate(currentOverallRead, length, nRead, customObject);
                }
            }
            os.flush();
            if (td != null) {
                td.onTransferUpdate(currentOverallRead, length, nRead, customObject);
            }
            if (td != null) {
                td.onTransferFinished(is, os);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (td != null) {
                td.onTransferFailed(is, os, e);
            }
        }
    }

    public static void transferDataExc(InputStream is, OutputStream os, long length, TransferDelegate td, Object customObject) throws Exception {
        int nRead;
        byte[] data = new byte[8192];
        long currentOverallRead = 0;

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                os.write(data, 0, nRead);
                os.flush();
                currentOverallRead += nRead;
                if (td != null) {
                    td.onTransferUpdate(currentOverallRead, length, nRead, customObject);
                }
            }
            os.flush();
            if (td != null) {
                td.onTransferUpdate(currentOverallRead, length, nRead, customObject);
            }
            if (td != null) {
                td.onTransferFinished(is, os);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (td != null) {
                td.onTransferFailed(is, os, e);
            }
            throw e;
        }
    }
}
