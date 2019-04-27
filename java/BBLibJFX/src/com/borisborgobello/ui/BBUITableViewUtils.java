/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.ui;

import com.borisborgobello.img.BBImgUtils;
import com.borisborgobello.ui.controllers.BBSuperController;
import com.borisborgobello.utils.BBLog;
import com.borisborgobello.utils.BBTools;
import com.borisborgobello.utils.Callb;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javax.imageio.ImageIO;

/**
 *
 * @author borisborgobello
 */
public class BBUITableViewUtils {
    
    public static final String S_GRAPHICS_NULL_ITEM = "###";
    public static final SimpleStringProperty S_GRAPHICS_NULL_PROP = new SimpleStringProperty(S_GRAPHICS_NULL_ITEM);

    public static ObservableList<TableColumn> flattenTableColumns(TableView table) {
        ObservableList<TableColumn> result = FXCollections.observableArrayList();
        for (TableColumn t : (ObservableList<TableColumn>) table.getColumns()) {
            flattenColumn(result, t);
        }
        return result;
    }

    private static <T> void flattenColumn(ObservableList<TableColumn> result, TableColumn t) {
        if (t.getColumns() == null || t.getColumns().isEmpty()) {
            result.add(t);
        } else {
            for (TableColumn tc : (ObservableList<TableColumn>) t.getColumns()) {
                flattenColumn(result, tc);
            }
        }
    }

    public static void centerColumn(TableColumn tc) {
        tc.setStyle("-fx-alignment: CENTER;");
    }

    public static void centerTable(TableView table) {
        for (TableColumn tc : flattenTableColumns(table)) {
            centerColumn(tc);
        }
    }

    public static final <T> void configureButtonCellFactory(TableView tv, int flatIdxCol, final String buttonImagePath, int imageHeight, int imageWidth, boolean isByteArray, final Callback<T, Object> c) {
        Callback<TableColumn<T, String>, TableCell<T, String>> deleteCellFactory
                = //
                new Callback<TableColumn<T, String>, TableCell<T, String>>() {
            @Override
            public TableCell call(final TableColumn<T, String> param) {
                final ImageView iv = new ImageView();
                iv.setFitHeight(imageHeight);
                iv.setFitWidth(imageWidth);
                iv.setPreserveRatio(false);
                final Button btn = new Button("", iv);

                boolean isStaticImage = !BBTools.isEmpty(buttonImagePath);
                if (isStaticImage) {
                    iv.setImage(new Image(buttonImagePath));
                }

                final TableCell<T, Object> cell = new TableCell<T, Object>() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        if (((item instanceof String) && ((String) item).equals(S_GRAPHICS_NULL_ITEM))
                                || empty) {
                            btn.setOnAction(null);
                            setGraphic(null);
                        } else {
                            if (!isStaticImage) {
                                if (item == null) {
                                    iv.setImage(null);
                                } else {
                                    try {
                                        if (!isByteArray) {
                                            iv.setImage(new Image((String) item));
                                        } else {
                                            iv.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream((byte[]) item)), null));
                                        }
                                    } catch (Exception e) {
                                        BBLog.s("Failed to load image " + item);
                                    }
                                }
                            }

                            btn.setOnAction((ActionEvent event)
                                    -> {
                                T product = getTableView().getItems().get(getIndex());
                                c.call(product);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(deleteCellFactory);
    }

    public static final <T> void configureButtonCellFactory(TableView tv, int flatIdxCol, final String buttonTitle, final Callback<T, Object> c) {
        Callback<TableColumn<T, String>, TableCell<T, String>> deleteCellFactory
                = //
                new Callback<TableColumn<T, String>, TableCell<T, String>>() {
            @Override
            public TableCell call(final TableColumn<T, String> param) {
                final TableCell<T, String> cell = new TableCell<T, String>() {
                    final Button btn = new Button(buttonTitle);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                            btn.setOnAction(null);
                        } else {
                            btn.setOnAction((ActionEvent event)
                                    -> {
                                T product = getTableView().getItems().get(getIndex());
                                c.call(product);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(deleteCellFactory);
    }

    public static final <T> void configureButtonCellFactory(TableView tv, int flatIdxCol, final String buttonTitle, final Callb<T> c) {
        configureButtonCellFactory(tv, flatIdxCol, buttonTitle, new Callback<T, Object>() {
            @Override
            public Object call(T param) {
                c.run(param);
                return null;
            }
        });
    }

    public static final <T> void configureCellValueFactory(BBSuperController ctl, TableView tv, BBUIHelper.TableValueMatcher a) throws Exception {
        //checkTableValueMatcher(tv, a);

        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        final SimpleStringProperty emptyDummy = new SimpleStringProperty("");

        for (int i = 0; i < columns.size(); i++) {
            TableColumn t = columns.get(i);
            Comparator c = a.findComparator(t, i);
            if (c != null) {
                t.setComparator(c);
            }
        }

        final Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> c = new Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<T, String> p) {
                try {
                    final int col = columns.indexOf(p.getTableColumn());
                    final T ap = p.getValue();
                    return a.findValue(col, ap, emptyDummy);
                } catch (Exception ex) {
                    ctl.criticalError(ex);
                }
                return new SimpleStringProperty("");
            }
        };
        for (int i = 0; i < columns.size(); i++) {
            ((TableColumn) columns.get(i)).setCellValueFactory(c);
        }
    }

    public static final <T> void configureDualBackgroundFactory(TableView tv, int flatIdxCol) {
        Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory
                = new Callback<TableColumn<Object, Object>, TableCell<Object, Object>>() {
            @Override
            public TableCell call(TableColumn p) {
                //Set up the ImageView
                final HBox hbox = new HBox();
                final Region i1 = new Region();
                final Region i2 = new Region();
                HBox.setHgrow(i1, Priority.ALWAYS);
                HBox.setHgrow(i2, Priority.ALWAYS);
                hbox.getChildren().addAll(i1, i2);

                TableCell cell = new TableCell<Object, Object>() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        //super.updateItem(item, empty);
                        //setText(empty ? null : getString());
                        if (empty || item == null) {
                            hbox.setVisible(false);
                            return;
                        } else {
                            hbox.setVisible(true);
                        }

                        try {
                            String[] colors = ((String) item).split("[|]");
                            if (colors.length > 1) {
                                i1.setStyle(String.format("-fx-background-color: #%s;", colors[0]));
                                i2.setStyle(String.format("-fx-background-color: #%s;", colors[1]));
                                hbox.setStyle("-fx-background-color: #00000000;");
                            } else {
                                hbox.setStyle(String.format("-fx-background-color: #%s;", item));
                                i1.setStyle("-fx-background-color: #00000000;");
                                i2.setStyle("-fx-background-color: #00000000;");
                            }
                        } catch (Exception e) {
                            BBLog.s("Failed to load image " + item);
                            //e.printStackTrace();
                            hbox.setVisible(false);
                        }
                    }
                };
                cell.setPadding(Insets.EMPTY);
                cell.setGraphic(hbox);
                return cell;
            }
        };

        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(cellFactory);
    }

    // default 135/100 for size
    public static final <T> void configureImageCellFactory(TableView tv, int flatIdxCol, final int width, final int height, boolean isByteArray) {
        Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory
                = new Callback<TableColumn<Object, Object>, TableCell<Object, Object>>() {
            public TableCell call(TableColumn p) {
                //Set up the ImageView
                final ImageView imageview = new ImageView();
                imageview.setFitHeight(height);
                imageview.setFitWidth(width);

                TableCell cell = new TableCell<Object, Object>() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        //super.updateItem(item, empty);
                        //setText(empty ? null : getString());
                        if (empty || item == null) {
                            try {
                                imageview.getImage().cancel();
                            } catch (Exception e) {
                            }
                            imageview.setImage(null);
                            imageview.setFitHeight(0);
                            imageview.setVisible(false);
                            return;
                        }
                        imageview.setFitHeight(height);
                        try {
                            if (!isByteArray) {
                                try {
                                    imageview.getImage().cancel();
                                } catch (Exception e) {
                                }
                                imageview.setImage(new Image((String) item, true));
                            } else {
                                try {
                                    imageview.getImage().cancel();
                                } catch (Exception e) {
                                }
                                imageview.setImage(BBImgUtils.imageFromData((byte[]) item));
                            }
                            imageview.setVisible(true);
                        } catch (Exception e) {
                            BBLog.s("Failed to load image " + item);
                            //e.printStackTrace();
                        }
                    }
                };
                cell.setGraphic(imageview);
                return cell;
            }
        };

        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(cellFactory);
    }

    public static final <T> void configureColorCellFactory(TableView tv, int flatIdxCol) {
        Callback<TableColumn<Object, String>, TableCell<Object, String>> cellFactory
                = new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
            public TableCell call(TableColumn p) {

                TableCell cell = new TableCell<Object, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setStyle("-fx-background-color:" + getString());
                    }

                    private String getString() {
                        return getItem() == null ? "" : getItem().toString();
                    }
                };
                return cell;
            }
        };

        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(cellFactory);
    }

    public static final <T> void configureProgressCellFactory(TableView tv, int flatIdxCol, String progressColor) {
        Callback<TableColumn<Object, String>, TableCell<Object, String>> cellFactory
                = new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
            public TableCell call(TableColumn p) {
                //Set up the ImageView

                final ProgressBar pb = new ProgressBar();
                pb.setPrefWidth(1000);
                if (progressColor != null) {
                    pb.setStyle("-fx-accent: #" + progressColor);
                }
                TableCell cell = new TableCell<Object, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        //super.updateItem(item, empty);
                        //setText(empty ? null : getString());
                        if (empty || item == null) {
                            pb.setProgress(0);
                            setGraphic(null);
                            return;
                        }
                        try {
                            pb.setProgress(Double.parseDouble(item));
                        } catch (Exception e) {
                            BBLog.s("Failed to load image " + item);
                        }
                        setGraphic(pb);
                    }
                };
                return cell;
            }
        };

        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(cellFactory);
    }

    public static interface DynamiqueButtonCallbacks<T> {

        boolean shouldDiplayButton(int idx, T item);

        String buttonTitle(int idx, T item);

        void buttonClicked(int idx, T item);

        void setupButton(Button btn, T item);
    }

    public static final <T> void configureDynamiqueButtonCellFactory(TableView tv, int flatIdxCol, final DynamiqueButtonCallbacks<T> c) {
        Callback<TableColumn<T, String>, TableCell<T, String>> deleteCellFactory
                = //
                new Callback<TableColumn<T, String>, TableCell<T, String>>() {
            @Override
            public TableCell call(final TableColumn<T, String> param) {
                final TableCell<T, String> cell = new TableCell<T, String>() {
                    final Button btn = new Button();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        int idx = getIndex();
                        T data;
                        try {
                            data = getTableView().getItems().get(idx);
                            if (!c.shouldDiplayButton(idx, data)) {
                                throw new Exception("");
                            }
                        } catch (Exception e) {
                            setGraphic(null);
                            setText(null);
                            btn.setOnAction(null);
                            return;
                        }

                        btn.setText(c.buttonTitle(idx, data));
                        btn.setOnAction((ActionEvent event)
                                -> {
                            c.buttonClicked(idx, data);
                        });
                        c.setupButton(btn, data);
                        setGraphic(btn);
                        setText(null);
                    }
                };
                return cell;
            }
        };
        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(deleteCellFactory);
    }

    public static interface DynamiqueImageCFCallbacks<T> {

        boolean shouldDisplayImage(int idx, T item);

        int getWidth(int idx, T item);

        int getHeight(int idx, T item);

        String getUrl(int idx, T item);
    }

    public static final <T> void configureDynamiqueImageCF(TableView tv, int flatIdxCol, final DynamiqueImageCFCallbacks<T> c) {
        Callback<TableColumn<T, Object>, TableCell<T, Object>> cellFactory
                = new Callback<TableColumn<T, Object>, TableCell<T, Object>>() {
            public TableCell call(TableColumn p) {
                //Set up the ImageView
                final ImageView imageview = new ImageView();

                TableCell cell = new TableCell<T, Object>() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        int idx = getIndex();
                        T data;
                        String url;
                        try {
                            data = getTableView().getItems().get(idx);
                            if (!c.shouldDisplayImage(idx, data)) {
                                throw new Exception("");
                            }
                            url = c.getUrl(idx, data);
                            if (url == null) {
                                throw new Exception("");
                            }
                        } catch (Exception e) {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        imageview.setFitHeight(c.getHeight(idx, data));
                        imageview.setFitWidth(c.getWidth(idx, data));

                        imageview.setImage(new Image(url, true));

                        setGraphic(imageview);
                        setText(null);
                    }
                };
                cell.setGraphic(imageview);
                return cell;
            }
        };

        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(cellFactory);
    }

    public static final <T, CONTENT_TYPE> TableColumn configureTextFieldCellFactory(TableView tv, int flatIdxCol, Class<CONTENT_TYPE> clazz,
            EventHandler<TableColumn.CellEditEvent<T, CONTENT_TYPE>> editCommitCallb) {
        Callback<TableColumn<T, CONTENT_TYPE>, TextFieldTableCell<T, CONTENT_TYPE>> cf = new Callback<TableColumn<T, CONTENT_TYPE>, TextFieldTableCell<T, CONTENT_TYPE>>() {
            @Override
            public TextFieldTableCell<T, CONTENT_TYPE> call(TableColumn<T, CONTENT_TYPE> param) {
                final TextFieldTableCell<T, CONTENT_TYPE> cell = new TextFieldTableCell<T, CONTENT_TYPE>(BBUIHelper.newStringConverter(clazz)) {
                    @Override
                    public void updateSelected(boolean selected) {
                        super.updateSelected(selected);
                    }
                };
                return cell;
            }
        };
        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        TableColumn c = columns.get(flatIdxCol);
        c.setOnEditCommit(editCommitCallb);
        c.setCellFactory(cf);
        c.setEditable(true);
        tv.setEditable(true);
        return c;
    }

    public static interface DynamiqueTableCellCallbacks<T> {

        boolean dtcIsEmptyCell(int rowIdx, T rowItem, Object cellValue, boolean empty);

        boolean dtcUpdate(DynamiqueTableCell<T> cell, int idx, T rowItem, Object cellValue, boolean empty) throws Exception;
    }

    public static final class EmptyException extends RuntimeException {
    }

    public static class DynamiqueTableCell<T> extends TableCell<T, Object> {

        final static Font btnSmall = new Font("Arial", 9);

        final DynamiqueTableCellCallbacks<T> del;

        public DynamiqueTableCell(DynamiqueTableCellCallbacks<T> del) {
            this.del = del;
        }

        public void makeEmpty() {
            setGraphic(null);
            setText(null);
        }

        public boolean loadImage(int width, int height, String url) {
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(height);
            imageview.setFitWidth(width);
            imageview.setImage(new Image(url, true));
            setGraphic(imageview);
            setText(null);
            return true;
        }

        public CheckBox loadCheckBox(boolean checked) {
            final CheckBox cb = new CheckBox();
            cb.setSelected(checked);
            setGraphic(cb);
            setText(null);
            return cb;
        }

        public Button loadSmallButton(String title) {
            final Button btn = new Button(title);
            btn.setFont(btnSmall);
            setGraphic(btn);
            setText(null);
            return btn;
        }

        public Button loadButton(String title) {
            final Button btn = new Button(title);
            setGraphic(btn);
            setText(null);
            return btn;
        }

        public Label loadText(String title) {
            final Label btn = new Label(title);
            setGraphic(btn);
            setText(null);
            return btn;
        }

        public boolean loadImageFromBytes(int width, int height, byte[] data) throws IOException {
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(height);
            imageview.setFitWidth(width);
            imageview.setImage(BBImgUtils.imageFromData(data));
            setGraphic(imageview);
            setText(null);
            return true;
        }

        public boolean loadBackgroundColor(String c) {
            if (c.startsWith("#")) {
                c = c.substring(1);
            }
            this.setStyle(String.format("-fx-background-color: #%s;", c));
            return true;
        }

        public boolean loadBackgroundColors(String... colorHexa) {
            final HBox hbox = new HBox();
            for (String c : colorHexa) {
                if (c.startsWith("#")) {
                    c = c.substring(1);
                }
                final Region i1 = new Region();
                HBox.setHgrow(i1, Priority.ALWAYS);
                hbox.getChildren().add(i1);
                i1.setStyle(String.format("-fx-background-color: #%s;", c));
            }
            setPadding(Insets.EMPTY);
            setGraphic(hbox);
            return true;
        }

        @Override
        public void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            int idx = getIndex();
            T data;
            try {
                data = getTableView().getItems().get(idx);
                if (del.dtcIsEmptyCell(idx, data, item, empty)) {
                    throw new EmptyException();
                }
                if (!del.dtcUpdate(this, idx, data, item, empty)) {
                    throw new Exception();
                }
            } catch (EmptyException e) {
                makeEmpty();
            } catch (IndexOutOfBoundsException e) {
                makeEmpty();
            } catch (Exception e) {
                //e.printStackTrace();
                makeEmpty();
            }
        }
    }

    public static final <T> void configureDynamiqueSuperCell(TableView tv, int flatIdxCol, final DynamiqueTableCellCallbacks<T> c) {
        Callback<TableColumn<T, Object>, TableCell<T, Object>> cellFactory
                = new Callback<TableColumn<T, Object>, TableCell<T, Object>>() {
            public TableCell call(TableColumn p) {
                DynamiqueTableCell<T> cell = new DynamiqueTableCell<>(c);
                return cell;
            }
        };

        final ObservableList<TableColumn> columns = flattenTableColumns(tv);
        columns.get(flatIdxCol).setCellFactory(cellFactory);
    }
}
