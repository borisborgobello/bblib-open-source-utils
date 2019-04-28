/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author borisborgobello
 */
public class ISDualPrintStream extends PrintStream{
    private PrintStream out2;
    
    public ISDualPrintStream(String fileName, PrintStream stream2) throws FileNotFoundException {
        super(fileName);
        out2 = stream2;
    }

    @Override
    public PrintStream append(char c) {
        out2.append(c);
        return super.append(c); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        out2.append(csq, start, end);
        return super.append(csq, start, end); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintStream append(CharSequence csq) {
        out2.append(csq);
        return super.append(csq); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        out2.format(l, format, args);
        return super.format(l, format, args); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintStream format(String format, Object... args) {
        out2.format(format, args);
        return super.format(format, args); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        out2.printf(l, format, args);
        return super.printf(l, format, args); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        out2.printf(format, args);
        return super.printf(format, args); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(Object x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(String x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(char[] x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(double x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(float x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(long x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(int x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(char x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println(boolean x) {
        out2.println(x);
        super.println(x); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void println() {
        out2.println();
        super.println(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(Object obj) {
        out2.print(obj);
        super.print(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(String s) {
        out2.print(s);
        super.print(s); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(char[] s) {
        out2.print(s);
        super.print(s); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(double d) {
        out2.print(d);
        super.print(d); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(float f) {
        out2.print(f);
        super.print(f); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(long l) {
        out2.print(l);
        super.print(l); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(int i) {
        out2.print(i);
        super.print(i); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(char c) {
        out2.print(c);
        super.print(c); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(boolean b) {
        out2.print(b);
        super.print(b); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        out2.write(buf, off, len);
        super.write(buf, off, len); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(int b) {
        out2.write(b);
        super.write(b); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void clearError() {
        //out2.clearError();
        super.clearError(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setError() {
        //out2.setError();
        super.setError(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean checkError() {
        //out2.checkError();
        return super.checkError(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        out2.close();
        super.close(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flush() {
        out2.flush();
        super.flush(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(byte[] b) throws IOException {
        out2.write(b);
        super.write(b); //To change body of generated methods, choose Tools | Templates.
    }
}
