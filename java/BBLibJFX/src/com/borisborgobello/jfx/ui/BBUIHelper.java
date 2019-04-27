/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.ui;

import com.borisborgobello.jfx.img.BBImgUtils;
import com.borisborgobello.jfx.utils.BBDateUtils;
import com.borisborgobello.jfx.utils.BBColor;
import com.borisborgobello.jfx.utils.BBZipper;
import com.borisborgobello.jfx.utils.BBLog;
import com.borisborgobello.jfx.utils.BBTools;
import com.borisborgobello.jfx.utils.Callb;
import com.borisborgobello.jfx.dialogs.FXMLInputDialogController;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.dialogs.FXMLProgressDialogController;
import com.borisborgobello.jfx.utils.BBColorUtils;
import com.borisborgobello.jfx.utils.BBRes;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javax.imageio.ImageIO;
import org.controlsfx.control.Notifications;

/**
 *
 * @author borisborgobello
 */
public class BBUIHelper {

    public static ResourceBundle DEFAULT_RESOURCE_BUNDLE = null;
    public static String DEFAULT_THEME_CSS = null;

    public static synchronized void setDefaultBundleAndTheme(ResourceBundle bundle, String themeCSS) {
        DEFAULT_RESOURCE_BUNDLE = bundle;
        BBRes.DEFAULT_BUNDLE = bundle;
        DEFAULT_THEME_CSS = themeCSS;
    }

    public static void setSceneStyleSheets(Scene s, String css) {
        s.getStylesheets().add(css);
    }

    public static void setSceneStyleSheets(Scene s) {
        setSceneStyleSheets(s, DEFAULT_THEME_CSS);
    }

    public static final Notifications notif(String title, String content) {
        return Notifications.create()
                .title(title)
                .text(content)
                .position(Pos.TOP_CENTER)
                .darkStyle();
    }

    public static final Notifications notif(String title, String content, Duration d) {
        return Notifications.create()
                .title(title)
                .text(content)
                .hideAfter(d)
                .position(Pos.TOP_CENTER)
                .darkStyle();
    }

    public static Label INFO_LABEL;

    public static void INFO(String s) {
        INFO_LABEL.setText(String.format("%s--%s", BBDateUtils.FDATE_LOGSTYLE.format(new Date()), s));
        BBLog.s(s);
    }

    public static void quitApplication() {
        BBUIHelper.runLater(5, () -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static String promptForInput(BBSuperController c, String title) {
        return FXMLInputDialogController.showDNotBlank(c, title);
    }

    public static void requestFocusFixed(Node tfModifStock) {
        runLater(50, () -> { tfModifStock.requestFocus(); });
    }

    protected static final <T> StringConverter<T> newStringConverter(final Class<T> clazz) {
        return new StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (clazz == Integer.class) {
                    return new IntegerStringConverter().toString((Integer) object);
                } else if (clazz == Double.class) {
                    return new DoubleStringConverter().toString((Double) object);
                } else if (clazz == String.class) {
                    return (String) object;
                }
                return "";
            }

            @Override
            public T fromString(String string) {
                if (clazz == Integer.class) {
                    return (T) new IntegerStringConverter().fromString(string);
                } else if (clazz == Double.class) {
                    return (T) new DoubleStringConverter().fromString(string);
                } else if (clazz == String.class) {
                    return (T) string;
                }
                return null;
            }
        };
    }

    public abstract static class TableValueMatcher<T> {

        protected ObservableValue<? extends Object> ov(Object o) {
            return new SimpleObjectProperty<>(o);
            /*
            if (o instanceof Integer) return new SimpleIntegerProperty((int) o);
            if (o instanceof Double) return new SimpleDoubleProperty((double) o);
            if (o instanceof String) return new SimpleStringProperty((String) o);
            if (o instanceof String) return new SimpleObjectProperty<>((String) o);*/
        }

        public abstract ObservableValue<? extends Object> findValue(int flatColIndx, T object, SimpleStringProperty emptyDummy);

        public abstract Comparator<?> findComparator(TableColumn t, int flatColIdx);
    }

    public static final void unzip(BBSuperController c, String progressTitle, File src, File dest,
            Callback<Void, Void> success, Callback<Exception, Void> failure) {
        try {
            final FXMLProgressDialogController pdc = (FXMLProgressDialogController) FXMLProgressDialogController.showProgressD(c, "");
            new Thread() {
                @Override
                public void run() {
                    try {
                        BBZipper.unzip(src.getPath(),
                                dest.getPath(), new BBZipper.TransferDelegate() {
                            @Override
                            public void onTransferUpdate(long transmitedBytes, long totalBytes, long deltaBytes, Object customObject) throws InterruptedException {
                                float progress = 100.0f * transmitedBytes / totalBytes;
                                updateProgress(pdc, String.format("%s %.02f%%", progressTitle, progress), progress / 100);
                            }

                            @Override
                            public void onTransferFailed(InputStream is, OutputStream os, Exception e) {
                                error(e);
                            }

                            @Override
                            public void onTransferFinished(InputStream is, OutputStream os) {
                                Platform.runLater(() -> {
                                    pdc.dismiss();
                                    success.call(null);
                                });
                            }
                        }, src.length());
                    } catch (Exception ex) {
                        error(ex);
                    }
                }

                void error(Exception e) {
                    Platform.runLater(() -> {
                        if (failure != null) {
                            pdc.dismiss();
                            failure.call(e);
                        } else {
                            pdc.dismiss();
                            c.criticalError(e);
                        }
                    });
                }

                void updateProgress(FXMLProgressDialogController d, String title, double ratio) {
                    d.updateProgress(ratio);
                    d.updateMessage(title);
                }
            }.start();
        } catch (Exception ex) {
            c.criticalError(ex);
        }
    }

    public static final void runLater(int delayMs, Runnable r) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(delayMs), (event) -> {
            r.run();
        }));
        timeline.play();
    }

    public static final File promptForFile(BBSuperController c, String title, String extTitle, String... exts) {
        for (int i = 0; i < exts.length; i++) {
            exts[i] = "*." + exts[i];
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(extTitle, exts));
        File selectedFile = fileChooser.showOpenDialog(c.stage.get());
        return selectedFile;
    }

    public static final List<File> promptForFiles(BBSuperController c, String title, String extTitle, String... exts) {
        for (int i = 0; i < exts.length; i++) {
            exts[i] = "*." + exts[i];
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(extTitle, exts));
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(c.stage.get());
        return selectedFile;
    }

    public static final File promptForDirectory(BBSuperController c, String title) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(title);
        File selectedFile = fileChooser.showDialog(c.stage.get());
        return selectedFile;
    }

    public static File promptChooserToSaveFile(BBSuperController c, String title, String extTitle, String ext) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(extTitle, "*." + ext));
        return fileChooser.showSaveDialog(c.stage.get());
    }

    public static final void saveNodeAsPng(BBSuperController c, Node n) {
        WritableImage image = n.snapshot(new SnapshotParameters(), null);

        File file = promptChooserToSaveFile(c, "Saving full cover photo PNG file...", "PNG File", "png");
        try {
            ImageIO.write(BBImgUtils.fromFXImageSafe(image), "png", file);
        } catch (IOException e) {
            c.criticalError(e);
        }
    }

    public static final WritableImage getImageFromNode(Parent n, boolean transparent) {
        return getImageFromNode(n, transparent, DEFAULT_THEME_CSS);
    }

    public static final WritableImage getImageFromNode(Parent n, boolean transparent, String themeCSS) {
        Scene s = new Scene(n);
        if (themeCSS != null) {
            setSceneStyleSheets(s, themeCSS);
        }
        SnapshotParameters sp = new SnapshotParameters();
        if (transparent) {
            s.setFill(Color.TRANSPARENT);
            sp.setFill(Color.TRANSPARENT);
        }
        WritableImage wi = n.snapshot(sp, null);
        s.setRoot(new HBox());
        return wi;
    }

    public static final WritableImage getImageFromNode(Parent n) {
        return getImageFromNode(n, false);
    }

    public static final BufferedImage getImageFromNodeDepadded(Parent n) {
        BufferedImage img = BBImgUtils.fromFXImageSafe(getImageFromNode(n, null));

        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;

        boolean done;
        done = false;
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                if (img.getRGB(i, j) != BBColor.I_TRANSPARENT) {
                    y1 = j;
                    done = true;
                    break;
                }
            }
            if (done) {
                break;
            }
        }
        done = false;
        for (int j = img.getHeight() - 1; j >= 0; j--) {
            for (int i = 0; i < img.getWidth(); i++) {
                if (img.getRGB(i, j) != BBColor.I_TRANSPARENT) {
                    y2 = j;
                    done = true;
                    break;
                }
            }
            if (done) {
                break;
            }
        }

        done = false;
        for (int j = 0; j < img.getWidth(); j++) {
            for (int i = 0; i < img.getHeight(); i++) {
                if (img.getRGB(j, i) != BBColor.I_TRANSPARENT) {
                    x1 = j;
                    done = true;
                    break;
                }
            }
            if (done) {
                break;
            }
        }
        done = false;
        for (int j = img.getWidth() - 1; j >= 0; j--) {
            for (int i = 0; i < img.getHeight(); i++) {
                if (img.getRGB(j, i) != BBColor.I_TRANSPARENT) {
                    x2 = j;
                    done = true;
                    break;
                }
            }
            if (done) {
                break;
            }
        }

        if (x1 == 0 && x2 == 0 && y1 == 0 && y2 == 0) {
            return null;
        }
        return img.getSubimage(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }

    public static final WritableImage getImageFromNode(Parent n, Color backgroundColor) {
        return getImageFromNode(n, backgroundColor, DEFAULT_THEME_CSS);
    }

    public static final WritableImage getImageFromNode(Parent n, Color backgroundColor, String themeCSS) {
        backgroundColor = backgroundColor == null ? Color.TRANSPARENT : backgroundColor;
        n.setStyle("-fx-background-color: " + BBColorUtils.getCssColorForColor(backgroundColor));
        Scene s = new Scene(n);
        if (themeCSS != null) {
            setSceneStyleSheets(s, themeCSS);
        }
        SnapshotParameters sp = new SnapshotParameters();
        s.setFill(backgroundColor);
        sp.setFill(backgroundColor);

        WritableImage wi = n.snapshot(sp, null);
        s.setRoot(new HBox());
        return wi;
    }

    public static final WritableImage getImageFromNode(Node n, Color backgroundColor) {
        String bg = backgroundColor == null ? "transparent;" : BBColorUtils.getCssColorForColor(backgroundColor);
        if (!(n instanceof Parent)) {
            VBox vb = new VBox(n);
            vb.setStyle("-fx-background-color: " + bg);
            n = vb;
        }
        return getImageFromNode((Parent) n, backgroundColor);
    }

    public static final WritableImage getImageFromFile(File f) throws IOException {
        return SwingFXUtils.toFXImage(ImageIO.read(f), null);
    }

    public static int toI(TextInputControl tic) {
        return Integer.parseInt(tic.getText());
    }

    public static int toI(Labeled l) {
        return Integer.parseInt(l.getText());
    }

    public static double toD(TextInputControl tic) {
        return Double.parseDouble(tic.getText());
    }

    public static double toD(Labeled l) {
        return Double.parseDouble(l.getText());
    }

    // PROGRESS
    final private static Map<BBSuperController, FXMLProgressDialogController> progresses = Collections.synchronizedMap(new HashMap<>());

    public static void showProgress(BBSuperController c, String title, double progress) {
        synchronized (progresses) {
            final Callb<FXMLProgressDialogController> lambda = (progC) -> {
                progC.updateTitle(title);
                progC.updateMessage(title);
                progC.updateProgress(progress);
            };
            FXMLProgressDialogController mProgress = progresses.get(c);
            if (mProgress == null) {
                try {
                    mProgress = (FXMLProgressDialogController) FXMLProgressDialogController.showProgressDTSafeAsync(c, title);
                    progresses.put(c, mProgress);
                    lambda.run(mProgress);
                } catch (Exception ex) {
                    c.criticalError(ex);
                    return;
                }
            }
            lambda.run(mProgress);
        }
    }

    public static void hideProgress(BBSuperController c) {
        synchronized (progresses) {
            FXMLProgressDialogController mProgress = progresses.get(c);
            if (mProgress != null) {
                mProgress.dismiss();
            }
            progresses.remove(c);
        }
    }

    public static void copyToClipboard(BBSuperController c, String s) {
        BBTools.copyToClipboard(s);
        c.notifyInfo("Copied to clipboard");
    }
}
