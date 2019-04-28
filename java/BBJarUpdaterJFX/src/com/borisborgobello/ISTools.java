/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

/**
 *
 * @author borisborgobello
 */
public class ISTools {
    
    public static final String cleanString(String s) { return s.replaceAll("[^\\w\\s]",""); } // remove everything but number, alphabet, and .
    /*public static final String cleanFilename(String s) { 
        return s.replaceAll("[^\\w\\s]",""); 
    } // remove everything but number, alphabet, and .*/
    
    public static final boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
    
    public static final String extractFilename(String fullpath, boolean withExtension) {
		if (!withExtension) {
			fullpath = fileWithoutExtension(fullpath);
		}
		String[] segments = fullpath.split(File.separator);
		return segments[segments.length-1];
	}
    
    // return directory including ending slash
	public static final String extractDirectory(String fullpath) {
		if (!fullpath.contains(File.separator)) return "";
		else {
			int lastIdx = fullpath.lastIndexOf(File.separator);
			return fullpath.substring(0, lastIdx+1);
		}
	}
        public static final boolean hasExtension(String file) {
		return file.lastIndexOf(".") > file.lastIndexOf("/");
	}
	public static final String fileWithoutExtension(String file) {
		if (hasExtension(file)) {
			int idxOfDot = file.lastIndexOf(".");
			return file.substring(0, idxOfDot);
		}
		return file;
	}
        
        public static final Comparator<String> INTEGER_COMPARATOR = new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    Integer i1, i2;
                    try { i1 = Integer.parseInt(s1); } catch (Exception e) { i1 = null;}
                    try { i2 = Integer.parseInt(s2); } catch (Exception e) { i2 = null;}
                    
                    if (i1 == null && i2 == null) {
                        if (s1 == null && s2 == null) return 0;
                        if (s1 == null) return 1;
                        return s1.compareToIgnoreCase(s2);
                    }
                    if (i1 == null) return 1;
                    if (i2 == null) return -1;
                    
                    return i1-i2;
                }
            };
        
        public static final Comparator<String> LONG_COMPARATOR = new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    Long i1, i2;
                    try { i1 = Long.parseLong(s1); } catch (Exception e) { i1 = null;}
                    try { i2 = Long.parseLong(s2); } catch (Exception e) { i2 = null;}
                    
                    if (i1 == null && i2 == null) {
                        if (s1 == null && s2 == null) return 0;
                        if (s1 == null) return 1;
                        return s1.compareToIgnoreCase(s2);
                    }
                    if (i1 == null) return 1;
                    if (i2 == null) return -1;
                    if (i1 > i2) return 1;
                    else return -1;
                }
            };
        
        public static final Comparator<String> DOUBLE_COMPARATOR = new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    Double i1, i2;
                    try { i1 = Double.parseDouble(s1); } catch (Exception e) { i1 = null;}
                    try { i2 = Double.parseDouble(s2); } catch (Exception e) { i2 = null;}
                    
                    if (i1 == null && i2 == null) {
                        if (s1 == null && s2 == null) return 0;
                        if (s1 == null) return 1;
                        return s1.compareToIgnoreCase(s2);
                    }
                    if (i1 == null) return 1;
                    if (i2 == null) return -1;
                    
                    if (i1.equals(i2)) return 0;
                    else if (i1>i2) return 1;
                    else return -1;
                }
            };
        
            public static final Comparator<String> LOG_DATE_COMPARATOR = new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    if (isEmpty(s1) && isEmpty(s2)) return 0;
                    if (isEmpty(s1)) return 1;
                    if (isEmpty(s2)) return -1;
                    Date d1 = null, d2 = null;
                    try {
                        d1 = ISGlobal.logFormat.parse(s1);
                    } catch (Exception e) {}
                    try {
                        d2 =ISGlobal.logFormat.parse(s2);
                    } catch (Exception e) {}
                    
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    
                    return d1.compareTo(d2);
                }
            };
        
    public static class ProgressInputStream extends FilterInputStream {
        private final PropertyChangeSupport propertyChangeSupport;
        private final long maxNumBytes;
        final private long bytesBetweenUpdates;
        
        private volatile long totalNumBytesRead;
        private long currentBytePeriod = 0;

        public ProgressInputStream(InputStream in, long maxNumBytes, long bytesBetweenUpdates) {
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
            return (int)updateProgress(super.read(b));
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return (int)updateProgress(super.read(b, off, len));
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
                if (currentBytePeriod < bytesBetweenUpdates) return numBytesRead;
                else { currentBytePeriod = 0; }
                
                long oldTotalNumBytesRead = this.totalNumBytesRead;
                this.totalNumBytesRead += numBytesRead;
                propertyChangeSupport.firePropertyChange("totalNumBytesRead", oldTotalNumBytesRead, this.totalNumBytesRead);
            }

            return numBytesRead;
        }
    }
    
    private static final int BUFFER_SIZE = 8192;
    
    public static long copy(InputStream source, OutputStream sink)
        throws IOException
    {
        long nread = 0L;
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }
    
/**
 * HttpEntityWrapper with a progress callback
 *
 * @see <a href="http://stackoverflow.com/a/7319110/268795">http://stackoverflow.com/a/7319110/268795</a>
 */

    public static class ProgressHttpEntityWrapper extends HttpEntityWrapper {

        private final ProgressCallback progressCallback;

        public static interface ProgressCallback {
            public void progress(float progress);
        }

        public ProgressHttpEntityWrapper(final HttpEntity entity, final ProgressCallback progressCallback) {
            super(entity);
            this.progressCallback = progressCallback;
        }

        @Override
        public void writeTo(final OutputStream out) throws IOException {
            this.wrappedEntity.writeTo(out instanceof ProgressFilterOutputStream ? out : new ProgressFilterOutputStream(out, this.progressCallback, getContentLength()));
        }

        static class ProgressFilterOutputStream extends FilterOutputStream {

            private final ProgressCallback progressCallback;
            private long transferred;
            private long totalBytes;

            ProgressFilterOutputStream(final OutputStream out, final ProgressCallback progressCallback, final long totalBytes) {
                super(out);
                this.progressCallback = progressCallback;
                this.transferred = 0;
                this.totalBytes = totalBytes;
            }

            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException {
                //super.write(byte b[], int off, int len) calls write(int b)
                out.write(b, off, len);
                this.transferred += len;
                this.progressCallback.progress(getCurrentProgress());
            }

            @Override
            public void write(final int b) throws IOException {
                out.write(b);
                this.transferred++;
                this.progressCallback.progress(getCurrentProgress());
            }

            private float getCurrentProgress() {
                return ((float) this.transferred / this.totalBytes) * 100;
            }

        }

    }
    
    public static ArrayList<File> getAllFiles(ArrayList<File> list, File current) {
        if (!current.isDirectory()) {
            list.add(current);
            System.out.println("Added file : " + current.getAbsolutePath());
        }
        else {
            for (File f : current.listFiles()) {
                getAllFiles(list, f);
            }
        }
        return list;
    }
    
    public static void deleteDirectoryRecursively(File currentDir) {
		if (!currentDir.exists() || !currentDir.isDirectory()) return;
		File[] files = currentDir.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				deleteDirectoryRecursively(f);
			}
			else {
				f.delete();
			}
		}
		currentDir.delete();
	}
	
	/**  **/
	private void deleteDirectoryRecursively2(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	        	deleteDirectoryRecursively2(child);

	    fileOrDirectory.delete();
	}
}
