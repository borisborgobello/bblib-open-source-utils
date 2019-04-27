/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author borisborgobello
 */
public class BBFileInout {

    public static final String S_TMP_PATH = Paths.get(new File(".").getAbsolutePath(), "temp").toString();

    // Example of call : "tmp", "1", "test", "aaa.png", last seg is the filename, its path is ignored bbb/aaa.png -> aaa.png
    public static final File getTemporaryFile(String... segments) {
        segments[segments.length - 1] = extractFilename(segments[segments.length - 1], true); // ignoring path
        File f = Paths.get(S_TMP_PATH, segments).toFile();
        f.getParentFile().mkdirs();
        return f;
    }

    public static byte[] readAllDataFromInputStream(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteDirectoryRecursively(File currentDir) {
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            return;
        }
        File[] files = currentDir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                deleteDirectoryRecursively(f);
            } else {
                f.delete();
            }
        }
        currentDir.delete();
    }

    private void deleteDirectoryRecursively2(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteDirectoryRecursively2(child);
            }
        }

        fileOrDirectory.delete();
    }
    
    public static final String getExtensionWithoutDot(String pathAndFilename) {
        if (pathAndFilename == null) {
            return "";
        }
        int dotIdx = pathAndFilename.lastIndexOf(".");
        if (dotIdx == -1 || dotIdx == pathAndFilename.length() - 1) {
            return "";
        }
        return pathAndFilename.substring(dotIdx + 1);
    }
    public static final String extractFilename(String fullpath, boolean withExtension) {
        Path p = Paths.get(fullpath);
        String filename = p.getFileName().toString();
        return withExtension ? filename : filename.split("\\.")[0];
    }
    
    public static final boolean hasExtension(String file) {
        return Paths.get(file).getFileName().toString().contains(".");
    }

    public static final String fileWithoutExtension(String file) {
        if (hasExtension(file)) {
            int idxOfDot = file.lastIndexOf(".");
            return file.substring(0, idxOfDot);
        }
        return file;
    }
}
