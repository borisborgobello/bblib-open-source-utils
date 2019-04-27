/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.io;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author borisborgobello
 * 
 * Customized from https://stackoverflow.com/questions/1339437/inputstream-or-reader-wrapper-for-progress-reporting
 */
public class BBProgressInputStream extends FilterInputStream {

    private final PropertyChangeSupport propertyChangeSupport;
    private final long maxNumBytes;
    final private long bytesBetweenUpdates;

    private volatile long totalNumBytesRead;
    private long currentBytePeriod = 0;

    public BBProgressInputStream(InputStream in, long maxNumBytes, long bytesBetweenUpdates) {
        super(in);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.maxNumBytes = maxNumBytes;
        this.bytesBetweenUpdates = bytesBetweenUpdates;
    }

    public long getMaxNumBytes() {
        return maxNumBytes;
    }

    public long getTotalNumBytesRead() {
        return totalNumBytesRead;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        updateProgress(1);
        return b;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return (int) updateProgress(super.read(b));
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return (int) updateProgress(super.read(b, off, len));
    }

    @Override
    public long skip(long n) throws IOException {
        return updateProgress(super.skip(n));
    }

    @Override
    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    private long updateProgress(long numBytesRead) {
        if (numBytesRead > 0) {
            currentBytePeriod += numBytesRead;
            if (currentBytePeriod < bytesBetweenUpdates) {
                return numBytesRead;
            } else {
                currentBytePeriod = 0;
            }

            long oldTotalNumBytesRead = this.totalNumBytesRead;
            this.totalNumBytesRead += numBytesRead;
            propertyChangeSupport.firePropertyChange("totalNumBytesRead", oldTotalNumBytesRead, this.totalNumBytesRead);
        }

        return numBytesRead;
    }
}
