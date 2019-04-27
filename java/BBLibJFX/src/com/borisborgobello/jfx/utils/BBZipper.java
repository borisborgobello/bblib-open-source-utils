package com.borisborgobello.jfx.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/*
* Zipper utils, includes transfer delegate for progress display
 */
public class BBZipper {

    public interface TransferDelegate {

        void onTransferUpdate(long transmitedBytes, long totalBytes, long deltaBytes, Object customObject) throws InterruptedException;

        void onTransferFailed(InputStream is, OutputStream os, Exception e);

        void onTransferFinished(InputStream is, OutputStream os);
    }

    static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);

        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }

    static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
            throws Exception {
        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            try (FileInputStream in = new FileInputStream(srcFile)) {
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        }
    }

    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
            throws Exception {
        File folder = new File(srcFolder);

        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }

    public static boolean unzip(String zipname, String destination, TransferDelegate del) {
        return unzip(zipname, destination, del, -1, -1);
    }

    public static boolean unzip(String zipname, String destination, TransferDelegate del, long dataLength) {
        return unzip(zipname, destination, del, dataLength, 500000);
    }

    public static boolean unzip(String zipname, String destination, TransferDelegate del,
            long dataLength, long updateEveryXOctets) {
        InputStream is;
        ZipInputStream zis;
        String path;
        new File(destination).mkdirs();
        if (!destination.endsWith("/")) {
            destination = destination + "/";
        }
        long currentTotalCopiedBytes = 0;
        long currentCopiedBytes = 0;

        if (BBTools.isEmpty(destination)) {
            path = new File(zipname).getParent();
        } else {
            path = destination;
        }

        try {
            String filename;
            is = new FileInputStream(zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int nbEntries = 0;
            if (del != null && dataLength <= 0) {
                while ((ze = zis.getNextEntry()) != null) {
                    nbEntries++;
                    zis.closeEntry();
                }
                zis.close();
                is = new FileInputStream(zipname);
                zis = new ZipInputStream(new BufferedInputStream(is));
            }

            int count;
            int entryCount = 0;
            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                BBLog.s("BBZipper", "unzipping file " + filename);

                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                } else if (filename.contains("/")) {
                    File fmd = new File(path + filename.substring(0, filename.lastIndexOf("/")));
                    fmd.mkdirs();
                }

                try (FileOutputStream fout = new FileOutputStream(path + filename)) {
                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                        if (dataLength > 0) {
                            currentCopiedBytes += count;
                            currentTotalCopiedBytes += count;
                            if (currentCopiedBytes > updateEveryXOctets) {
                                currentCopiedBytes = 0;
                                try {
                                    del.onTransferUpdate(currentTotalCopiedBytes, dataLength, -1, null);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                }
                zis.closeEntry();
                entryCount++;
                if (dataLength <= 0) {
                    try {
                        del.onTransferUpdate(entryCount, nbEntries, 1, null);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            zis.close();
            del.onTransferFinished(null, null);
        } catch (IOException e) {
            e.printStackTrace();
            del.onTransferFailed(null, null, e);
            return false;
        }

        return true;
    }

    public static interface ZipSource {

        InputStream openInput();
    }

    public static boolean unzip(ZipSource source, @NonNull String destination, TransferDelegate del,
            long dataLength, long updateEveryXOctets) {
        ZipInputStream zis;

        new File(destination).mkdirs();
        if (!destination.endsWith("/")) {
            destination = destination + "/";
        }
        long currentTotalCopiedBytes = 0;
        long currentCopiedBytes = 0;
        String path = destination;

        try {
            String filename;
            zis = new ZipInputStream(new BufferedInputStream(source.openInput()));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int nbEntries = 0;
            if (del != null && dataLength <= 0) {
                while ((ze = zis.getNextEntry()) != null) {
                    nbEntries++;
                    zis.closeEntry();
                }
                zis.close();
                zis = new ZipInputStream(new BufferedInputStream(source.openInput()));
            }

            int count;
            int entryCount = 0;
            while ((ze = zis.getNextEntry()) != null) {
                // zapis do souboru
                filename = ze.getName();
                BBLog.s("BBZipper", "unzipping file " + filename);

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                } else if (filename.contains("/")) {
                    File fmd = new File(path + filename.substring(0, filename.lastIndexOf("/")));
                    fmd.mkdirs();
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                    if (dataLength > 0) {
                        currentCopiedBytes += count;
                        currentTotalCopiedBytes += count;
                        if (currentCopiedBytes > updateEveryXOctets) {
                            currentCopiedBytes = 0;
                            try {
                                del.onTransferUpdate(currentTotalCopiedBytes, dataLength, -1, null);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }

                fout.close();
                zis.closeEntry();
                entryCount++;
                if (dataLength <= 0) {
                    try {
                        del.onTransferUpdate(entryCount, nbEntries, 1, null);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            zis.close();
            del.onTransferFinished(null, null);
        } catch (IOException e) {
            e.printStackTrace();
            del.onTransferFailed(null, null, e);
            return false;
        }

        return true;
    }

    public final File zipFile;
    private ZipOutputStream out;

    public BBZipper(File f) {
        if (!f.getPath().endsWith(".zip")) {
            zipFile = new File(f.getPath() + ".zip");
        } else {
            zipFile = f;
        }
    }

    public BBZipper start() throws FileNotFoundException {
        out = new ZipOutputStream(new FileOutputStream(zipFile));
        return this;
    }

    public BBZipper addEntry(String name, byte[] data) throws IOException {
        ZipEntry e = new ZipEntry(name);
        out.putNextEntry(e);

        out.write(data, 0, data.length);
        out.closeEntry();
        return this;
    }

    public BBZipper finish() throws IOException {
        out.close();
        return this;
    }
}
