/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.io;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 *
 * @author borisborgobello
 * 
 * This class is used to pipe data to both a stream (stream2) and a file (based on fileName)
 * Encapsulating STDOUT inside BBDualPrintStream allows easy logging
 */
public class BBDualPrintStream extends PrintStream{
    final private PrintStream out2;
    
    public BBDualPrintStream(String fileName, PrintStream stream2) throws FileNotFoundException {
        super(fileName);
        out2 = stream2;
    }

    @Override
    public void println(Object x) {
        super.println(x);
    }

    @Override
    public void println(String x) {
        super.println(x);
    }

    @Override
    public void println(char[] x) {
        super.println(x);
        out2.println();
    }

    @Override
    public void println(double x) {
        super.println(x);
        out2.println();
    }

    @Override
    public void println(float x) {
        super.println(x);
        out2.println();
    }

    @Override
    public void println(long x) {
        super.println(x);
        out2.println();
    }

    @Override
    public void println(int x) {
        super.println(x);
        out2.println();
    }

    @Override
    public void println(char x) {
        super.println(x);
        out2.println();
    }

    @Override
    public void println(boolean x) {
        super.println(x);
        out2.println();
    }

    @Override
    public void println() {
        super.println();
        out2.println();
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        out2.write(buf, off, len);
        super.write(buf, off, len);
    }

    @Override
    public void write(int b) {
        out2.write(b);
        super.write(b);
    }
    
    @Override
    public void close() {
        out2.close();
        super.close();
    }

    @Override
    public void flush() {
        out2.flush();
        super.flush();
    }
}
