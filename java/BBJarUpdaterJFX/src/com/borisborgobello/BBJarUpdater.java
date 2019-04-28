/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

/**
 *
 * @author borisborgobello
 */
public class BBJarUpdater extends Application {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static final String CONF_FILE = "./updater.conf";
    public static final boolean APP_IS_PROD_VERSION = true;
    public static final AppLanguage APP_LANGUAGE = AppLanguage.en;
    
    public static enum AppLanguage {
        en, vi
    }
    
    FXMLProgressDialogController pd;
    BBConf conf;
    BBMemory memory;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        
        System.setErr(new ISDualPrintStream(new File("./err.log").getAbsolutePath(), System.err));
        System.setOut(new ISDualPrintStream(new File("./out.log").getAbsolutePath(), System.out));
        
        pd = (FXMLProgressDialogController) 
                FXMLProgressDialogController.showProgressD("",stage);
        updateProgress("Please type key \"R\" to force reinstall", 0);
        
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                updater.start();
            }
        };
        Timer t = new Timer("Delayer");
        t.schedule(tt, 4000);
        
        pd.stage.get().getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.R) {
                    pd.stage.get().getScene().setOnKeyPressed(null);
                    updateProgress("Full reinstall requested", 0);
                    fullReinstall = true;
                    if (tt.cancel()) updater.start();
                }
            }
        });
    }
    
    boolean fullReinstall = false;
    
    public static final String S_ISTITCH_USR_PARAM = "usr";
    public static final String S_ISTITCH_PWD_PARAM = "pwd";
    
    Thread updater = new Thread() {
        @Override
        public void run() {
            try {
                pd.stage.get().getScene().setOnKeyPressed(null);
                
                memory = BBMemory.getMemory();
                if (memory != null) {
                    log("Last memory = " + memory);
                }
                if (fullReinstall && memory != null) {
                    memory.delete();
                    memory = null;
                }
                try {
                    conf = getConf();
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.exit();
                    return;
                }
                conf.check();
                log("Loaded conf file : " + conf);
                
                updateProgress("Checking ISTITCH server updates...", .1);
                
                HttpClient httpclient = new DefaultHttpClient();
                URIBuilder builder = new URIBuilder(conf.urlCheckLastBin)
                        .setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1.toString());
                
                if (!TextUtils.isEmpty(conf.basicAuth)) {
                    String usrToPwd[] = new String(Hex.decodeHex(conf.basicAuth.toCharArray())).split(":");
                    builder.setParameter(S_ISTITCH_USR_PARAM, usrToPwd[0]).setParameter(S_ISTITCH_PWD_PARAM, usrToPwd[1]);
                }
                HttpPost request = new HttpPost(builder.build());

                //log("executing request " + request.getRequestLine());
                HttpResponse response = httpclient.execute(request);
                HttpEntity resEntity = response.getEntity();
                log(response.getStatusLine().toString());
                String resS = null;
                if (resEntity != null) {
                    resS = EntityUtils.toString(resEntity);
                    resS = resS.replaceAll("\n", "");
                    resEntity.consumeContent();
                }
                httpclient.getConnectionManager().shutdown();
                log("Done");

                
                if (ISTools.isEmpty(resS)) {
                    throw new Exception("Server returned empty string for last bin");
                }

                if (!resS.startsWith("https") && ISTools.isEmpty(conf.urlBinFolder)) {
                    throw new Exception("If server doesn't send back the full URL with https, "
                            + "the field url_bin_folder must be specified!");
                }
                
                if (memory != null && resS.equals(memory.previousServerFile) 
                        && new File(memory.previousLocalBin).exists()) { // disabled for mac
                    finish();
                    return;
                }
                
                updateProgress("New update available ! Downloading files...", .3);
                
                ISTools.deleteDirectoryRecursively(new File("./tmp"));

                String downloadUrl = null;
                if (resS.startsWith("https")) {
                    downloadUrl = resS;
                } else {
                    downloadUrl = String.format("%s%s%s", 
                            conf.urlBinFolder, 
                            conf.urlBinFolder.endsWith("/") ? "" : "/",
                            resS);
                }

                httpclient = new DefaultHttpClient();
                builder = new URIBuilder(downloadUrl)
                        .setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1.toString());
                HttpGet httpget = new HttpGet(builder.build());
                if (!TextUtils.isEmpty(conf.basicAuth)) {
                    String auth = Base64.getEncoder().encodeToString(Hex.decodeHex(conf.basicAuth.toCharArray()));
                    httpget.addHeader("Authorization", "Basic " + auth);
                }
                //System.out.println("executing request " + httpget.getRequestLine());
                response = httpclient.execute(httpget);
                resEntity = response.getEntity();
                System.out.println(response.getStatusLine());

                String tmpDirFixed = fixedPath("./tmp");
                
                File tmpFile = new File(tmpDirFixed, new File(downloadUrl).getName());
                tmpFile.getParentFile().mkdirs();
                Files.copy(resEntity.getContent(), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                resEntity.consumeContent();
                httpclient.getConnectionManager().shutdown();
                System.out.println("Done");
            
                
                // Check file move/replace or unzip and move/replace all files
                updateProgress("Unzipping files...", .6);
                String destDir = new File(tmpDirFixed,"zipoutput").getAbsolutePath();
                new File(destDir).mkdir();
                if (!BBZipper.unzip(tmpFile.getPath(), destDir+"/")) throw new Exception("Unzipping failed");
                
                updateProgress("Replacing files...", .8);
                // Move and replace
                String srcDir = destDir;
                destDir = fixedPath(conf.exportFolder);
                ArrayList<File> srcFiles = ISTools.getAllFiles(new ArrayList<>(), new File(srcDir));
                ArrayList<String> relatPaths = new ArrayList<>(srcFiles.size());
                for (File f : srcFiles) {
                    String path = new File(srcDir).toPath().relativize(f.toPath()).toString();
                    //String path = f.getPath().replaceAll(srcDir, "./");
                    relatPaths.add(path);
                }
                
                
                for (String rp : relatPaths) {
                    File srcF = Paths.get(srcDir, rp).toFile();
                    File dstF = Paths.get(destDir, rp).toFile();
                    
                    if (dstF.exists()) {
                        log("Deleted file : " + dstF.getAbsolutePath());
                    }
                    else {
                        dstF.getParentFile().mkdirs();
                    }
                    Files.copy(srcF.toPath(), dstF.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
                updateProgress("Finishing setup...", 1);
                
                String binPath = null;
                // Update mac icon
                if (conf.isMacUpdater()) {
                    //replace icons
                    File updaterIcns = new File("./Resources/icon.icns");
                    for (String rp : relatPaths) {
                        if (rp.contains(".icns")) {
                            File srcF = Paths.get(destDir, rp).toFile();
                            Files.copy(srcF.toPath(), updaterIcns.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            break;
                        }
                    }
                    // Find binary
                    for (String rp : relatPaths) {
                        if (rp.contains(".app")) {
                            String[] segments = rp.split("/");
                            for (String segment : segments) {
                                if (segment.contains(".app")) {
                                    binPath = fixedPath(Paths.get(".", conf.binPath, segment).toString());
                                    log("Binary path = " + binPath);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    
                    // chmod the file
                    File javaExecFile = new File(binPath, "Contents/MacOS/JavaAppLauncher");
                    Files.setPosixFilePermissions(javaExecFile.toPath(), 
                            PosixFilePermissions.fromString("rwxrwxrwx"));
                } else {
                    binPath = new File(conf.binPath).getAbsolutePath();
                }
                
                if (!new File(binPath).exists()) throw new Exception("Binpath doesn't exist " + binPath);
                
                memory = new BBMemory();
                memory.previousServerFile = resS;
                memory.previousLocalBin = binPath;
                memory.save();
                ISTools.deleteDirectoryRecursively(new File(tmpDirFixed));
                finish();
            } catch (Exception ex) {
                error(ex);
            }
        }
        
        String fixedPath(String path) {
            return fixedPath(path, conf.isMacUpdater());
        }
        
        String fixedPath(String path, boolean mac) {
            if (mac) return new File("./../..", path).getAbsolutePath();
            else return path;
        }
        
        BBConf getConf() throws Exception {
            try {
                return ISAllParsers.getPOJOFromJsonFile(new File(new File(CONF_FILE).getAbsolutePath()), BBConf.class, false);
            } catch (Exception e) {}
            try {
                return ISAllParsers.getPOJOFromJsonFile(new File(new File(fixedPath(CONF_FILE, true)).getAbsolutePath()), BBConf.class, false);
            } catch (Exception e) {}
            throw new Exception("Impossible to find conf file, "
                    + "conf should be name updater.conf and inside same directory of app or jar");
        }
    };
    
    void finish() {
            Platform.runLater(() -> {
                proceedToAppLaunch();
            });
        }
        
        void error(final String s) {
            error(new Exception (s));
        }
        void error(final Exception e) {
            Platform.runLater(() -> {
                criticalError(e);
            });
        }
        
        public void criticalError(Exception e) {
            Logger.getLogger(ISSuperController.class.getName()).log(Level.SEVERE, null, e);
            alertError("Critical error : " + e.getMessage());
            proceedToAppLaunch();
        }
        
        void updateProgress(String title, double ratio) {
            log(title);
            pd.updateProgress(ratio);
            pd.updateTitle(title);
        }
        
        void proceedToAppLaunch() {
            
            try {
                File bin = new File(memory.previousLocalBin);
                if (!bin.exists()) {
                    alertError(String.format("Binary jar %s not found\nFix the configuration file"
                            + " to reflect exported data", bin.getAbsolutePath()));
                    Platform.exit();
                    return;
                }
                
                ArrayList<String> finalArgs = new ArrayList<>();
                String[] processNArgsRaw;
                if (conf.isMacUpdater()) {
                    processNArgsRaw = new String[]{ "open", "-a", bin.getAbsolutePath()};
                    for (String s : processNArgsRaw) { 
                        finalArgs.add(s);
                    }
                } else {
                    processNArgsRaw = conf.execCommand.split(" ");
                    for (String s : processNArgsRaw) { 
                        finalArgs.add(s.replaceAll("bin_path", new File(conf.binPath).getCanonicalPath()));
                    }
                }
                
                ProcessBuilder pb = new ProcessBuilder(finalArgs);
                pb.directory(new File(conf.binPath).getParentFile());
                
                log(finalArgs.toString());
                
                //Process ps=Runtime.getRuntime().exec();
                //ps.waitFor();
                Process ps = pb.start();
                /*ps.waitFor();
                
                InputStream is=ps.getInputStream();
                byte b[]=new byte[is.available()];
                is.read(b,0,b.length);
                log("Process return : " + ps.exitValue());
                log("Process out : " + new String(b));
                
                is = ps.getErrorStream();
                b=new byte[is.available()];
                is.read(b,0,b.length);
                log("Process err : " + new String(b));*/
            } catch (Exception e) {
                alertError(String.format("Critical error : %s\n"
                        + "Restart app and press \"R\". If error persists contact Boris.", e.getMessage()));
            }
            Platform.exit();
        }
        public void alertError(String s) {
            log(s);
            new Alert(Alert.AlertType.ERROR, s).showAndWait();
        }
    
    public static final void log(String s) { System.out.println(s); }
}
