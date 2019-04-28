package com.borisborgobello;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BBZipper {

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
                    FileInputStream in = new FileInputStream(srcFile);
                    zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                    while ((len = in.read(buf)) > 0) {
                            zip.write(buf, 0, len);
                    }
                    in.close();
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

    public static boolean unzip(String zippath) {
            return unzip(zippath, null);
    }

    public static boolean unzip(String zipname, String destination) {
            InputStream is;
            ZipInputStream zis;
            String path;
            if(ISTools.isEmpty(destination))
                    path = ISTools.extractDirectory(zipname);
            else
                    path = destination;

            try 
            {
                    String filename;
                    is = new FileInputStream(zipname);
                    zis = new ZipInputStream(new BufferedInputStream(is));          
                    ZipEntry ze;
                    byte[] buffer = new byte[1024];
                    int count;

                    while ((ze = zis.getNextEntry()) != null) 
                    {
                            // zapis do souboru
                            filename = ze.getName();
                            System.out.println("BBZipper unzipping file "+ filename);

                            // Need to create directories if not exists, or
                            // it will generate an Exception...
                            if (ze.isDirectory()) {
                                    File fmd = new File(path + filename);
                                    fmd.mkdirs();
                                    continue;
                            }
                            else if(filename.contains("/")) {
                                    File fmd = new File(path + filename.substring(0, filename.lastIndexOf("/")));
                                    fmd.mkdirs();
                            }

                            FileOutputStream fout = new FileOutputStream(path + filename);

                            // cteni zipu a zapis
                            while ((count = zis.read(buffer)) != -1) 
                            {
                                    fout.write(buffer, 0, count);             
                            }

                            fout.close();               
                            zis.closeEntry();
                    }

                    zis.close();
            } 
            catch(IOException e)
            {
                    e.printStackTrace();
                    return false;
            }

            return true;
    }
    
    public final File zipFile;
    private ZipOutputStream out;
    public BBZipper(File f) {
        if (!f.getPath().endsWith(".zip")) zipFile = new File(f.getPath() + ".zip");
        else zipFile = f;
    }
    public BBZipper start() throws FileNotFoundException { 
        out = new ZipOutputStream(new FileOutputStream(zipFile)); return this;
    }
    
    public BBZipper addEntry(String name, byte[] data) throws IOException {
        ZipEntry e = new ZipEntry(name);
        out.putNextEntry(e);

        out.write(data, 0, data.length);
        out.closeEntry();
        return this;
    }
    
    public BBZipper finish() throws IOException { out.close(); return this; }
}